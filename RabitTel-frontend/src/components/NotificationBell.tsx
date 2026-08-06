import { useEffect, useRef, useState, useCallback } from 'react'
import { useAuth } from '../auth/AuthContext'
import { notificationService } from '../services'
import type { NotificationDeliveryResponse } from '../types'

// ─── helpers ─────────────────────────────────────────────────────────────────

/** Returns a friendly French label for each notification type */
function typeLabel(type: string): string {
  const map: Record<string, string> = {
    CREATE_USER:        'Compte créé',
    RESET_PASSWORD:     'Mot de passe réinitialisé',
    CHANGE_PASSWORD:    'Mot de passe modifié',
    ACCOUNT_LOCKED:     'Compte verrouillé',
    ACCOUNT_UNLOCKED:   'Compte déverrouillé',
    ACCOUNT_ACTIVATED:  'Compte activé',
    ACCOUNT_DEACTIVATED:'Compte désactivé',
    LINE_CREATED:       'Ligne créée',
    LINE_UPDATED:       'Ligne mise à jour',
    LINE_DELETED:       'Ligne supprimée',
    LINE_ASSIGNED:      'Ligne assignée',
    LINE_UNASSIGNED:    'Ligne désassignée',
    CONTRACT_CREATED:   'Contrat créé',
    CONTRACT_UPDATED:   'Contrat mis à jour',
    CONTRACT_EXPIRED:   'Contrat expiré',
    CONTRACT_EXPIRING:  'Contrat expirant bientôt',
    IMPORT_STARTED:     'Import démarré',
    IMPORT_COMPLETED:   'Import terminé',
    IMPORT_FAILED:      'Import échoué',
    SYSTEM_NOTIFICATION:'Notification système',
  }
  return map[type] ?? type
}

/** Status dot color */
function statusColor(status: string): string {
  if (status === 'SENT')    return '#16a34a'
  if (status === 'FAILED')  return '#dc2626'
  if (status === 'PENDING') return '#d97706'
  return '#6b7280'
}

/** Format ISO date to a short French string */
function formatDate(iso: string | null): string {
  if (!iso) return ''
  const d = new Date(iso)
  const now = new Date()
  const diffMs = now.getTime() - d.getTime()
  const diffMin = Math.floor(diffMs / 60000)
  if (diffMin < 1)   return "À l'instant"
  if (diffMin < 60)  return `Il y a ${diffMin} min`
  const diffH = Math.floor(diffMin / 60)
  if (diffH < 24)    return `Il y a ${diffH}h`
  const diffD = Math.floor(diffH / 24)
  if (diffD < 7)     return `Il y a ${diffD}j`
  return d.toLocaleDateString('fr-FR', { day: 'numeric', month: 'short' })
}

// ─── Component ────────────────────────────────────────────────────────────────

/**
 * Bell icon in the topbar.
 * Polls the notification-service every 30 seconds for IN_APP deliveries
 * addressed to the current user's email.
 * Shows a red badge with the count of unread notifications.
 * Click → opens a dropdown panel with the full list.
 */
export default function NotificationBell() {
  const { user } = useAuth()
  const [deliveries, setDeliveries] = useState<NotificationDeliveryResponse[]>([])
  const [open, setOpen]   = useState(false)
  const [loading, setLoading] = useState(false)
  const panelRef = useRef<HTMLDivElement>(null)

  // ── Fetch IN_APP deliveries for current user ──────────────────────────────
  const fetchNotifications = useCallback(async () => {
    if (!user?.email) return
    try {
      setLoading(true)
      const notifications = await notificationService.getByRecipient(user.email)
      // Flatten all IN_APP deliveries targeting this user
      const inApp = notifications
        .flatMap(n => n.deliveries ?? [])
        .filter(d => d.channel === 'IN_APP' && d.recipient === user.email)
        // Most recent first
        .sort((a, b) => {
          const ta = a.sentAt ? new Date(a.sentAt).getTime() : 0
          const tb = b.sentAt ? new Date(b.sentAt).getTime() : 0
          return tb - ta
        })
      setDeliveries(inApp)
    } catch {
      // Notification service may not be running — fail silently
    } finally {
      setLoading(false)
    }
  }, [user?.email])

  // Initial fetch + polling every 30 s
  useEffect(() => {
    fetchNotifications()
    const id = setInterval(fetchNotifications, 30_000)
    return () => clearInterval(id)
  }, [fetchNotifications])

  // Close panel when clicking outside
  useEffect(() => {
    function handleClick(e: MouseEvent) {
      if (panelRef.current && !panelRef.current.contains(e.target as Node)) {
        setOpen(false)
      }
    }
    if (open) document.addEventListener('mousedown', handleClick)
    return () => document.removeEventListener('mousedown', handleClick)
  }, [open])

  // ── Mark as read ──────────────────────────────────────────────────────────
  const markRead = async (deliveryId: string) => {
    try {
      await notificationService.markAsRead(deliveryId)
      setDeliveries(prev =>
        prev.map(d => d.id === deliveryId ? { ...d, read: true, readAt: new Date().toISOString() } : d)
      )
    } catch {
      // ignore
    }
  }

  const markAllRead = async () => {
    const unread = deliveries.filter(d => !d.read)
    await Promise.allSettled(unread.map(d => markRead(d.id)))
  }

  const unreadCount = deliveries.filter(d => !d.read).length

  // ─────────────────────────────────────────────────────────────────────────
  return (
    <div ref={panelRef} style={{ position: 'relative', display: 'inline-block' }}>

      {/* Bell button */}
      <button
        onClick={() => setOpen(v => !v)}
        title="Notifications"
        style={{
          position: 'relative',
          background: 'none',
          border: 'none',
          cursor: 'pointer',
          fontSize: 20,
          padding: '2px 4px',
          lineHeight: 1,
          color: '#374151',
        }}
      >
        🔔
        {unreadCount > 0 && (
          <span style={{
            position: 'absolute',
            top: -4,
            right: -4,
            background: '#dc2626',
            color: '#fff',
            borderRadius: '50%',
            fontSize: 10,
            fontWeight: 700,
            minWidth: 16,
            height: 16,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '0 3px',
            lineHeight: 1,
          }}>
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown panel */}
      {open && (
        <div style={{
          position: 'absolute',
          top: 'calc(100% + 8px)',
          right: 0,
          width: 360,
          maxHeight: 480,
          background: '#fff',
          border: '1px solid #e5e7eb',
          borderRadius: 8,
          boxShadow: '0 10px 30px rgba(0,0,0,0.12)',
          zIndex: 1000,
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden',
        }}>

          {/* Header */}
          <div style={{
            padding: '12px 16px',
            borderBottom: '1px solid #f3f4f6',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            background: '#f9fafb',
          }}>
            <span style={{ fontWeight: 700, fontSize: 14, color: '#111827' }}>
              🔔 Notifications {unreadCount > 0 && <span style={{ color: '#dc2626' }}>({unreadCount} non lues)</span>}
            </span>
            <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
              {unreadCount > 0 && (
                <button
                  onClick={markAllRead}
                  style={{
                    fontSize: 11,
                    color: '#2563eb',
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    padding: 0,
                    textDecoration: 'underline',
                  }}
                >
                  Tout marquer lu
                </button>
              )}
              <button
                onClick={fetchNotifications}
                title="Actualiser"
                style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 14, color: '#6b7280', padding: 0 }}
              >
                ↻
              </button>
            </div>
          </div>

          {/* List */}
          <div style={{ overflowY: 'auto', flex: 1 }}>
            {loading && deliveries.length === 0 && (
              <div style={{ padding: '24px 16px', textAlign: 'center', color: '#9ca3af', fontSize: 13 }}>
                Chargement…
              </div>
            )}

            {!loading && deliveries.length === 0 && (
              <div style={{ padding: '32px 16px', textAlign: 'center' }}>
                <div style={{ fontSize: 32, marginBottom: 8 }}>🔕</div>
                <p style={{ color: '#6b7280', fontSize: 13, margin: 0 }}>Aucune notification</p>
              </div>
            )}

            {deliveries.map(delivery => (
              <div
                key={delivery.id}
                onClick={() => { if (!delivery.read) markRead(delivery.id) }}
                style={{
                  padding: '12px 16px',
                  borderBottom: '1px solid #f3f4f6',
                  background: delivery.read ? '#fff' : '#eff6ff',
                  cursor: delivery.read ? 'default' : 'pointer',
                  transition: 'background 0.15s',
                  display: 'flex',
                  gap: 10,
                  alignItems: 'flex-start',
                }}
              >
                {/* Status dot */}
                <span style={{
                  width: 8,
                  height: 8,
                  borderRadius: '50%',
                  background: statusColor(delivery.status),
                  marginTop: 5,
                  flexShrink: 0,
                }} />

                {/* Content */}
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 8 }}>
                    <span style={{
                      fontWeight: delivery.read ? 400 : 600,
                      fontSize: 13,
                      color: '#111827',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      whiteSpace: 'nowrap',
                    }}>
                      {delivery.subject || typeLabel('SYSTEM_NOTIFICATION')}
                    </span>
                    {!delivery.read && (
                      <span style={{
                        width: 8,
                        height: 8,
                        borderRadius: '50%',
                        background: '#2563eb',
                        flexShrink: 0,
                        marginTop: 3,
                      }} />
                    )}
                  </div>

                  {delivery.body && (
                    <p style={{
                      fontSize: 12,
                      color: '#6b7280',
                      margin: '3px 0 0 0',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical',
                    }}>
                      {delivery.body}
                    </p>
                  )}

                  <span style={{ fontSize: 11, color: '#9ca3af', marginTop: 4, display: 'block' }}>
                    {formatDate(delivery.sentAt)}
                    {delivery.status === 'FAILED' && (
                      <span style={{ color: '#dc2626', marginLeft: 6 }}>✕ Échec d'envoi</span>
                    )}
                  </span>
                </div>
              </div>
            ))}
          </div>

          {/* Footer */}
          {deliveries.length > 0 && (
            <div style={{
              padding: '8px 16px',
              borderTop: '1px solid #f3f4f6',
              background: '#f9fafb',
              textAlign: 'center',
              fontSize: 12,
              color: '#9ca3af',
            }}>
              {deliveries.length} notification{deliveries.length > 1 ? 's' : ''}
            </div>
          )}
        </div>
      )}
    </div>
  )
}

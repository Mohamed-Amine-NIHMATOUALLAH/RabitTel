import { Navigate, useLocation } from 'react-router-dom'
import { useAuth, isAdmin } from './AuthContext'
import type { ReactNode } from 'react'

interface Props {
  children: ReactNode
  /** If true, only ADMIN can access. MEMBER sees an error. */
  adminOnly?: boolean
}

export default function ProtectedRoute({ children, adminOnly = false }: Props) {
  const { user, token, ready } = useAuth()
  const loc = useLocation()

  // ── Loading ──────────────────────────────────────────────────────────────
  if (!ready) {
    return (
      <div style={{ padding: 40, textAlign: 'center' }}>
        <span className="spinner" />Chargement…
      </div>
    )
  }

  // ── Not authenticated ─────────────────────────────────────────────────────
  if (!token || !user) {
    return (
      <Navigate
        to={'/login?redirect=' + encodeURIComponent(loc.pathname + loc.search)}
        replace
      />
    )
  }

  // ── Inactive account ──────────────────────────────────────────────────────
  if (!user.isActive) {
    return (
      <div style={{ padding: 40, textAlign: 'center' }}>
        <div className="error-box" style={{ maxWidth: 400, margin: '0 auto' }}>
          Votre compte est désactivé. Contactez un administrateur.
        </div>
        <LogoutButton />
      </div>
    )
  }

  // ── Force password change on first login ─────────────────────────────────
  // Only the initial system account (admin@gmail.com) is exempt.
  // Every other account — whether ADMIN or MEMBER — must change their
  // password on first login before accessing any page.
  const SYSTEM_ADMIN_EMAIL = 'admin@gmail.com'
  const isSystemAdmin = user.email === SYSTEM_ADMIN_EMAIL
  const isOnForcedPage = loc.pathname === '/change-password-forced'

  if (user.firstLogin && !isSystemAdmin && !isOnForcedPage) {
    return <Navigate to="/change-password-forced" replace />
  }

  // ── Admin-only guard ──────────────────────────────────────────────────────
  if (adminOnly && !isAdmin(user)) {
    return (
      <div style={{ padding: 40, textAlign: 'center' }}>
        <div className="error-box" style={{ maxWidth: 400, margin: '0 auto' }}>
          Accès réservé aux administrateurs.
        </div>
      </div>
    )
  }

  return <>{children}</>
}

function LogoutButton() {
  const { logout } = useAuth()
  const go = () => {
    logout()
    location.href = '/login'
  }
  return (
    <button className="primary" style={{ marginTop: 16 }} onClick={go}>
      Retour à la connexion
    </button>
  )
}

import { useEffect, useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { userService } from '../services'
import type { UserResponse } from '../types'
import { UserRole } from '../types'
import ConfirmDialog from '../components/ConfirmDialog'
import ErrorMsg from '../components/ErrorMsg'
import { isAdmin } from '../auth/AuthContext'
import { useAuth } from '../auth/AuthContext'

const createSchema = z.object({
  firstName: z.string().min(2, 'Min 2 caractères').max(50),
  lastName: z.string().min(2, 'Min 2 caractères').max(50),
  email: z.string().email('Email invalide'),
  phoneNumber: z.string().regex(/^(\+212|0)[5-7][0-9]{8}$/, 'Format téléphone MAR (+212 ou 0 + 5/6/7 + 8 chiffres)'),
  role: z.nativeEnum(UserRole, { required_error: 'Rôle requis' }),
})

const updateSchema = z.object({
  role: z.nativeEnum(UserRole),
  isActive: z.boolean(),
})

type CreateF = z.infer<typeof createSchema>
type UpdateF = z.infer<typeof updateSchema>

export default function UsersPage() {
  const qc = useQueryClient()
  const { user: currentUser } = useAuth()
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(10)
  const [showCreate, setShowCreate] = useState(false)
  const [editing, setEditing] = useState<UserResponse | null>(null)
  const [confirmState, setConfirmState] = useState<null | { msg: string; fn: () => void }>(null)
  const [err, setErr] = useState<unknown>(null)

  const q = useQuery({
    queryKey: ['users', page, size],
    queryFn: () => userService.getAll({ page, size, sortBy: 'createdAt', sortDirection: 'desc' }),
  })

  const createMut = useMutation({
    mutationFn: (d: CreateF) => userService.create(d),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['users'] }); setShowCreate(false) },
    onError: (e) => setErr(e),
  })
  const updateMut = useMutation({
    mutationFn: ({ id, d }: { id: string; d: UpdateF }) => userService.update(id, d),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['users'] }); setEditing(null) },
    onError: (e) => setErr(e),
  })
  const activateMut = useMutation({
    mutationFn: (id: string) => userService.activate(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['users'] }),
    onError: (e) => setErr(e),
  })
  const deactivateMut = useMutation({
    mutationFn: (id: string) => userService.deactivate(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['users'] }),
    onError: (e) => setErr(e),
  })
  const resetPwdMut = useMutation({
    mutationFn: (id: string) => userService.resetPassword(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['users'] }),
    onError: (e) => setErr(e),
  })

  const roleBadge = (r: UserRole) => (
    <span className={'badge ' + (r === UserRole.ADMIN ? 'purple' : 'blue')}>{r}</span>
  )
  const statusBadge = (a: boolean) => (
    <span className={'badge ' + (a ? 'green' : 'gray')}>{a ? 'Actif' : 'Désactivé'}</span>
  )

  useEffect(() => { setErr(null) }, [showCreate, editing])

  return (
    <div>
      <div className="flex-between" style={{ marginBottom: 12 }}>
        <h2 style={{ margin: 0 }}>Utilisateurs</h2>
        {isAdmin(currentUser) ? (
          <button className="primary" onClick={() => setShowCreate(true)}>
            + Nouvel utilisateur
          </button>
        ) : null}
      </div>

      <ErrorMsg error={err} />

      <div className="toolbar">
        <div className="field" style={{ marginBottom: 0 }}>
          <label>Page</label>
          <select value={size} onChange={(e) => { setSize(Number(e.target.value)); setPage(0) }}>
            {[5, 10, 20, 50].map(n => <option key={n} value={n}>{n}</option>)}
          </select>
        </div>
      </div>

      {q.isLoading ? <p><span className="spinner"></span>Chargement…</p> : null}
      {q.data ? (
        <>
          <table>
            <thead>
              <tr>
                <th>Nom d'utilisateur</th>
                <th>Nom complet</th>
                <th>Email</th>
                <th>Téléphone</th>
                <th>Rôle</th>
                <th>Statut</th>
                <th>Créé le</th>
                <th>Dernière connexion</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {q.data.content.length === 0 ? (
                <tr><td colSpan={9} className="center muted">Aucun utilisateur</td></tr>
              ) : q.data.content.map(u => (
                <tr key={u.id}>
                  <td>{u.username}</td>
                  <td>{u.firstName} {u.lastName}</td>
                  <td>{u.email}</td>
                  <td>{u.phoneNumber}</td>
                  <td>{roleBadge(u.role)}</td>
                  <td>{statusBadge(u.isActive)}</td>
                  <td>{new Date(u.createdAt).toLocaleString('fr-FR')}</td>
                  <td>{u.lastLoginAt ? new Date(u.lastLoginAt).toLocaleString('fr-FR') : <span className="muted">Jamais</span>}</td>
                  <td>
                    <div className="actions">
                      <button className="small" onClick={() => setEditing(u)}>Modifier</button>
                      {u.isActive
                        ? <button className="small" onClick={() => setConfirmState({ msg: `Désactiver ${u.username} ?`, fn: () => deactivateMut.mutate(u.id) })}>Désactiver</button>
                        : <button className="small primary" onClick={() => activateMut.mutate(u.id)}>Activer</button>}
                      <button className="small" onClick={() => setConfirmState({ msg: `Réinitialiser le mot de passe de ${u.username} ? (un mot de passe aléatoire sera généré)`, fn: () => resetPwdMut.mutate(u.id) })}>
                        Reset MDP
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div style={{ marginTop: 10, display: 'flex', alignItems: 'center', gap: 8 }}>
            <span className="muted">
              Page {q.data.number + 1} / {q.data.totalPages || 1} — {q.data.totalElements} utilisateur(s)
            </span>
            <div style={{ marginLeft: 'auto', display: 'flex', gap: 4 }}>
              <button className="small" disabled={q.data.first} onClick={() => setPage(p => p - 1)}>Précédent</button>
              <button className="small" disabled={q.data.last} onClick={() => setPage(p => p + 1)}>Suivant</button>
            </div>
          </div>
        </>
      ) : null}

      {confirmState && (
        <ConfirmDialog
          message={confirmState.msg}
          onConfirm={() => { confirmState.fn(); setConfirmState(null) }}
          onCancel={() => setConfirmState(null)}
        />
      )}

      {showCreate && <CreateModal onClose={() => setShowCreate(false)} onSubmit={(d) => createMut.mutate(d)} loading={createMut.isPending} />}
      {editing && <UpdateModal user={editing} onClose={() => setEditing(null)} onSubmit={(d) => updateMut.mutate({ id: editing.id, d })} loading={updateMut.isPending} />}
    </div>
  )
}

function CreateModal({ onClose, onSubmit, loading }: { onClose: () => void; onSubmit: (d: CreateF) => void; loading: boolean }) {
  const { register, handleSubmit, formState: { errors } } = useForm<CreateF>({
    resolver: zodResolver(createSchema),
    defaultValues: { role: UserRole.MEMBER },
  })
  return (
    <div className="modal-bg" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <header>
          <h3>Nouvel utilisateur</h3>
          <button className="ghost small" onClick={onClose}>✕</button>
        </header>
        <form className="body" onSubmit={handleSubmit(onSubmit)}>
          <div className="grid-2">
            <div className="field">
              <label>Prénom *</label>
              <input style={{ width: '100%' }} {...register('firstName')} />
              {errors.firstName && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errors.firstName.message}</p>}
            </div>
            <div className="field">
              <label>Nom *</label>
              <input style={{ width: '100%' }} {...register('lastName')} />
              {errors.lastName && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errors.lastName.message}</p>}
            </div>
          </div>
          <div className="field">
            <label>Email *</label>
            <input type="email" style={{ width: '100%' }} {...register('email')} />
            {errors.email && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errors.email.message}</p>}
          </div>
          <div className="field">
            <label>Téléphone * <span className="muted">(format MAR: 06..., +2126...)</span></label>
            <input style={{ width: '100%' }} placeholder="0612345678" {...register('phoneNumber')} />
            {errors.phoneNumber && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errors.phoneNumber.message}</p>}
          </div>
          <div className="field">
            <label>Rôle *</label>
            <select style={{ width: '100%' }} {...register('role')}>
              <option value={UserRole.MEMBER}>Membre</option>
              <option value={UserRole.ADMIN}>Administrateur</option>
            </select>
            {errors.role && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errors.role.message}</p>}
          </div>
          <p className="hint">Un nom d'utilisateur et un mot de passe aléatoire seront générés automatiquement.</p>
          <div style={{ marginTop: 14, display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
            <button type="button" onClick={onClose}>Annuler</button>
            <button type="submit" className="primary" disabled={loading}>
              {loading ? <span className="spinner"></span> : null}Créer
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

function UpdateModal({ user, onClose, onSubmit, loading }: { user: UserResponse; onClose: () => void; onSubmit: (d: UpdateF) => void; loading: boolean }) {
  const { register, handleSubmit, formState: { errors } } = useForm<UpdateF>({
    resolver: zodResolver(updateSchema),
    defaultValues: { role: user.role, isActive: user.isActive },
  })
  return (
    <div className="modal-bg" onClick={onClose}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <header>
          <h3>Modifier {user.username}</h3>
          <button className="ghost small" onClick={onClose}>✕</button>
        </header>
        <form className="body" onSubmit={handleSubmit(onSubmit)}>
          <div className="info-box">
            <b>{user.firstName} {user.lastName}</b> — {user.email}
          </div>
          <div className="field">
            <label>Rôle</label>
            <select style={{ width: '100%' }} {...register('role')}>
              <option value={UserRole.MEMBER}>Membre</option>
              <option value={UserRole.ADMIN}>Administrateur</option>
            </select>
            {errors.role && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errors.role.message}</p>}
          </div>
          <div className="field">
            <label style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
              <input type="checkbox" {...register('isActive')} />
              Compte actif
            </label>
          </div>
          <div style={{ marginTop: 14, display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
            <button type="button" onClick={onClose}>Annuler</button>
            <button type="submit" className="primary" disabled={loading}>
              {loading ? <span className="spinner"></span> : null}Enregistrer
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

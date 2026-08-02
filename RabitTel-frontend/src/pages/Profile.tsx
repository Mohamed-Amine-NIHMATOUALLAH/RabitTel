import { useState, type FormEvent } from 'react'
import { useMutation } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { profileService, authService } from '../services'
import { useAuth } from '../auth/AuthContext'
import ErrorMsg from '../components/ErrorMsg'

const profileSchema = z.object({
  firstName: z.string().min(2).max(50),
  lastName: z.string().min(2).max(50),
  email: z.string().email(),
  phoneNumber: z.string().regex(/^(\+212|0)[5-7][0-9]{8}$/, 'Format téléphone invalide'),
})

const pwdSchema = z.object({
  currentPassword: z.string().min(1, 'Requis'),
  newPassword: z.string().min(8, 'Min 8 caractères').max(100),
  confirmPassword: z.string().min(1, 'Requis'),
}).refine((d) => d.newPassword === d.confirmPassword, {
  message: 'Les mots de passe ne correspondent pas',
  path: ['confirmPassword'],
})

type ProfileF = z.infer<typeof profileSchema>
type PwdF = z.infer<typeof pwdSchema>

export default function ProfilePage() {
  const { user, refreshProfile } = useAuth()
  const [tab, setTab] = useState<'profile' | 'password'>('profile')
  const [profileMsg, setProfileMsg] = useState<string | null>(null)
  const [pwdMsg, setPwdMsg] = useState<string | null>(null)
  const [profileErr, setProfileErr] = useState<unknown>(null)
  const [pwdErr, setPwdErr] = useState<unknown>(null)

  const { register: regP, handleSubmit: hsp, formState: { errors: errP } } = useForm<ProfileF>({
    resolver: zodResolver(profileSchema),
    defaultValues: user ? {
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      phoneNumber: user.phoneNumber,
    } : undefined,
  })

  const { register: regW, handleSubmit: hsw, reset: resetW, formState: { errors: errW } } = useForm<PwdF>({
    resolver: zodResolver(pwdSchema),
  })

  const updateMut = useMutation({
    mutationFn: (d: ProfileF) => profileService.update(d),
    onSuccess: async () => {
      await refreshProfile()
      setProfileMsg('Profil mis à jour avec succès.')
      setProfileErr(null)
    },
    onError: (e) => { setProfileErr(e); setProfileMsg(null) },
  })

  const pwdMut = useMutation({
    mutationFn: (d: PwdF) => authService.changePassword(d),
    onSuccess: () => {
      setPwdMsg('Mot de passe modifié avec succès.')
      setPwdErr(null)
      resetW()
    },
    onError: (e) => { setPwdErr(e); setPwdMsg(null) },
  })

  if (!user) return <p><span className="spinner"></span>Chargement…</p>

  return (
    <div>
      <h2 style={{ marginTop: 0 }}>Mon profil</h2>

      <div className="card" style={{ marginBottom: 16 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{
            width: 64, height: 64, borderRadius: '50%',
            background: '#2563eb', color: '#fff',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 22, fontWeight: 700,
          }}>
            {user.firstName[0]}{user.lastName[0]}
          </div>
          <div>
            <div style={{ fontSize: 16, fontWeight: 700 }}>{user.firstName} {user.lastName}</div>
            <div className="muted">@{user.username}</div>
            <div style={{ marginTop: 4, display: 'flex', gap: 6, alignItems: 'center' }}>
              <span className={'badge ' + (user.role === 'ADMIN' ? 'purple' : 'blue')}>{user.role}</span>
              <span className={'badge ' + (user.isActive ? 'green' : 'gray')}>{user.isActive ? 'Actif' : 'Désactivé'}</span>
            </div>
          </div>
          <div style={{ marginLeft: 'auto', textAlign: 'right', fontSize: 13 }}>
            <div className="muted">Email</div><div>{user.email}</div>
            <div className="muted" style={{ marginTop: 4 }}>Téléphone</div><div>{user.phoneNumber}</div>
          </div>
        </div>
      </div>

      <div className="tabs">
        <div className={'tab ' + (tab === 'profile' ? 'active' : '')} onClick={() => setTab('profile')}>Informations personnelles</div>
        <div className={'tab ' + (tab === 'password' ? 'active' : '')} onClick={() => setTab('password')}>Modifier mot de passe</div>
      </div>

      {tab === 'profile' && (
        <div className="card">
          <h3>Informations personnelles</h3>
          {profileMsg && <div className="success-box">{profileMsg}</div>}
          <ErrorMsg error={profileErr} />
          <form onSubmit={hsp((d) => updateMut.mutate(d)) as (e: FormEvent) => void}>
            <div className="grid-2">
              <div className="field">
                <label>Prénom</label>
                <input style={{ width: '100%' }} {...regP('firstName')} />
                {errP.firstName && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errP.firstName.message}</p>}
              </div>
              <div className="field">
                <label>Nom</label>
                <input style={{ width: '100%' }} {...regP('lastName')} />
                {errP.lastName && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errP.lastName.message}</p>}
              </div>
            </div>
            <div className="grid-2">
              <div className="field">
                <label>Email</label>
                <input type="email" style={{ width: '100%' }} {...regP('email')} />
                {errP.email && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errP.email.message}</p>}
              </div>
              <div className="field">
                <label>Téléphone</label>
                <input style={{ width: '100%' }} {...regP('phoneNumber')} />
                {errP.phoneNumber && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errP.phoneNumber.message}</p>}
              </div>
            </div>
            <div className="muted" style={{ fontSize: 12, marginBottom: 10 }}>
              Nom d'utilisateur: <b>@{user.username}</b> — ne peut pas être modifié.
            </div>
            <button type="submit" className="primary" disabled={updateMut.isPending}>
              {updateMut.isPending ? <span className="spinner"></span> : null}Enregistrer
            </button>
          </form>
        </div>
      )}

      {tab === 'password' && (
        <div className="card">
          <h3>Modifier le mot de passe</h3>
          {pwdMsg && <div className="success-box">{pwdMsg}</div>}
          <ErrorMsg error={pwdErr} />
          <form onSubmit={hsw((d) => pwdMut.mutate(d)) as (e: FormEvent) => void}>
            <div className="field">
              <label>Mot de passe actuel</label>
              <input type="password" style={{ width: '100%' }} {...regW('currentPassword')} />
              {errW.currentPassword && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errW.currentPassword.message}</p>}
            </div>
            <div className="grid-2">
              <div className="field">
                <label>Nouveau mot de passe</label>
                <input type="password" style={{ width: '100%' }} {...regW('newPassword')} />
                {errW.newPassword && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errW.newPassword.message}</p>}
              </div>
              <div className="field">
                <label>Confirmer</label>
                <input type="password" style={{ width: '100%' }} {...regW('confirmPassword')} />
                {errW.confirmPassword && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errW.confirmPassword.message}</p>}
              </div>
            </div>
            <button type="submit" className="primary" disabled={pwdMut.isPending}>
              {pwdMut.isPending ? <span className="spinner"></span> : null}Modifier le mot de passe
            </button>
          </form>
        </div>
      )}
    </div>
  )
}

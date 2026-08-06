import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { authService } from '../services'
import { useAuth } from '../auth/AuthContext'
import ErrorMsg from '../components/ErrorMsg'

const schema = z
  .object({
    currentPassword: z.string().min(1, 'Mot de passe actuel requis'),
    newPassword: z
      .string()
      .min(8, 'Minimum 8 caractères')
      .regex(/[A-Z]/, 'Doit contenir une majuscule')
      .regex(/[0-9]/, 'Doit contenir un chiffre')
      .regex(/[@#$%&*!?]/, 'Doit contenir un caractère spécial (@#$%&*!?)'),
    confirmPassword: z.string().min(1, 'Confirmation requise'),
  })
  .refine(d => d.newPassword === d.confirmPassword, {
    message: 'Les mots de passe ne correspondent pas',
    path: ['confirmPassword'],
  })

type F = z.infer<typeof schema>

/**
 * Shown after first login for every non-admin account.
 * The user cannot access any page until they set a new password.
 */
export default function ChangePasswordForced() {
  const { refreshProfile, logout } = useAuth()
  const nav = useNavigate()
  const [err, setErr] = useState<unknown>(null)
  const [submitting, setSubmitting] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<F>({ resolver: zodResolver(schema) })

  const onSubmit = async (data: F) => {
    setErr(null)
    setSubmitting(true)
    try {
      await authService.changePassword({
        currentPassword: data.currentPassword,
        newPassword: data.newPassword,
        confirmPassword: data.confirmPassword,
      })
      // Refresh profile so firstLogin becomes false
      await refreshProfile()
      nav('/dashboard', { replace: true })
    } catch (e) {
      setErr(e)
    } finally {
      setSubmitting(false)
    }
  }

  const onLogout = () => {
    logout()
    nav('/login', { replace: true })
  }

  return (
    <div className="login-wrap">
      <form
        className="login-box"
        onSubmit={handleSubmit(onSubmit) as (e: React.FormEvent) => void}
        style={{ maxWidth: 420 }}
      >
        {/* Header */}
        <div
          style={{
            background: '#fff7ed',
            border: '1px solid #fed7aa',
            borderRadius: 6,
            padding: '12px 16px',
            marginBottom: 20,
            color: '#9a3412',
          }}
        >
          <b>🔐 Changement de mot de passe obligatoire</b>
          <p style={{ margin: '4px 0 0', fontSize: 13 }}>
            Pour des raisons de sécurité, vous devez définir un nouveau mot de passe
            avant de continuer.
          </p>
        </div>

        <ErrorMsg error={err} />

        <div className="field">
          <label>Mot de passe temporaire reçu par email</label>
          <input
            type="password"
            style={{ width: '100%' }}
            autoFocus
            {...register('currentPassword')}
          />
          {errors.currentPassword && (
            <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>
              {errors.currentPassword.message}
            </p>
          )}
        </div>

        <div className="field">
          <label>Nouveau mot de passe</label>
          <input type="password" style={{ width: '100%' }} {...register('newPassword')} />
          {errors.newPassword && (
            <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>
              {errors.newPassword.message}
            </p>
          )}
          <p style={{ fontSize: 11, color: '#888', margin: '4px 0 0' }}>
            Min. 8 caractères · 1 majuscule · 1 chiffre · 1 caractère spécial
          </p>
        </div>

        <div className="field">
          <label>Confirmer le nouveau mot de passe</label>
          <input
            type="password"
            style={{ width: '100%' }}
            {...register('confirmPassword')}
          />
          {errors.confirmPassword && (
            <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>
              {errors.confirmPassword.message}
            </p>
          )}
        </div>

        <button
          type="submit"
          className="primary"
          style={{ width: '100%', marginTop: 6, padding: '9px', fontSize: 14 }}
          disabled={submitting}
        >
          {submitting ? <span className="spinner" /> : null}
          Définir mon mot de passe
        </button>

        <button
          type="button"
          onClick={onLogout}
          style={{
            width: '100%',
            marginTop: 8,
            padding: '8px',
            fontSize: 13,
            background: 'none',
            border: '1px solid #ddd',
            borderRadius: 4,
            cursor: 'pointer',
            color: '#666',
          }}
        >
          Se déconnecter
        </button>
      </form>
    </div>
  )
}

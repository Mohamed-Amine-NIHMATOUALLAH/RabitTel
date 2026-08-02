import { useEffect, useState, type FormEvent } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { authService } from '../services'
import { useAuth } from '../auth/AuthContext'
import ErrorMsg from '../components/ErrorMsg'

const schema = z.object({
  email: z.string().email('Email invalide').min(1, 'Champ requis'),
  password: z.string().min(1, 'Mot de passe requis'),
})
type F = z.infer<typeof schema>

export default function LoginPage() {
  const { setAuth, token } = useAuth()
  const nav = useNavigate()
  const [sp] = useSearchParams()
  const [err, setErr] = useState<unknown>(null)
  const [submitting, setSubmitting] = useState(false)
  const { register, handleSubmit, formState: { errors } } = useForm<F>({
    resolver: zodResolver(schema),
    defaultValues: { email: '', password: '' },
  })

  useEffect(() => {
    if (token) nav('/', { replace: true })
  }, [token, nav])

  const onSubmit = async (data: F) => {
    setErr(null)
    setSubmitting(true)
    try {
      const resp = await authService.login(data)
      setAuth(resp.accessToken, resp.user)
      const rd = sp.get('redirect')
      nav(rd || '/', { replace: true })
    } catch (e) {
      setErr(e)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="login-wrap">
      <form className="login-box" onSubmit={handleSubmit(onSubmit) as (e: FormEvent) => void}>
        <h1>RabitTel</h1>
        <p className="sub">Plateforme de gestion télécom</p>

        <ErrorMsg error={err} />

        <div className="field">
          <label>Email</label>
          <input
            type="email"
            style={{ width: '100%' }}
            placeholder="admin@gmail.com"
            autoFocus
            {...register('email')}
          />
          {errors.email && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errors.email.message}</p>}
        </div>

        <div className="field">
          <label>Mot de passe</label>
          <input
            type="password"
            style={{ width: '100%' }}
            placeholder="Admin@2026"
            {...register('password')}
          />
          {errors.password && <p style={{ color: 'red', fontSize: 12, margin: '3px 0 0' }}>{errors.password.message}</p>}
        </div>

        <div className="info-box">
          <b>Par défaut :</b> admin@gmail.com / Admin@2026
        </div>

        <button
          type="submit"
          className="primary"
          style={{ width: '100%', marginTop: 6, padding: '9px', fontSize: 14 }}
          disabled={submitting}
        >
          {submitting ? <span className="spinner"></span> : null}
          Se connecter
        </button>
      </form>
    </div>
  )
}

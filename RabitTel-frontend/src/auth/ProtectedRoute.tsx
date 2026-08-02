import { Navigate, useLocation } from 'react-router-dom'
import { useAuth, isAdmin } from './AuthContext'
import type { ReactNode } from 'react'

export default function ProtectedRoute({ children, adminOnly = false }: { children: ReactNode; adminOnly?: boolean }) {
  const { user, token, ready } = useAuth()
  const loc = useLocation()

  if (!ready) {
    return <div style={{ padding: 40, textAlign: 'center' }}><span className="spinner"></span>Chargement…</div>
  }

  if (!token || !user) {
    return <Navigate to={'/login?redirect=' + encodeURIComponent(loc.pathname + loc.search)} replace />
  }

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
  const go = () => { logout(); location.href = '/login' }
  return <button className="primary" style={{ marginTop: 16 }} onClick={go}>Retour à la connexion</button>
}

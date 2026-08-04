import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth, isAdmin } from '../auth/AuthContext'

const nav = [
  { to: '/dashboard', label: 'Tableau de bord' },
  { to: '/agencies', label: 'Directions' },
  { to: '/plans', label: 'Forfaits' },
  { to: '/contracts', label: 'Contrats' },
]

const linesNav = [
  { to: '/lines', label: 'Aperçu' },
  { to: '/lines/ftth', label: 'FTTH' },
  { to: '/lines/rtc', label: 'RTC' },
  { to: '/lines/vpn-adsl', label: 'VPN ADSL' },
  { to: '/lines/adsl', label: 'ADSL' },
  { to: '/lines/lli', label: 'LLI' },
  { to: '/lines/vpn-ll', label: 'VPN LL' },
  { to: '/lines/gsm', label: 'GSM Pro' },
  { to: '/lines/4g', label: '4G Internet' },
  { to: '/lines/4g-vpn', label: '4G VPN' },
]

const adminNav = [
  { to: '/users', label: 'Utilisateurs' },
]

export default function Layout() {
  const { user, logout } = useAuth()
  const nav_ = useNavigate()

  const onLogout = () => {
    logout()
    nav_('/login', { replace: true })
  }

  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <aside style={{ width: 210, borderRight: '1px solid #ddd', background: '#fafafa', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: 14, borderBottom: '1px solid #ddd', fontWeight: 800, fontSize: 16, letterSpacing: 0.3, color: '#1e3a8a' }}>
          📡 RabitTel
        </div>

        <nav style={{ padding: 8, flex: 1, overflowY: 'auto' }}>
          {nav.map(item => (
            <NavLink
              key={item.to}
              to={item.to}
              end
              style={linkStyle}
            >
              {item.label}
            </NavLink>
          ))}

          <div className="menu-group-title">Lignes Télécom</div>
          {linesNav.map(item => (
            <NavLink
              key={item.to}
              to={item.to}
              end
              style={({ isActive }) => ({
                ...linkStyle({ isActive }) as React.CSSProperties,
                paddingLeft: 16,
                fontSize: 13,
              })}
            >
              {item.label}
            </NavLink>
          ))}

          {isAdmin(user) ? (
            <>
              <div className="menu-group-title">Administration</div>
              {adminNav.map(item => (
                <NavLink key={item.to} to={item.to} end style={linkStyle}>
                  {item.label}
                </NavLink>
              ))}
            </>
          ) : null}

          <div style={{ height: 30 }} />
        </nav>

        <div style={{ padding: 10, borderTop: '1px solid #ddd' }}>
          <NavLink to="/profile" style={({ isActive }) => ({
            display: 'block', padding: '6px 8px', marginBottom: 2,
            textDecoration: 'none', borderRadius: 3,
            background: isActive ? '#dbeafe' : 'transparent',
            color: '#111', fontSize: 13,
          })}>
            👤 Mon profil
          </NavLink>
          <button
            onClick={onLogout}
            style={{ width: '100%', textAlign: 'left', marginTop: 2, padding: '6px 8px', color: '#991b1b', border: 'none', background: 'transparent', fontSize: 13 }}
          >
            🚪 Déconnexion
          </button>
        </div>
      </aside>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', minWidth: 0 }}>
        <div className="topbar">
          <span className="muted">{new Date().toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}</span>
          <div style={{ borderLeft: '1px solid #ddd', paddingLeft: 10, marginLeft: 4, display: 'flex', alignItems: 'center', gap: 8 }}>
            <span className="badge blue">{user?.role}</span>
            <span>
              <b>{user?.firstName} {user?.lastName}</b>
              <span className="muted" style={{ marginLeft: 6, fontSize: 12 }}>@{user?.username}</span>
            </span>
            <button className="small" onClick={onLogout} title="Se déconnecter">⏻</button>
          </div>
        </div>
        <main style={{ flex: 1, padding: 20, overflowY: 'auto' }}>
          <Outlet />
        </main>
      </div>
    </div>
  )
}

const linkStyle = ({ isActive }: { isActive: boolean }): React.CSSProperties => ({
  display: 'block',
  padding: '6px 8px',
  marginBottom: 2,
  textDecoration: 'none',
  background: isActive ? '#dbeafe' : 'transparent',
  color: isActive ? '#1e40af' : '#111',
  fontWeight: isActive ? 600 : 400,
  borderRadius: 3,
  fontSize: 13,
})

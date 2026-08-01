import { NavLink, Outlet } from 'react-router-dom'

const nav = [
  { to: '/', label: 'Tableau de bord' },
  { to: '/agencies', label: 'Directions' },
  { to: '/plans', label: 'Forfaits' },
  { to: '/contracts', label: 'Contrats' },
  { to: '/lines/ftth', label: 'FTTH' },
  { to: '/lines/rtc', label: 'RTC' },
  { to: '/lines/vpn', label: 'VPN ADSL' },
  { to: '/lines/gsm', label: 'GSM Pro' },
  { to: '/lines/4g', label: '4G Internet' },
  { to: '/lines/4g-vpn', label: '4G VPN' },
]

export default function Layout() {
  return (
    <div style={{ display: 'flex', minHeight: '100vh' }}>
      <nav style={{ width: 190, borderRight: '1px solid #ccc', padding: 12 }}>
        <div style={{ fontWeight: 'bold', marginBottom: 16, fontSize: 16 }}>RabitTel</div>
        {nav.map(item => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            style={({ isActive }) => ({
              display: 'block',
              padding: '6px 8px',
              marginBottom: 2,
              textDecoration: 'none',
              background: isActive ? '#e0e0e0' : 'transparent',
              color: '#000',
              borderRadius: 3,
            })}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
      <main style={{ flex: 1, padding: 24 }}>
        <Outlet />
      </main>
    </div>
  )
}

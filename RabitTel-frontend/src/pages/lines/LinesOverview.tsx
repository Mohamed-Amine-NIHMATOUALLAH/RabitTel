import { useQueries } from '@tanstack/react-query'
import { Link } from 'react-router-dom'
import { ftthService, rtcService, vpnService, gsmService, internet4GService, vpn4GService } from '../../services'
import { LineType } from '../../types'

const LINE_TYPES: { label: string; to: string; type: LineType; fn: () => Promise<unknown[]>; color: string; icon: string }[] = [
  { label: 'FTTH', to: '/lines/ftth', type: LineType.FTTH, fn: () => ftthService.getAll(), color: '#2563eb', icon: '🔌' },
  { label: 'RTC', to: '/lines/rtc', type: LineType.RTC, fn: () => rtcService.getAll(), color: '#16a34a', icon: '☎️' },
  { label: 'GSM Pro', to: '/lines/gsm', type: LineType.GSM_PRO, fn: () => gsmService.getAll(), color: '#9333ea', icon: '📱' },
  { label: '4G Internet', to: '/lines/4g', type: LineType.G4, fn: () => internet4GService.getAll(), color: '#ea580c', icon: '🛰️' },
  { label: 'VPN ADSL', to: '/lines/vpn', type: LineType.VPN_ADSL, fn: () => vpnService.getAll(), color: '#0891b2', icon: '🔒' },
  { label: '4G VPN', to: '/lines/4g-vpn', type: LineType.G4_VPN, fn: () => vpn4GService.getAll(), color: '#be123c', icon: '🔐' },
]

export default function LinesOverviewPage() {
  const results = useQueries({
    queries: LINE_TYPES.map(lt => ({
      queryKey: ['lines-overview-' + lt.type],
      queryFn: lt.fn,
      staleTime: 1000 * 60,
    })),
  })

  const total = results.reduce((s, r) => s + (Array.isArray(r.data) ? r.data.length : 0), 0)

  return (
    <div>
      <div className="flex-between" style={{ marginBottom: 14 }}>
        <h2 style={{ margin: 0 }}>Lignes Télécom — Aperçu</h2>
        <div className="muted">Total : <b style={{ color: '#2563eb', fontSize: 16 }}>{total}</b> lignes</div>
      </div>

      <div className="stats">
        {LINE_TYPES.map((lt, i) => {
          const q = results[i]
          const n = Array.isArray(q.data) ? q.data.length : 0
          return (
            <div key={lt.type} className="stat" style={{ borderLeft: `4px solid ${lt.color}` }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <span style={{ fontSize: 24 }}>{lt.icon}</span>
                <span className={'badge ' + (n === 0 ? 'gray' : 'blue')}>{n}</span>
              </div>
              <div className="n" style={{ color: lt.color, marginTop: 8 }}>
                {q.isLoading ? <span className="spinner"></span> : n}
              </div>
              <div className="l">
                <Link to={lt.to}>{lt.label} →</Link>
              </div>
              {q.isError && <div style={{ color: '#b91c1c', fontSize: 11, marginTop: 4 }}>⚠ Erreur chargement</div>}
            </div>
          )
        })}
      </div>

      <div className="card">
        <h3>Accès rapide</h3>
        <table style={{ maxWidth: 500 }}>
          <thead>
            <tr><th>Type</th><th>Nombre</th><th style={{ width: 1 }}>Action</th></tr>
          </thead>
          <tbody>
            {LINE_TYPES.map((lt, i) => {
              const n = Array.isArray(results[i].data) ? results[i].data.length : 0
              return (
                <tr key={lt.type}>
                  <td><span style={{ fontSize: 16, marginRight: 6 }}>{lt.icon}</span><b>{lt.label}</b></td>
                  <td>{n}</td>
                  <td><Link to={lt.to}><button className="small primary">Ouvrir →</button></Link></td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </div>
  )
}

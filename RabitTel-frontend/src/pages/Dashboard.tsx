import type { ContractResponse } from '../types'
import { useQuery } from '@tanstack/react-query'
import { agencyService, planService, contractService, ftthService, rtcService, vpnService, gsmService, internet4GService, vpn4GService } from '../services'
import ErrorMsg from '../components/ErrorMsg'

export default function Dashboard() {
  const agencies = useQuery({ queryKey: ['agencies'], queryFn: () => agencyService.getAll() })
  const plans = useQuery({ queryKey: ['plans'], queryFn: () => planService.getAll() })
  const contracts = useQuery({ queryKey: ['contracts'], queryFn: () => contractService.getAll() })
  const expiring = useQuery({ queryKey: ['contracts-expiring'], queryFn: () => contractService.getExpiring(30) })
  const ftth = useQuery({ queryKey: ['ftth'], queryFn: () => ftthService.getAll() })
  const rtc = useQuery({ queryKey: ['rtc'], queryFn: () => rtcService.getAll() })
  const vpn = useQuery({ queryKey: ['vpn'], queryFn: () => vpnService.getAll() })
  const gsm = useQuery({ queryKey: ['gsm'], queryFn: () => gsmService.getAll() })
  const g4 = useQuery({ queryKey: ['4g'], queryFn: () => internet4GService.getAll() })
  const g4vpn = useQuery({ queryKey: ['4g-vpn'], queryFn: () => vpn4GService.getAll() })

  const totalLines =
    (ftth.data?.length ?? 0) +
    (rtc.data?.length ?? 0) +
    (vpn.data?.length ?? 0) +
    (gsm.data?.length ?? 0) +
    (g4.data?.length ?? 0) +
    (g4vpn.data?.length ?? 0)

  return (
    <div>
      <h1>Dashboard</h1>
      <table border={1} cellPadding={8} style={{ borderCollapse: 'collapse', marginTop: 16 }}>
        <tbody>
          <tr>
            <td>Agencies</td>
            <td>{agencies.isLoading ? '...' : agencies.data?.length ?? '-'}</td>
          </tr>
          <tr>
            <td>Plans</td>
            <td>{plans.isLoading ? '...' : plans.data?.length ?? '-'}</td>
          </tr>
          <tr>
            <td>Contracts</td>
            <td>{contracts.isLoading ? '...' : contracts.data?.length ?? '-'}</td>
          </tr>
          <tr>
            <td>Contracts expiring (&lt;30 days)</td>
            <td>{expiring.isLoading ? '...' : expiring.data?.length ?? '-'}</td>
          </tr>
          <tr>
            <td>Total Lines</td>
            <td>{ftth.isLoading ? '...' : totalLines}</td>
          </tr>
          <tr><td>FTTH</td><td>{ftth.data?.length ?? '-'}</td></tr>
          <tr><td>RTC</td><td>{rtc.data?.length ?? '-'}</td></tr>
          <tr><td>VPN ADSL</td><td>{vpn.data?.length ?? '-'}</td></tr>
          <tr><td>GSM Pro</td><td>{gsm.data?.length ?? '-'}</td></tr>
          <tr><td>4G Internet</td><td>{g4.data?.length ?? '-'}</td></tr>
          <tr><td>4G VPN</td><td>{g4vpn.data?.length ?? '-'}</td></tr>
        </tbody>
      </table>
      {(agencies.error || contracts.error) && <ErrorMsg error={agencies.error ?? contracts.error} />}

      {expiring.data && expiring.data.length > 0 && (
        <div style={{ marginTop: 24 }}>
          <h2>Contracts expiring within 30 days</h2>
          <table border={1} cellPadding={8} style={{ borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th>ID</th><th>Start</th><th>End</th><th>Duration</th><th>Status</th><th>Lines</th>
              </tr>
            </thead>
            <tbody>
              {expiring.data.map((c: ContractResponse) => (
                <tr key={c.id}>
                  <td>{c.id.slice(0, 8)}…</td>
                  <td>{c.startDate}</td>
                  <td>{c.endDate}</td>
                  <td>{c.durationMonths} mo</td>
                  <td>{c.status}</td>
                  <td>{c.linesCount}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}

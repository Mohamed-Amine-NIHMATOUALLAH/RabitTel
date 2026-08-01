import { createBrowserRouter } from 'react-router-dom'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import Agencies from './pages/Agencies'
import Plans from './pages/Plans'
import Contracts from './pages/Contracts'
import FTTHLines from './pages/lines/FTTHLines'
import RTCLines from './pages/lines/RTCLines'
import VPNLines from './pages/lines/VPNLines'
import GSMLines from './pages/lines/GSMLines'
import Internet4GLines from './pages/lines/Internet4GLines'
import VPN4GLines from './pages/lines/VPN4GLines'

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <Dashboard /> },
      { path: 'agencies', element: <Agencies /> },
      { path: 'plans', element: <Plans /> },
      { path: 'contracts', element: <Contracts /> },
      { path: 'lines/ftth', element: <FTTHLines /> },
      { path: 'lines/rtc', element: <RTCLines /> },
      { path: 'lines/vpn', element: <VPNLines /> },
      { path: 'lines/gsm', element: <GSMLines /> },
      { path: 'lines/4g', element: <Internet4GLines /> },
      { path: 'lines/4g-vpn', element: <VPN4GLines /> },
    ],
  },
])

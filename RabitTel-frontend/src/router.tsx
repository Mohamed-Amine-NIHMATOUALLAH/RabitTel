import { createBrowserRouter, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import ProtectedRoute from './auth/ProtectedRoute'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Agencies from './pages/Agencies'
import Plans from './pages/Plans'
import Contracts from './pages/Contracts'
import Users from './pages/Users'
import Profile from './pages/Profile'
import ChangePasswordForced from './pages/ChangePasswordForced'
import FTTHLines from './pages/lines/FTTHLines'
import RTCLines from './pages/lines/RTCLines'
import VPNAdslLines from './pages/lines/DataLines'
import AdslLines from './pages/lines/AdslLines'
import LLILines from './pages/lines/LLILines'
import VPNLLLines from './pages/lines/VPNLLLines'
import GSMLines from './pages/lines/GSMLines'
import Internet4GLines from './pages/lines/Internet4GLines'
import VPN4GLines from './pages/lines/VPN4GLines'
import LinesOverview from './pages/lines/LinesOverview'
import { useAuth, isAdmin } from './auth/AuthContext'

/** Smart index: ADMIN → /dashboard, MEMBER → /lines */
function HomeRedirect() {
  const { user } = useAuth()
  return <Navigate to={isAdmin(user) ? '/dashboard' : '/lines'} replace />
}

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/change-password-forced',
    element: (
      <ProtectedRoute>
        <ChangePasswordForced />
      </ProtectedRoute>
    ),
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <HomeRedirect /> },
      {
        path: 'dashboard',
        element: (
          <ProtectedRoute adminOnly>
            <Dashboard />
          </ProtectedRoute>
        ),
      },
      {
        path: 'users',
        element: (
          <ProtectedRoute adminOnly>
            <Users />
          </ProtectedRoute>
        ),
      },
      { path: 'profile', element: <Profile /> },
      { path: 'agencies', element: <Agencies /> },
      { path: 'plans', element: <Plans /> },
      { path: 'contracts', element: <Contracts /> },
      { path: 'lines', element: <LinesOverview /> },
      { path: 'lines/ftth', element: <FTTHLines /> },
      { path: 'lines/rtc', element: <RTCLines /> },
      { path: 'lines/vpn-adsl', element: <VPNAdslLines /> },
      { path: 'lines/adsl', element: <AdslLines /> },
      { path: 'lines/lli', element: <LLILines /> },
      { path: 'lines/vpn-ll', element: <VPNLLLines /> },
      { path: 'lines/gsm', element: <GSMLines /> },
      { path: 'lines/4g', element: <Internet4GLines /> },
      { path: 'lines/4g-vpn', element: <VPN4GLines /> },
    ],
  },
  { path: '*', element: <Navigate to="/" replace /> },
])

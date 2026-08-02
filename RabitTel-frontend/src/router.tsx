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
import FTTHLines from './pages/lines/FTTHLines'
import RTCLines from './pages/lines/RTCLines'
import VPNLines from './pages/lines/VPNLines'
import GSMLines from './pages/lines/GSMLines'
import Internet4GLines from './pages/lines/Internet4GLines'
import VPN4GLines from './pages/lines/VPN4GLines'
import LinesOverview from './pages/lines/LinesOverview'

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/',
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard', element: <Dashboard /> },
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
      { path: 'lines/vpn', element: <VPNLines /> },
      { path: 'lines/gsm', element: <GSMLines /> },
      { path: 'lines/4g', element: <Internet4GLines /> },
      { path: 'lines/4g-vpn', element: <VPN4GLines /> },
    ],
  },
  { path: '*', element: <Navigate to="/dashboard" replace /> },
])

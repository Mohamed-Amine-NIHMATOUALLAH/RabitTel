import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import { getToken, setToken, clearToken } from '../lib/axios'
import type { UserResponse } from '../types'
import { profileService } from '../services'

interface AuthState {
  token: string | null
  user: UserResponse | null
  ready: boolean
}

interface AuthContextType extends AuthState {
  setAuth: (token: string, user: UserResponse) => void
  logout: () => void
  refreshProfile: () => Promise<void>
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setTokenState] = useState<string | null>(() => getToken())
  const [user, setUser] = useState<UserResponse | null>(null)
  const [ready, setReady] = useState(false)

  useEffect(() => {
    ;(async () => {
      if (token) {
        try {
          const u = await profileService.get()
          setUser(u)
        } catch (e) {
          clearToken()
          setTokenState(null)
          setUser(null)
        }
      }
      setReady(true)
    })()
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const setAuth = (t: string, u: UserResponse) => {
    setToken(t)
    setTokenState(t)
    setUser(u)
  }

  const logout = () => {
    clearToken()
    setTokenState(null)
    setUser(null)
  }

  const refreshProfile = async () => {
    if (token) {
      try {
        const u = await profileService.get()
        setUser(u)
      } catch { /* ignore */ }
    }
  }

  return (
    <AuthContext.Provider value={{ token, user, ready, setAuth, logout, refreshProfile }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}

export function isAdmin(user: UserResponse | null): boolean {
  return user?.role === 'ADMIN'
}

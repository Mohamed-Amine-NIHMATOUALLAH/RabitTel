import axios from 'axios'
import type { AxiosResponse } from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

api.interceptors.response.use(
  (r: AxiosResponse) => r,
  (err: unknown) => {
    const e = err as { response?: { data?: unknown }; message?: string }
    console.error('API Error:', e.response?.data ?? e.message)
    return Promise.reject(err)
  }
)

export default api

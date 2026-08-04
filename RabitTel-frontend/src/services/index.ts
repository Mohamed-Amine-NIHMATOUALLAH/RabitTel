import type { AxiosResponse } from 'axios'
import api from '../lib/axios'
import type {
  AgencyResponse, AgencyCreateRequest, AgencyUpdateRequest,
  PlanResponse, PlanCreateRequest, PlanUpdateRequest,
  ContractResponse, ContractCreateRequest, ContractRenewalRequest,
  FTTHLineResponse, FTTHLineCreateRequest, FTTHLineUpdateRequest,
  RTCLineResponse, RTCLineCreateRequest, RTCLineUpdateRequest,
  DataLineResponse, DataLineCreateRequest, DataLineUpdateRequest,
  GSMLineResponse, GSMLineCreateRequest, GSMLineUpdateRequest,
  Internet4GLineResponse, Internet4GLineCreateRequest, Internet4GLineUpdateRequest,
  VPN4GLineResponse, VPN4GLineCreateRequest, VPN4GLineUpdateRequest,
  LoginRequest, LoginResponse,
  UserResponse, AdminCreateUserRequest, AdminUpdateUserRequest,
  UserUpdateRequest, ChangePasswordRequest, ForgotPasswordRequest,
  Page,
} from '../types'
import { ContractStatus, LineStatus, LineType } from '../types'

const d = <T>(r: AxiosResponse<T>) => r.data

// ─── Auth ────────────────────────────────────────────────────────────────────
export const authService = {
  login: (req: LoginRequest) =>
    api.post<LoginResponse>('/auth/login', req).then(d),
  changePassword: (req: ChangePasswordRequest) =>
    api.post<void>('/auth/change-password', req).then(() => undefined as void),
}

// ─── Admin Users ─────────────────────────────────────────────────────────────
export const userService = {
  create: (req: AdminCreateUserRequest) =>
    api.post<UserResponse>('/admin/users', req).then(d),
  update: (id: string, req: AdminUpdateUserRequest) =>
    api.put<UserResponse>(`/admin/users/${id}`, req).then(d),
  activate: (id: string) =>
    api.patch<void>(`/admin/users/${id}/activate`).then(() => undefined as void),
  deactivate: (id: string) =>
    api.patch<void>(`/admin/users/${id}/deactivate`).then(() => undefined as void),
  getById: (id: string) =>
    api.get<UserResponse>(`/admin/users/${id}`).then(d),
  getAll: (p?: { page?: number; size?: number; sortBy?: string; sortDirection?: string }) =>
    api.get<Page<UserResponse>>('/admin/users', { params: p }).then(d),
  resetPassword: (id: string) =>
    api.patch<void>(`/admin/users/${id}/reset-password`).then(() => undefined as void),
}

// ─── Profile ─────────────────────────────────────────────────────────────────
export const profileService = {
  get: () => api.get<UserResponse>('/profile').then(d),
  update: (req: UserUpdateRequest) => api.put<UserResponse>('/profile', req).then(d),
}

// ─── Password reset ──────────────────────────────────────────────────────────
export const passwordService = {
  forgot: (req: ForgotPasswordRequest) =>
    api.post<void>('/password/forgot', req).then(() => undefined as void),
  reset: (userId: string) =>
    api.patch<void>(`/password/reset/${userId}`).then(() => undefined as void),
}

// ─── Agencies ────────────────────────────────────────────────────────────────
export const agencyService = {
  getAll: (p?: { active?: boolean; region?: string; directorateCode?: string; name?: string }) =>
    api.get<AgencyResponse[]>('/agencies', { params: p }).then(d),
  getById: (id: string) =>
    api.get<AgencyResponse>(`/agencies/${id}`).then(d),
  create: (dto: AgencyCreateRequest) =>
    api.post<AgencyResponse>('/agencies', dto).then(d),
  update: (id: string, dto: AgencyUpdateRequest) =>
    api.put<AgencyResponse>(`/agencies/${id}`, dto).then(d),
  deactivate: (id: string) =>
    api.patch(`/agencies/${id}/deactivate`).then(() => undefined as void),
  delete: (id: string) =>
    api.delete(`/agencies/${id}`).then(() => undefined as void),
}

// ─── Plans ───────────────────────────────────────────────────────────────────
export const planService = {
  getAll: (p?: { active?: boolean; name?: string; priceFrom?: number; priceTo?: number }) =>
    api.get<PlanResponse[]>('/plans', { params: p }).then(d),
  getById: (id: string) =>
    api.get<PlanResponse>(`/plans/${id}`).then(d),
  create: (dto: PlanCreateRequest) =>
    api.post<PlanResponse>('/plans', dto).then(d),
  update: (id: string, dto: PlanUpdateRequest) =>
    api.put<PlanResponse>(`/plans/${id}`, dto).then(d),
  delete: (id: string) =>
    api.delete(`/plans/${id}`).then(() => undefined as void),
}

// ─── Contracts ───────────────────────────────────────────────────────────────
export const contractService = {
  getAll: (p?: { status?: ContractStatus; startDateFrom?: string; startDateTo?: string; endDateFrom?: string; endDateTo?: string }) =>
    api.get<ContractResponse[]>('/contracts', { params: p }).then(d),
  getById: (id: string) =>
    api.get<ContractResponse>(`/contracts/${id}`).then(d),
  getActive: () =>
    api.get<ContractResponse[]>('/contracts/active').then(d),
  getExpired: () =>
    api.get<ContractResponse[]>('/contracts/expired').then(d),
  getExpiring: (daysThreshold = 30) =>
    api.get<ContractResponse[]>('/contracts/expiring', { params: { daysThreshold } }).then(d),
  getDaysUntilExpiration: (id: string) =>
    api.get<number>(`/contracts/${id}/days-until-expiration`).then(d),
  create: (dto: ContractCreateRequest) =>
    api.post<ContractResponse>('/contracts', dto).then(d),
  renew: (id: string, dto: ContractRenewalRequest) =>
    api.patch<ContractResponse>(`/contracts/${id}/renew`, dto).then(d),
  delete: (id: string) =>
    api.delete(`/contracts/${id}`).then(() => undefined as void),
}

// ─── FTTH Lines ───────────────────────────────────────────────────────────────
export const ftthService = {
  getAll: (p?: { lineNumber?: string; lineStatus?: LineStatus; fixedLineNumber?: string; routerBrand?: string; bandwidth?: string }) =>
    api.get<FTTHLineResponse[]>('/lines/ftth', { params: p }).then(d),
  getById: (id: string) =>
    api.get<FTTHLineResponse>(`/lines/ftth/${id}`).then(d),
  getByLineNumber: (n: string) =>
    api.get<FTTHLineResponse>(`/lines/ftth/number/${n}`).then(d),
  getBillable: () =>
    api.get<FTTHLineResponse[]>('/lines/ftth/billable').then(d),
  create: (dto: FTTHLineCreateRequest) =>
    api.post<FTTHLineResponse>('/lines/ftth', dto).then(d),
  update: (id: string, dto: FTTHLineUpdateRequest) =>
    api.put<FTTHLineResponse>(`/lines/ftth/${id}`, dto).then(d),
  terminate: (id: string) =>
    api.patch(`/lines/ftth/${id}/terminate`).then(() => undefined as void),
  delete: (id: string) =>
    api.delete(`/lines/ftth/${id}`).then(() => undefined as void),
}

// ─── RTC Lines ────────────────────────────────────────────────────────────────
export const rtcService = {
  getAll: (p?: { lineNumber?: string; lineStatus?: LineStatus }) =>
    api.get<RTCLineResponse[]>('/lines/rtc', { params: p }).then(d),
  getById: (id: string) =>
    api.get<RTCLineResponse>(`/lines/rtc/${id}`).then(d),
  getByLineNumber: (n: string) =>
    api.get<RTCLineResponse>(`/lines/rtc/number/${n}`).then(d),
  getBillable: () =>
    api.get<RTCLineResponse[]>('/lines/rtc/billable').then(d),
  create: (dto: RTCLineCreateRequest) =>
    api.post<RTCLineResponse>('/lines/rtc', dto).then(d),
  update: (id: string, dto: RTCLineUpdateRequest) =>
    api.put<RTCLineResponse>(`/lines/rtc/${id}`, dto).then(d),
  terminate: (id: string) =>
    api.patch(`/lines/rtc/${id}/terminate`).then(() => undefined as void),
  delete: (id: string) =>
    api.delete(`/lines/rtc/${id}`).then(() => undefined as void),
}

// ─── Data Lines ──────────────────────────────────────────────────────────
export const dataLineService = {
  getAll: (p?: { lineNumber?: string; lineStatus?: LineStatus; lineType?: LineType; bandwidth?: string; ipAddress?: string }) =>
    api.get<DataLineResponse[]>('/lines/data', { params: p }).then(d),
  getById: (id: string) =>
    api.get<DataLineResponse>(`/lines/data/${id}`).then(d),
  getByLineNumber: (n: string) =>
    api.get<DataLineResponse>(`/lines/data/number/${n}`).then(d),
  getBillable: (lineType?: LineType) =>
    api.get<DataLineResponse[]>('/lines/data/billable', { params: { lineType } }).then(d),
  create: (dto: DataLineCreateRequest) =>
    api.post<DataLineResponse>('/lines/data', dto).then(d),
  update: (id: string, dto: DataLineUpdateRequest) =>
    api.put<DataLineResponse>(`/lines/data/${id}`, dto).then(d),
  terminate: (id: string) =>
    api.patch(`/lines/data/${id}/terminate`).then(() => undefined as void),
  delete: (id: string) =>
    api.delete(`/lines/data/${id}`).then(() => undefined as void),
  getAllByType: (lineType: LineType) =>
    api.get<DataLineResponse[]>('/lines/data', { params: { lineType } }).then(d),
  getBillableByType: (lineType: LineType) =>
    api.get<DataLineResponse[]>('/lines/data/billable', { params: { lineType } }).then(d),
}

// ─── GSM Pro Lines ────────────────────────────────────────────────────────────
export const gsmService = {
  getAll: (p?: { lineNumber?: string; lineStatus?: LineStatus; serviceFunction?: string; chipSerialNumber?: string; chipDeliveryDateFrom?: string; chipDeliveryDateTo?: string; pinCode?: string; pukCode?: string }) =>
    api.get<GSMLineResponse[]>('/lines/gsm', { params: p }).then(d),
  getById: (id: string) =>
    api.get<GSMLineResponse>(`/lines/gsm/${id}`).then(d),
  getByLineNumber: (n: string) =>
    api.get<GSMLineResponse>(`/lines/gsm/number/${n}`).then(d),
  getBillable: () =>
    api.get<GSMLineResponse[]>('/lines/gsm/billable').then(d),
  create: (dto: GSMLineCreateRequest) =>
    api.post<GSMLineResponse>('/lines/gsm', dto).then(d),
  update: (id: string, dto: GSMLineUpdateRequest) =>
    api.put<GSMLineResponse>(`/lines/gsm/${id}`, dto).then(d),
  terminate: (id: string) =>
    api.patch(`/lines/gsm/${id}/terminate`).then(() => undefined as void),
  delete: (id: string) =>
    api.delete(`/lines/gsm/${id}`).then(() => undefined as void),
}

// ─── 4G Internet Lines ────────────────────────────────────────────────────────
export const internet4GService = {
  getAll: (p?: { lineNumber?: string; lineStatus?: LineStatus; serviceFunction?: string; simSerialNumber?: string; pinCode?: string; pukCode?: string; equipment?: string; equipmentSerialNumber?: string; bandwidth?: string }) =>
    api.get<Internet4GLineResponse[]>('/lines/4g-internet', { params: p }).then(d),
  getById: (id: string) =>
    api.get<Internet4GLineResponse>(`/lines/4g-internet/${id}`).then(d),
  getByLineNumber: (n: string) =>
    api.get<Internet4GLineResponse>(`/lines/4g-internet/number/${n}`).then(d),
  getBillable: () =>
    api.get<Internet4GLineResponse[]>('/lines/4g-internet/billable').then(d),
  create: (dto: Internet4GLineCreateRequest) =>
    api.post<Internet4GLineResponse>('/lines/4g-internet', dto).then(d),
  update: (id: string, dto: Internet4GLineUpdateRequest) =>
    api.put<Internet4GLineResponse>(`/lines/4g-internet/${id}`, dto).then(d),
  terminate: (id: string) =>
    api.patch(`/lines/4g-internet/${id}/terminate`).then(() => undefined as void),
  delete: (id: string) =>
    api.delete(`/lines/4g-internet/${id}`).then(() => undefined as void),
}

// ─── VPN 4G Lines ─────────────────────────────────────────────────────────────
export const vpn4GService = {
  getAll: (p?: { lineNumber?: string; lineStatus?: LineStatus; equipment?: string; ipAddress?: string; serialNumber?: string; deliveryDateFrom?: string; deliveryDateTo?: string }) =>
    api.get<VPN4GLineResponse[]>('/lines/4g-vpn', { params: p }).then(d),
  getById: (id: string) =>
    api.get<VPN4GLineResponse>(`/lines/4g-vpn/${id}`).then(d),
  getByLineNumber: (n: string) =>
    api.get<VPN4GLineResponse>(`/lines/4g-vpn/number/${n}`).then(d),
  getBillable: () =>
    api.get<VPN4GLineResponse[]>('/lines/4g-vpn/billable').then(d),
  create: (dto: VPN4GLineCreateRequest) =>
    api.post<VPN4GLineResponse>('/lines/4g-vpn', dto).then(d),
  update: (id: string, dto: VPN4GLineUpdateRequest) =>
    api.put<VPN4GLineResponse>(`/lines/4g-vpn/${id}`, dto).then(d),
  terminate: (id: string) =>
    api.patch(`/lines/4g-vpn/${id}/terminate`).then(() => undefined as void),
  delete: (id: string) =>
    api.delete(`/lines/4g-vpn/${id}`).then(() => undefined as void),
}

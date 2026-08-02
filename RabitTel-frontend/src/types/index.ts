export * from './enums'

import { ContractStatus, LineStatus, LineType, UserRole } from './enums'

// ─── Agency ─────────────────────────────────────────────────────────────────
export interface AgencyResponse {
  id: string
  name: string
  directorateCode: string
  region: string
  active: boolean
  creationDate: string
  lastModificationDate: string
  linesCount: number
}
export interface AgencyCreateRequest {
  name: string
  directorateCode: string
  region: string
}
export interface AgencyUpdateRequest {
  name: string
  directorateCode: string
  region: string
  active: boolean
}

// ─── Plan ────────────────────────────────────────────────────────────────────
export interface PlanResponse {
  id: string
  name: string
  price: number
  description: string | null
  active: boolean
  creationDate: string
  linesCount: number
}
export interface PlanCreateRequest {
  name: string
  price: number
  description?: string
}
export interface PlanUpdateRequest {
  name: string
  price: number
  description?: string
  active: boolean
}

// ─── Contract ────────────────────────────────────────────────────────────────
export interface ContractResponse {
  id: string
  startDate: string
  durationMonths: number
  endDate: string
  status: ContractStatus
  creationDate: string
  linesCount: number
}
export interface ContractCreateRequest {
  startDate: string
  durationMonths: number
}
export interface ContractRenewalRequest {
  newDurationMonths: number
}

// ─── Line base ───────────────────────────────────────────────────────────────
export interface LineResponse {
  id: string
  lineNumber: string
  lineType: LineType
  lineStatus: LineStatus
  contractualAmount: number
  agencyId: string
  agencyName: string
  planId: string
  planName: string
  contractId: string | null
  contractEndDate: string | null
  createdBy: string | null
  creationDate: string
  lastModificationDate: string
}

// ─── FTTH ───────────────────────────────────────────────────────────────────
export interface FTTHLineResponse extends LineResponse {
  fixedLineNumber: string
  routerBrand: string
  bandwidth: number | string
}
export interface FTTHLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  fixedLineNumber: string
  routerBrand: string
  bandwidth: number
  lineType: LineType
  lineStatus: LineStatus
}
export interface FTTHLineUpdateRequest {
  lineNumber?: string
  lineType?: LineType
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
  fixedLineNumber?: string
  routerBrand?: string
  bandwidth?: number
}

// ─── RTC ───────────────────────────────────────────────────────────────────
export type RTCLineResponse = LineResponse
export interface RTCCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  lineType: LineType
  lineStatus: LineStatus
}
export interface RTCUpdateRequest {
  lineNumber?: string
  lineType?: LineType
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
}
export type RTCLineCreateRequest = RTCCreateRequest
export type RTCLineUpdateRequest = RTCUpdateRequest

// ─── VPN ADSL ────────────────────────────────────────────────────────────
export interface VPNLineResponse extends LineResponse {
  bandwidth: number | string
  ipAddress: string
}
export interface VPNLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  bandwidth: number
  ipAddress: string
  lineType: LineType
  lineStatus: LineStatus
}
export interface VPNLineUpdateRequest {
  lineNumber?: string
  lineType?: LineType
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
  bandwidth?: number
  ipAddress?: string
}

// ─── GSM Pro ────────────────────────────────────────────────────────────
export interface GSMLineResponse extends LineResponse {
  serviceFunction: string
  chipSerialNumber: string
  chipDeliveryDate: string
  pinCode: string
  pukCode: string
}
export interface GSMLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  serviceFunction: string
  chipSerialNumber: string
  chipDeliveryDate: string
  pinCode: string
  pukCode: string
  lineType: LineType
  lineStatus: LineStatus
}
export interface GSMLineUpdateRequest {
  lineNumber?: string
  lineType?: LineType
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
  serviceFunction?: string
  chipSerialNumber?: string
  chipDeliveryDate?: string
  pinCode?: string
  pukCode?: string
}

// ─── 4G Internet ────────────────────────────────────────────────────────────
export interface Internet4GLineResponse extends LineResponse {
  serviceFunction: string
  simSerialNumber: string
  pinCode: string
  pukCode: string
  equipment: string
  equipmentSerialNumber: string
  bandwidth: number | string
}
export interface Internet4GLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  serviceFunction: string
  simSerialNumber: string
  pinCode: string
  pukCode: string
  equipment: string
  equipmentSerialNumber: string
  bandwidth: number
  lineType: LineType
  lineStatus: LineStatus
}
export interface Internet4GLineUpdateRequest {
  lineNumber?: string
  lineType?: LineType
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
  serviceFunction?: string
  simSerialNumber?: string
  pinCode?: string
  pukCode?: string
  equipment?: string
  equipmentSerialNumber?: string
  bandwidth?: number
}

// ─── VPN 4G ────────────────────────────────────────────────────────────
export interface VPN4GLineResponse extends LineResponse {
  equipment: string
  ipAddress: string
  serialNumber: string
  deliveryDate: string
}
export interface VPN4GLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  equipment: string
  ipAddress: string
  serialNumber: string
  deliveryDate: string
  lineType: LineType
  lineStatus: LineStatus
}
export interface VPN4GLineUpdateRequest {
  lineNumber?: string
  lineType?: LineType
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
  equipment?: string
  ipAddress?: string
  serialNumber?: string
  deliveryDate?: string
}

// ─── User / Auth ─────────────────────────────────────────────────────────────
export interface UserResponse {
  id: string
  username: string
  firstName: string
  lastName: string
  email: string
  phoneNumber: string
  role: UserRole
  isActive: boolean
  createdAt: string
  lastLoginAt: string | null
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  firstLogin: boolean
  user: UserResponse
}

export interface AdminCreateUserRequest {
  firstName: string
  lastName: string
  email: string
  phoneNumber: string
  role: UserRole
}

export interface AdminUpdateUserRequest {
  role: UserRole
  isActive: boolean
}

export interface UserUpdateRequest {
  firstName: string
  lastName: string
  email: string
  phoneNumber: string
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

export interface ForgotPasswordRequest {
  email: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  first: boolean
  last: boolean
}

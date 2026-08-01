export * from './enums'

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
import { ContractStatus } from './enums'
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
import { LineStatus, LineType } from './enums'
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
  creationDate: string
  lastModificationDate: string
}

// FTTH
export interface FTTHLineResponse extends LineResponse {
  fixedLineNumber: string
  routerBrand: string
  bandwidth: string
}
export interface FTTHLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  lineType: LineType
  lineStatus: LineStatus
  fixedLineNumber: string
  routerBrand: string
  bandwidth: number   // long en backend
}
export interface FTTHLineUpdateRequest {
  lineNumber?: string
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
  fixedLineNumber?: string
  routerBrand?: string
  bandwidth: number   // long primitif — toujours envoyer une valeur, jamais undefined
}

// RTC
export type RTCLineResponse = LineResponse
export interface RTCLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  lineType: LineType         // required: always RTC
  lineStatus: LineStatus     // required: always ACTIVE on creation
}
export interface RTCLineUpdateRequest {
  lineNumber?: string
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
}

// VPN ADSL
export interface VPNLineResponse extends LineResponse {
  bandwidth: string
  ipAddress: string
}
export interface VPNLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  lineType: LineType
  lineStatus: LineStatus
  bandwidth: number   // long en backend
  ipAddress: string
}
export interface VPNLineUpdateRequest {
  lineNumber?: string
  lineStatus?: LineStatus
  contractualAmount?: number
  agencyId?: string
  planId?: string
  contractId?: string
  bandwidth: number   // long primitif — toujours envoyer une valeur, jamais undefined
  ipAddress?: string
}

// GSM Pro
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
  lineType: LineType         // required: always GSM_PRO
  lineStatus: LineStatus     // required: always ACTIVE on creation
  serviceFunction: string
  chipSerialNumber: string
  chipDeliveryDate: string
  pinCode: string
  pukCode: string
}
export interface GSMLineUpdateRequest {
  lineNumber?: string
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

// 4G Internet
export interface Internet4GLineResponse extends LineResponse {
  serviceFunction: string
  simSerialNumber: string
  pinCode: string
  pukCode: string
  equipment: string
  equipmentSerialNumber: string
  bandwidth: string
}
export interface Internet4GLineCreateRequest {
  lineNumber: string
  contractualAmount: number
  agencyId: string
  planId: string
  lineType: LineType
  lineStatus: LineStatus
  serviceFunction: string
  simSerialNumber: string
  pinCode: string
  pukCode: string
  equipment: string
  equipmentSerialNumber: string
  bandwidth: number   // long en backend
}
export interface Internet4GLineUpdateRequest {
  lineNumber?: string
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
  bandwidth: number   // long primitif — toujours envoyer une valeur, jamais undefined
}

// VPN 4G
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
  lineType: LineType         // required: always G4_VPN
  lineStatus: LineStatus     // required: always ACTIVE on creation
  equipment: string
  ipAddress: string
  serialNumber: string
  deliveryDate: string
}
export interface VPN4GLineUpdateRequest {
  lineNumber?: string
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

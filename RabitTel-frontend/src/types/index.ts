export * from "./enums";

import { ContractStatus, LineStatus, LineType, UserRole } from "./enums";

// ─── Agency ─────────────────────────────────────────────────────────────────
export interface AgencyResponse {
  id: string;
  name: string;
  directorateCode: string;
  region: string;
  active: boolean;
  creationDate: string;
  lastModificationDate: string;
  linesCount: number;
}
export interface AgencyCreateRequest {
  name: string;
  directorateCode: string;
  region: string;
}
export interface AgencyUpdateRequest {
  name: string;
  directorateCode: string;
  region: string;
  active: boolean;
}

// ─── Plan ────────────────────────────────────────────────────────────────────
export interface PlanResponse {
  id: string;
  name: string;
  price: number;
  description: string | null;
  active: boolean;
  creationDate: string;
  linesCount: number;
}
export interface PlanCreateRequest {
  name: string;
  price: number;
  description?: string;
}
export interface PlanUpdateRequest {
  name: string;
  price: number;
  description?: string;
  active: boolean;
}

// ─── Contract ────────────────────────────────────────────────────────────────
export interface ContractResponse {
  id: string;
  startDate: string;
  durationMonths: number;
  endDate: string;
  status: ContractStatus;
  creationDate: string;
  linesCount: number;
}
export interface ContractCreateRequest {
  startDate: string;
  durationMonths: number;
}
export interface ContractRenewalRequest {
  newDurationMonths: number;
}

// ─── Line base ───────────────────────────────────────────────────────────────
export interface LineResponse {
  id: string;
  lineNumber: string;
  lineType: LineType;
  lineStatus: LineStatus;
  contractualAmount: number;
  agencyId: string;
  agencyName: string;
  contractId: string | null;
  contractEndDate: string | null;
  createdBy: string | null;
  creationDate: string;
  lastModificationDate: string;
}

// ─── FTTH ───────────────────────────────────────────────────────────────────
export interface FTTHLineResponse extends LineResponse {
  fixedLineNumber: string;
  routerBrand: string;
  bandwidth: string;
}
export interface FTTHLineCreateRequest {
  lineNumber: string;
  contractualAmount: number;
  agencyId: string;
  fixedLineNumber: string;
  routerBrand: string;
  bandwidth: string;
  lineType: LineType;
  lineStatus: LineStatus;
}
export interface FTTHLineUpdateRequest {
  lineNumber?: string;
  lineType?: LineType;
  lineStatus?: LineStatus;
  contractualAmount?: number;
  agencyId?: string;
  contractId?: string;
  fixedLineNumber?: string;
  routerBrand?: string;
  bandwidth?: string;
}

// ─── RTC ───────────────────────────────────────────────────────────────────
export type RTCLineResponse = LineResponse;
export interface RTCCreateRequest {
  lineNumber: string;
  contractualAmount: number;
  agencyId: string;
  lineType: LineType;
  lineStatus: LineStatus;
}
export interface RTCUpdateRequest {
  lineNumber?: string;
  lineType?: LineType;
  lineStatus?: LineStatus;
  contractualAmount?: number;
  agencyId?: string;
  contractId?: string;
}
export type RTCLineCreateRequest = RTCCreateRequest;
export type RTCLineUpdateRequest = RTCUpdateRequest;

// ─── Data Lines ────────────────────────────────────────────────────────────
export interface DataLineResponse extends LineResponse {
  bandwidth: string;
  ipAddress: string;
}
export interface DataLineCreateRequest {
  lineNumber: string;
  contractualAmount: number;
  agencyId: string;
  bandwidth: string;
  ipAddress: string;
  lineType: LineType;
  lineStatus: LineStatus;
}
export interface DataLineUpdateRequest {
  lineNumber?: string;
  lineType?: LineType;
  lineStatus?: LineStatus;
  contractualAmount?: number;
  agencyId?: string;
  contractId?: string;
  bandwidth?: string;
  ipAddress?: string;
}

// ─── GSM Pro ────────────────────────────────────────────────────────────
export interface GSMLineResponse extends LineResponse {
  planId: string;
  planName: string;
  serviceFunction: string;
  chipSerialNumber: string;
  chipDeliveryDate: string;
  pinCode: string;
  pukCode: string;
}
export interface GSMLineCreateRequest {
  lineNumber: string;
  contractualAmount: number;
  agencyId: string;
  planId: string;
  serviceFunction: string;
  chipSerialNumber: string;
  chipDeliveryDate: string;
  pinCode: string;
  pukCode: string;
  lineType: LineType;
  lineStatus: LineStatus;
}
export interface GSMLineUpdateRequest {
  lineNumber?: string;
  lineType?: LineType;
  lineStatus?: LineStatus;
  contractualAmount?: number;
  agencyId?: string;
  planId?: string;
  contractId?: string;
  serviceFunction?: string;
  chipSerialNumber?: string;
  chipDeliveryDate?: string;
  pinCode?: string;
  pukCode?: string;
}

// ─── 4G Internet ────────────────────────────────────────────────────────────
export interface Internet4GLineResponse extends LineResponse {
  serviceFunction: string;
  simSerialNumber: string;
  pinCode: string;
  pukCode: string;
  equipment: string;
  equipmentSerialNumber: string;
  bandwidth: string;
}
export interface Internet4GLineCreateRequest {
  lineNumber: string;
  contractualAmount: number;
  agencyId: string;
  serviceFunction: string;
  simSerialNumber: string;
  pinCode: string;
  pukCode: string;
  equipment: string;
  equipmentSerialNumber: string;
  bandwidth: string;
  lineType: LineType;
  lineStatus: LineStatus;
}
export interface Internet4GLineUpdateRequest {
  lineNumber?: string;
  lineType?: LineType;
  lineStatus?: LineStatus;
  contractualAmount?: number;
  agencyId?: string;
  contractId?: string;
  serviceFunction?: string;
  simSerialNumber?: string;
  pinCode?: string;
  pukCode?: string;
  equipment?: string;
  equipmentSerialNumber?: string;
  bandwidth?: string;
}

// ─── VPN 4G ────────────────────────────────────────────────────────────
export interface VPN4GLineResponse extends LineResponse {
  equipment: string;
  ipAddress: string;
  serialNumber: string;
  deliveryDate: string;
}
export interface VPN4GLineCreateRequest {
  lineNumber: string;
  contractualAmount: number;
  agencyId: string;
  equipment: string;
  ipAddress: string;
  serialNumber: string;
  deliveryDate: string;
  lineType: LineType;
  lineStatus: LineStatus;
}
export interface VPN4GLineUpdateRequest {
  lineNumber?: string;
  lineType?: LineType;
  lineStatus?: LineStatus;
  contractualAmount?: number;
  agencyId?: string;
  contractId?: string;
  equipment?: string;
  ipAddress?: string;
  serialNumber?: string;
  deliveryDate?: string;
}

// ─── User / Auth ─────────────────────────────────────────────────────────────
export interface UserResponse {
  id: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  role: UserRole;
  isActive: boolean;
  firstLogin: boolean;
  createdAt: string;
  lastLoginAt: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  firstLogin: boolean;
  user: UserResponse;
}

export interface AdminCreateUserRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  role: UserRole;
}

export interface AdminUpdateUserRequest {
  role: UserRole;
  isActive: boolean;
}

export interface UserUpdateRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

// ─── Notifications ───────────────────────────────────────────────────────────
export type NotificationChannel = 'EMAIL' | 'IN_APP' | 'SMS' | 'WHATSAPP' | 'MICROSOFT_TEAMS';
export type NotificationStatus  = 'PENDING' | 'SENT' | 'FAILED' | 'RETRYING' | 'CANCELLED';
export type NotificationType =
  | 'CREATE_USER' | 'RESET_PASSWORD' | 'CHANGE_PASSWORD'
  | 'ACCOUNT_LOCKED' | 'ACCOUNT_UNLOCKED' | 'ACCOUNT_ACTIVATED' | 'ACCOUNT_DEACTIVATED'
  | 'LINE_CREATED' | 'LINE_UPDATED' | 'LINE_DELETED' | 'LINE_ASSIGNED' | 'LINE_UNASSIGNED'
  | 'CONTRACT_CREATED' | 'CONTRACT_UPDATED' | 'CONTRACT_EXPIRED' | 'CONTRACT_EXPIRING'
  | 'IMPORT_STARTED' | 'IMPORT_COMPLETED' | 'IMPORT_FAILED'
  | 'SYSTEM_NOTIFICATION';

export interface NotificationDeliveryResponse {
  id: string;
  channel: NotificationChannel;
  recipient: string;
  subject: string | null;
  body: string | null;
  status: NotificationStatus;
  read: boolean;
  readAt: string | null;
  sentAt: string | null;
  errorMessage: string | null;
}

export interface NotificationResponse {
  id: string;
  type: NotificationType;
  resourceType: string;
  resourceId: string;
  createdAt: string;
  deliveryCount: number;
  deliveries: NotificationDeliveryResponse[];
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

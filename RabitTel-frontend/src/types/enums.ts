export enum ContractStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  EXPIRED = 'EXPIRED',
  RENEWED = 'RENEWED',
}

export enum LineStatus {
  ACTIVE = 'ACTIVE',
  SUSPENDED = 'SUSPENDED',
  TERMINATED = 'TERMINATED',
  TRANSFERRED = 'TRANSFERRED',
}

export enum LineType {
  FTTH = 'FTTH',
  RTC = 'RTC',
  VPN_ADSL = 'VPN_ADSL',
  G4 = 'G4',
  G4_VPN = 'G4_VPN',
  GSM_PRO = 'GSM_PRO',
}

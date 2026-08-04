export enum ContractStatus {
  IN_PROGRESS = "IN_PROGRESS",
  EXPIRED = "EXPIRED",
  RENEWED = "RENEWED",
}

export enum LineStatus {
  ACTIVE = "ACTIVE",
  SUSPENDED = "SUSPENDED",
  TERMINATED = "TERMINATED",
  TRANSFERRED = "TRANSFERRED",
}

export enum LineType {
  FTTH = "FTTH",
  RTC = "RTC",
  VPN_ADSL = "VPN_ADSL",
  ADSL = "ADSL",
  LLI = "LLI",
  VPN_LL = "VPN_LL",
  G4 = "G4",
  G4_VPN = "G4_VPN",
  GSM_PRO = "GSM_PRO",
}

export enum UserRole {
  ADMIN = "ADMIN",
  MEMBER = "MEMBER",
}

export const FTTH_BANDWIDTH_OPTIONS = ["1Go", "200Mo", "100Mo"] as const;
export type FTTHBandwidth = (typeof FTTH_BANDWIDTH_OPTIONS)[number];

export const INTERNET_4G_BANDWIDTH_OPTIONS = ["40Go", "70Go", "90Go"] as const;
export type Internet4GBandwidth =
  (typeof INTERNET_4G_BANDWIDTH_OPTIONS)[number];

export const ADSL_BANDWIDTH_OPTIONS = ["4M", "12M", "20M"] as const;
export type ADSLBandwidth = (typeof ADSL_BANDWIDTH_OPTIONS)[number];

export const DEDICATED_LINE_BANDWIDTH_OPTIONS = [
  "8M",
  "10M",
  "20M",
  "50M",
] as const;
export type DedicatedLineBandwidth =
  (typeof DEDICATED_LINE_BANDWIDTH_OPTIONS)[number];

export function getDataLineBandwidthOptions(
  lineType: LineType,
): readonly string[] {
  switch (lineType) {
    case LineType.ADSL:
    case LineType.VPN_ADSL:
      return ADSL_BANDWIDTH_OPTIONS;
    case LineType.LLI:
    case LineType.VPN_LL:
      return DEDICATED_LINE_BANDWIDTH_OPTIONS;
    default:
      return [];
  }
}

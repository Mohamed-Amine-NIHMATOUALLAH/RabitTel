import { LineType } from '../../types'
import { DataLineTypePage } from './DataLines'

export default function VPNLLLines() {
  return (
    <DataLineTypePage
      lineType={LineType.VPN_LL}
      title="Lignes VPN LL"
      queryKey="data-vpn-ll"
    />
  )
}

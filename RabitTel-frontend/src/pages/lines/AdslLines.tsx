import { LineType } from '../../types'
import { DataLineTypePage } from './DataLines'

export default function AdslLines() {
  return (
    <DataLineTypePage
      lineType={LineType.ADSL}
      title="Lignes ADSL"
      queryKey="data-adsl"
    />
  )
}

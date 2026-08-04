import { LineType } from '../../types'
import { DataLineTypePage } from './DataLines'

export default function LLILines() {
  return (
    <DataLineTypePage
      lineType={LineType.LLI}
      title="Lignes LLI"
      queryKey="data-lli"
    />
  )
}

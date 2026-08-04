import { useState } from 'react'
import { dataLineService } from '../../services'
import type { DataLineResponse, DataLineCreateRequest, DataLineUpdateRequest, AgencyResponse, ContractResponse } from '../../types'
import { LineStatus, LineType, getDataLineBandwidthOptions } from '../../types'
import LinePage from './LinePage'
import { useRefData } from './useRefData'
import ErrorMsg from '../../components/ErrorMsg'

interface DataLineTypePageProps {
  lineType: LineType
  title: string
  queryKey: string
}

function CreateForm({
  lineType,
  onSubmit,
  isPending,
  error,
  onCancel,
}: {
  lineType: LineType
  onSubmit: (dto: DataLineCreateRequest) => void
  isPending: boolean
  error: unknown
  onCancel: () => void
}) {
  const { agencies } = useRefData()
  const bandwidthOptions = getDataLineBandwidthOptions(lineType)
  const [f, setF] = useState({ lineNumber: '', contractualAmount: '', agencyId: '', bandwidth: '', ipAddress: '' })
  const [err, setErr] = useState('')

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!f.lineNumber || !f.contractualAmount || !f.agencyId || !f.bandwidth || !f.ipAddress) {
      setErr('Tous les champs sont obligatoires'); return
    }
    onSubmit({
      ...f,
      contractualAmount: Number(f.contractualAmount),
      bandwidth: f.bandwidth,
      lineType,
      lineStatus: LineStatus.ACTIVE,
    })
  }

  return (
    <form onSubmit={handleSubmit}>
      <div><label>Type de ligne<br /><input value={lineType} readOnly /></label></div>
      <div><label>Numéro de ligne<br /><input value={f.lineNumber} onChange={e => setF(p => ({ ...p, lineNumber: e.target.value }))} /></label></div>
      <div><label>Montant contractuel<br /><input type="number" step="0.01" value={f.contractualAmount} onChange={e => setF(p => ({ ...p, contractualAmount: e.target.value }))} /></label></div>
      <div><label>Direction (Agence)<br /><select value={f.agencyId} onChange={e => setF(p => ({ ...p, agencyId: e.target.value }))}>
        <option value="">-- sélectionner --</option>
        {agencies.map((a: AgencyResponse) => <option key={a.id} value={a.id}>{a.name}</option>)}
      </select></label></div>
      <div><label>Débit<br /><select value={f.bandwidth} onChange={e => setF(p => ({ ...p, bandwidth: e.target.value }))}>
        <option value="">-- sélectionner --</option>
        {bandwidthOptions.map(bw => <option key={bw} value={bw}>{bw}</option>)}
      </select></label></div>
      <div><label>Adresse IP<br /><input value={f.ipAddress} placeholder="ex: 192.168.1.1" onChange={e => setF(p => ({ ...p, ipAddress: e.target.value }))} /></label></div>
      {err && <p style={{ color: 'red' }}>{err}</p>}
      <ErrorMsg error={error} />
      <div style={{ marginTop: 8 }}>
        <button type="submit" disabled={isPending}>{isPending ? 'Enregistrement…' : 'Enregistrer'}</button>
        {' '}<button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}

function UpdateForm({
  lineType,
  initial,
  onSubmit,
  isPending,
  error,
  onCancel,
}: {
  lineType: LineType
  initial: DataLineResponse
  onSubmit: (dto: DataLineUpdateRequest) => void
  isPending: boolean
  error: unknown
  onCancel: () => void
}) {
  const { agencies, contracts } = useRefData()
  const bandwidthOptions = getDataLineBandwidthOptions(lineType)
  const [f, setF] = useState({
    lineNumber: initial.lineNumber,
    lineStatus: initial.lineStatus,
    contractualAmount: String(initial.contractualAmount),
    agencyId: initial.agencyId,
    contractId: initial.contractId ?? '',
    bandwidth: initial.bandwidth ?? '',
    ipAddress: initial.ipAddress,
  })

  return (
    <form onSubmit={e => {
      e.preventDefault()
      if (!f.bandwidth) { alert('Le débit est obligatoire'); return }
      onSubmit({
        ...f,
        contractualAmount: Number(f.contractualAmount),
        bandwidth: f.bandwidth,
        contractId: f.contractId || undefined,
        lineType,
      })
    }}>
      <div><label>Type de ligne<br /><input value={lineType} readOnly /></label></div>
      <div><label>Numéro de ligne<br /><input value={f.lineNumber} onChange={e => setF(p => ({ ...p, lineNumber: e.target.value }))} /></label></div>
      <div><label>État<br /><select value={f.lineStatus} onChange={e => setF(p => ({ ...p, lineStatus: e.target.value as LineStatus }))}>
        {Object.values(LineStatus).map((s: string) => <option key={s} value={s}>{s}</option>)}
      </select></label></div>
      <div><label>Montant contractuel<br /><input type="number" step="0.01" value={f.contractualAmount} onChange={e => setF(p => ({ ...p, contractualAmount: e.target.value }))} /></label></div>
      <div><label>Direction (Agence)<br /><select value={f.agencyId} onChange={e => setF(p => ({ ...p, agencyId: e.target.value }))}>
        {agencies.map((a: AgencyResponse) => <option key={a.id} value={a.id}>{a.name}</option>)}
      </select></label></div>
      <div><label>Contrat (optionnel)<br /><select value={f.contractId} onChange={e => setF(p => ({ ...p, contractId: e.target.value }))}>
        <option value="">Aucun</option>
        {contracts.map((c: ContractResponse) => <option key={c.id} value={c.id}>{c.id.slice(0, 8)} — {c.endDate}</option>)}
      </select></label></div>
      <div><label>Débit<br /><select value={f.bandwidth} onChange={e => setF(p => ({ ...p, bandwidth: e.target.value }))}>
        {bandwidthOptions.map(bw => <option key={bw} value={bw}>{bw}</option>)}
      </select></label></div>
      <div><label>Adresse IP<br /><input value={f.ipAddress} onChange={e => setF(p => ({ ...p, ipAddress: e.target.value }))} /></label></div>
      <ErrorMsg error={error} />
      <div style={{ marginTop: 8 }}>
        <button type="submit" disabled={isPending}>{isPending ? 'Enregistrement…' : 'Enregistrer'}</button>
        {' '}<button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}

export function DataLineTypePage({ lineType, title, queryKey }: DataLineTypePageProps) {
  return (
    <LinePage<DataLineResponse, DataLineCreateRequest, DataLineUpdateRequest>
      title={title}
      queryKey={queryKey}
      fetchAll={() => dataLineService.getAllByType(lineType)}
      fetchBillable={() => dataLineService.getBillableByType(lineType)}
      createFn={dataLineService.create}
      updateFn={dataLineService.update}
      terminateFn={dataLineService.terminate}
      deleteFn={dataLineService.delete}
      extraColumns={[
        { header: 'Débit', cell: (r: DataLineResponse) => r.bandwidth },
        { header: 'Adresse IP', cell: (r: DataLineResponse) => r.ipAddress },
      ]}
      CreateForm={({ onSubmit, isPending, error, onCancel }) => (
        <CreateForm
          lineType={lineType}
          onSubmit={onSubmit}
          isPending={isPending}
          error={error}
          onCancel={onCancel}
        />
      )}
      UpdateForm={({ initial, onSubmit, isPending, error, onCancel }) => (
        <UpdateForm
          lineType={lineType}
          initial={initial}
          onSubmit={onSubmit}
          isPending={isPending}
          error={error}
          onCancel={onCancel}
        />
      )}
    />
  )
}

export default function VPNAdslLines() {
  return (
    <DataLineTypePage
      lineType={LineType.VPN_ADSL}
      title="Lignes VPN ADSL"
      queryKey="data-vpn-adsl"
    />
  )
}

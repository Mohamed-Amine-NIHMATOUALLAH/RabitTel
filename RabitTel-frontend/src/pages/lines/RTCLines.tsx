import { useState } from 'react'
import { rtcService } from '../../services'
import type { RTCLineResponse, RTCLineCreateRequest, RTCLineUpdateRequest, AgencyResponse, PlanResponse, ContractResponse } from '../../types'
import { LineStatus, LineType } from '../../types'
import LinePage from './LinePage'
import { useRefData } from './useRefData'
import ErrorMsg from '../../components/ErrorMsg'

function CreateForm({ onSubmit, isPending, error, onCancel }: {
  onSubmit: (dto: RTCLineCreateRequest) => void
  isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies, plans } = useRefData()
  const [f, setF] = useState({ lineNumber: '', contractualAmount: '', agencyId: '', planId: '' })
  const [err, setErr] = useState('')
  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!f.lineNumber || !f.contractualAmount || !f.agencyId || !f.planId) { setErr('Tous les champs sont obligatoires'); return }
    onSubmit({ lineNumber: f.lineNumber, contractualAmount: Number(f.contractualAmount), agencyId: f.agencyId, planId: f.planId, lineType: LineType.RTC, lineStatus: LineStatus.ACTIVE })
  }
  return (
    <form onSubmit={handleSubmit}>
      <div><label>Numéro de ligne<br /><input value={f.lineNumber} onChange={e => setF(p => ({ ...p, lineNumber: e.target.value }))} /></label></div>
      <div><label>Montant contractuel<br /><input type="number" step="0.01" value={f.contractualAmount} onChange={e => setF(p => ({ ...p, contractualAmount: e.target.value }))} /></label></div>
      <div><label>Direction (Agence)<br /><select value={f.agencyId} onChange={e => setF(p => ({ ...p, agencyId: e.target.value }))}>
        <option value="">-- sélectionner --</option>
        {agencies.map((a: AgencyResponse) => <option key={a.id} value={a.id}>{a.name}</option>)}
      </select></label></div>
      <div><label>Forfait<br /><select value={f.planId} onChange={e => setF(p => ({ ...p, planId: e.target.value }))}>
        <option value="">-- sélectionner --</option>
        {plans.map((pl: PlanResponse) => <option key={pl.id} value={pl.id}>{pl.name}</option>)}
      </select></label></div>
      {err && <p style={{ color: 'red' }}>{err}</p>}
      <ErrorMsg error={error} />
      <div style={{ marginTop: 8 }}>
        <button type="submit" disabled={isPending}>{isPending ? 'Enregistrement…' : 'Enregistrer'}</button>
        {' '}<button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}

function UpdateForm({ initial, onSubmit, isPending, error, onCancel }: {
  initial: RTCLineResponse; onSubmit: (dto: RTCLineUpdateRequest) => void
  isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies, plans, contracts } = useRefData()
  const [f, setF] = useState({
    lineNumber: initial.lineNumber,
    lineStatus: initial.lineStatus,
    contractualAmount: String(initial.contractualAmount),
    agencyId: initial.agencyId,
    planId: initial.planId,
    contractId: initial.contractId ?? '',
  })
  return (
    <form onSubmit={e => { e.preventDefault(); onSubmit({ ...f, contractualAmount: Number(f.contractualAmount), contractId: f.contractId || undefined }) }}>
      <div><label>Numéro de ligne<br /><input value={f.lineNumber} onChange={e => setF(p => ({ ...p, lineNumber: e.target.value }))} /></label></div>
      <div><label>État<br /><select value={f.lineStatus} onChange={e => setF(p => ({ ...p, lineStatus: e.target.value as LineStatus }))}>
        {Object.values(LineStatus).map((s: string) => <option key={s} value={s}>{s}</option>)}
      </select></label></div>
      <div><label>Montant contractuel<br /><input type="number" step="0.01" value={f.contractualAmount} onChange={e => setF(p => ({ ...p, contractualAmount: e.target.value }))} /></label></div>
      <div><label>Direction (Agence)<br /><select value={f.agencyId} onChange={e => setF(p => ({ ...p, agencyId: e.target.value }))}>
        {agencies.map((a: AgencyResponse) => <option key={a.id} value={a.id}>{a.name}</option>)}
      </select></label></div>
      <div><label>Forfait<br /><select value={f.planId} onChange={e => setF(p => ({ ...p, planId: e.target.value }))}>
        {plans.map((pl: PlanResponse) => <option key={pl.id} value={pl.id}>{pl.name}</option>)}
      </select></label></div>
      <div><label>Contrat (optionnel)<br /><select value={f.contractId} onChange={e => setF(p => ({ ...p, contractId: e.target.value }))}>
        <option value="">Aucun</option>
        {contracts.map((c: ContractResponse) => <option key={c.id} value={c.id}>{c.id.slice(0, 8)} — {c.endDate}</option>)}
      </select></label></div>
      <ErrorMsg error={error} />
      <div style={{ marginTop: 8 }}>
        <button type="submit" disabled={isPending}>{isPending ? 'Enregistrement…' : 'Enregistrer'}</button>
        {' '}<button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}

export default function RTCLines() {
  return (
    <LinePage<RTCLineResponse, RTCLineCreateRequest, RTCLineUpdateRequest>
      title="Lignes RTC"
      queryKey="rtc"
      fetchAll={rtcService.getAll}
      fetchBillable={rtcService.getBillable}
      createFn={rtcService.create}
      updateFn={rtcService.update}
      terminateFn={rtcService.terminate}
      deleteFn={rtcService.delete}
      extraColumns={[]}
      CreateForm={CreateForm}
      UpdateForm={UpdateForm}
    />
  )
}

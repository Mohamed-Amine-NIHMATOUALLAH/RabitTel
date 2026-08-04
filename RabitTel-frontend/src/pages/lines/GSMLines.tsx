import { useState } from 'react'
import { gsmService } from '../../services'
import type { GSMLineResponse, GSMLineCreateRequest, GSMLineUpdateRequest, AgencyResponse, PlanResponse, ContractResponse } from '../../types'
import { LineStatus, LineType } from '../../types'
import LinePage from './LinePage'
import { useRefData } from './useRefData'
import ErrorMsg from '../../components/ErrorMsg'

function CreateForm({ onSubmit, isPending, error, onCancel }: {
  onSubmit: (dto: GSMLineCreateRequest) => void; isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies, plans } = useRefData()
  const [f, setF] = useState({
    lineNumber: '', contractualAmount: '', agencyId: '', planId: '',
    serviceFunction: '', chipSerialNumber: '', chipDeliveryDate: '', pinCode: '', pukCode: ''
  })
  const [err, setErr] = useState('')
  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!f.lineNumber || !f.contractualAmount || !f.agencyId || !f.planId || !f.serviceFunction || !f.chipSerialNumber || !f.chipDeliveryDate || !f.pinCode || !f.pukCode) {
      setErr('Tous les champs sont obligatoires'); return
    }
    onSubmit({ ...f, contractualAmount: Number(f.contractualAmount), lineType: LineType.GSM_PRO, lineStatus: LineStatus.ACTIVE })
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
      <div><label>Affectation / Fonction<br /><input value={f.serviceFunction} onChange={e => setF(p => ({ ...p, serviceFunction: e.target.value }))} /></label></div>
      <div><label>N° Série de la puce<br /><input value={f.chipSerialNumber} onChange={e => setF(p => ({ ...p, chipSerialNumber: e.target.value }))} /></label></div>
      <div><label>Date de livraison puce<br /><input type="date" value={f.chipDeliveryDate} onChange={e => setF(p => ({ ...p, chipDeliveryDate: e.target.value }))} /></label></div>
      <div><label>Code PIN<br /><input value={f.pinCode} onChange={e => setF(p => ({ ...p, pinCode: e.target.value }))} /></label></div>
      <div><label>Code PUK<br /><input value={f.pukCode} onChange={e => setF(p => ({ ...p, pukCode: e.target.value }))} /></label></div>
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
  initial: GSMLineResponse; onSubmit: (dto: GSMLineUpdateRequest) => void; isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies, plans, contracts } = useRefData()
  const [f, setF] = useState({
    lineNumber: initial.lineNumber, lineStatus: initial.lineStatus,
    contractualAmount: String(initial.contractualAmount),
    agencyId: initial.agencyId, planId: initial.planId,
    contractId: initial.contractId ?? '',
    serviceFunction: initial.serviceFunction, chipSerialNumber: initial.chipSerialNumber,
    chipDeliveryDate: initial.chipDeliveryDate?.slice(0, 10) ?? '',
    pinCode: initial.pinCode, pukCode: initial.pukCode,
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
      <div><label>Affectation / Fonction<br /><input value={f.serviceFunction} onChange={e => setF(p => ({ ...p, serviceFunction: e.target.value }))} /></label></div>
      <div><label>N° Série de la puce<br /><input value={f.chipSerialNumber} onChange={e => setF(p => ({ ...p, chipSerialNumber: e.target.value }))} /></label></div>
      <div><label>Date de livraison puce<br /><input type="date" value={f.chipDeliveryDate} onChange={e => setF(p => ({ ...p, chipDeliveryDate: e.target.value }))} /></label></div>
      <div><label>Code PIN<br /><input value={f.pinCode} onChange={e => setF(p => ({ ...p, pinCode: e.target.value }))} /></label></div>
      <div><label>Code PUK<br /><input value={f.pukCode} onChange={e => setF(p => ({ ...p, pukCode: e.target.value }))} /></label></div>
      <ErrorMsg error={error} />
      <div style={{ marginTop: 8 }}>
        <button type="submit" disabled={isPending}>{isPending ? 'Enregistrement…' : 'Enregistrer'}</button>
        {' '}<button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}

export default function GSMLines() {
  return (
    <LinePage<GSMLineResponse, GSMLineCreateRequest, GSMLineUpdateRequest>
      title="Lignes GSM Pro"
      queryKey="gsm"
      fetchAll={gsmService.getAll}
      fetchBillable={gsmService.getBillable}
      createFn={gsmService.create}
      updateFn={gsmService.update}
      terminateFn={gsmService.terminate}
      deleteFn={gsmService.delete}
      extraColumns={[
        { header: 'Forfait', cell: (r: GSMLineResponse) => r.planName },
        { header: 'Affectation / Fonction', cell: (r: GSMLineResponse) => r.serviceFunction },
        { header: 'N° Série puce', cell: (r: GSMLineResponse) => r.chipSerialNumber },
        { header: 'Date livraison puce', cell: (r: GSMLineResponse) => r.chipDeliveryDate?.slice(0, 10) },
        { header: 'Code PIN', cell: (r: GSMLineResponse) => r.pinCode },
        { header: 'Code PUK', cell: (r: GSMLineResponse) => r.pukCode },
      ]}
      CreateForm={CreateForm}
      UpdateForm={UpdateForm}
    />
  )
}

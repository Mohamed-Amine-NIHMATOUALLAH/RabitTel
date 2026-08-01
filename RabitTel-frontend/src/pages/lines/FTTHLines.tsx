import { useState } from 'react'
import { ftthService } from '../../services'
import type { FTTHLineResponse, FTTHLineCreateRequest, FTTHLineUpdateRequest, AgencyResponse, PlanResponse, ContractResponse } from '../../types'
import { LineStatus, LineType } from '../../types'
import LinePage from './LinePage'
import { useRefData } from './useRefData'
import ErrorMsg from '../../components/ErrorMsg'

function CreateForm({ onSubmit, isPending, error, onCancel }: {
  onSubmit: (dto: FTTHLineCreateRequest) => void
  isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies, plans } = useRefData()
  const [f, setF] = useState({ lineNumber: '', contractualAmount: '', agencyId: '', planId: '', fixedLineNumber: '', routerBrand: '', bandwidth: '' })
  const [err, setErr] = useState('')
  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!f.lineNumber || !f.contractualAmount || !f.agencyId || !f.planId || !f.fixedLineNumber || !f.routerBrand || !f.bandwidth) {
      setErr('Tous les champs sont obligatoires'); return
    }
    onSubmit({ ...f, contractualAmount: Number(f.contractualAmount), lineType: LineType.FTTH, lineStatus: LineStatus.ACTIVE })
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
      <div><label>ND Fixe<br /><input value={f.fixedLineNumber} onChange={e => setF(p => ({ ...p, fixedLineNumber: e.target.value }))} /></label></div>
      <div><label>Marque routeur<br /><input value={f.routerBrand} onChange={e => setF(p => ({ ...p, routerBrand: e.target.value }))} /></label></div>
      <div><label>Débit<br /><input value={f.bandwidth} placeholder="ex: 100Mo, 1Go" onChange={e => setF(p => ({ ...p, bandwidth: e.target.value }))} /></label></div>
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
  initial: FTTHLineResponse; onSubmit: (dto: FTTHLineUpdateRequest) => void
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
    fixedLineNumber: initial.fixedLineNumber,
    routerBrand: initial.routerBrand,
    bandwidth: initial.bandwidth,
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
      <div><label>ND Fixe<br /><input value={f.fixedLineNumber} onChange={e => setF(p => ({ ...p, fixedLineNumber: e.target.value }))} /></label></div>
      <div><label>Marque routeur<br /><input value={f.routerBrand} onChange={e => setF(p => ({ ...p, routerBrand: e.target.value }))} /></label></div>
      <div><label>Débit<br /><input value={f.bandwidth} onChange={e => setF(p => ({ ...p, bandwidth: e.target.value }))} /></label></div>
      <ErrorMsg error={error} />
      <div style={{ marginTop: 8 }}>
        <button type="submit" disabled={isPending}>{isPending ? 'Enregistrement…' : 'Enregistrer'}</button>
        {' '}<button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}

export default function FTTHLines() {
  return (
    <LinePage<FTTHLineResponse, FTTHLineCreateRequest, FTTHLineUpdateRequest>
      title="Lignes FTTH"
      queryKey="ftth"
      fetchAll={ftthService.getAll}
      fetchBillable={ftthService.getBillable}
      createFn={ftthService.create}
      updateFn={ftthService.update}
      terminateFn={ftthService.terminate}
      deleteFn={ftthService.delete}
      extraColumns={[
        { header: 'ND Fixe', cell: (r: FTTHLineResponse) => r.fixedLineNumber },
        { header: 'Marque routeur', cell: (r: FTTHLineResponse) => r.routerBrand },
        { header: 'Débit', cell: (r: FTTHLineResponse) => r.bandwidth },
      ]}
      CreateForm={CreateForm}
      UpdateForm={UpdateForm}
    />
  )
}

import { useState } from 'react'
import { vpnService } from '../../services'
import type { VPNLineResponse, VPNLineCreateRequest, VPNLineUpdateRequest, AgencyResponse, PlanResponse, ContractResponse } from '../../types'
import { LineStatus, LineType } from '../../types'
import LinePage from './LinePage'
import { useRefData } from './useRefData'
import ErrorMsg from '../../components/ErrorMsg'

function CreateForm({ onSubmit, isPending, error, onCancel }: {
  onSubmit: (dto: VPNLineCreateRequest) => void; isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies, plans } = useRefData()
  const [f, setF] = useState({ lineNumber: '', contractualAmount: '', agencyId: '', planId: '', bandwidth: '', ipAddress: '' })
  const [err, setErr] = useState('')
  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!f.lineNumber || !f.contractualAmount || !f.agencyId || !f.planId || !f.bandwidth || !f.ipAddress) {
      setErr('Tous les champs sont obligatoires'); return
    }
    onSubmit({ ...f, contractualAmount: Number(f.contractualAmount), bandwidth: Number(f.bandwidth), lineType: LineType.VPN_ADSL, lineStatus: LineStatus.ACTIVE })
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
      <div><label>Débit (Kbps)<br /><input type="number" value={f.bandwidth} placeholder="ex: 512, 1024" onChange={e => setF(p => ({ ...p, bandwidth: e.target.value }))} /></label></div>
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

function UpdateForm({ initial, onSubmit, isPending, error, onCancel }: {
  initial: VPNLineResponse; onSubmit: (dto: VPNLineUpdateRequest) => void; isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies, plans, contracts } = useRefData()
  const [f, setF] = useState({
    lineNumber: initial.lineNumber, lineStatus: initial.lineStatus,
    contractualAmount: String(initial.contractualAmount),
    agencyId: initial.agencyId, planId: initial.planId,
    contractId: initial.contractId ?? '',
    bandwidth: initial.bandwidth != null ? String(initial.bandwidth) : '0',
    ipAddress: initial.ipAddress,
  })
  return (
    <form onSubmit={e => {
      e.preventDefault()
      const bw = Number(f.bandwidth)
      if (!bw || bw <= 0) { alert('Le débit doit être un nombre positif'); return }
      onSubmit({ ...f, contractualAmount: Number(f.contractualAmount), bandwidth: bw, contractId: f.contractId || undefined })
    }}>
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
      <div><label>Débit (Kbps)<br /><input type="number" value={f.bandwidth} onChange={e => setF(p => ({ ...p, bandwidth: e.target.value }))} /></label></div>
      <div><label>Adresse IP<br /><input value={f.ipAddress} onChange={e => setF(p => ({ ...p, ipAddress: e.target.value }))} /></label></div>
      <ErrorMsg error={error} />
      <div style={{ marginTop: 8 }}>
        <button type="submit" disabled={isPending}>{isPending ? 'Enregistrement…' : 'Enregistrer'}</button>
        {' '}<button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}

export default function VPNLines() {
  return (
    <LinePage<VPNLineResponse, VPNLineCreateRequest, VPNLineUpdateRequest>
      title="Lignes VPN ADSL"
      queryKey="vpn"
      fetchAll={vpnService.getAll}
      fetchBillable={vpnService.getBillable}
      createFn={vpnService.create}
      updateFn={vpnService.update}
      terminateFn={vpnService.terminate}
      deleteFn={vpnService.delete}
      extraColumns={[
        { header: 'Débit', cell: (r: VPNLineResponse) => r.bandwidth },
        { header: 'Adresse IP', cell: (r: VPNLineResponse) => r.ipAddress },
      ]}
      CreateForm={CreateForm}
      UpdateForm={UpdateForm}
    />
  )
}

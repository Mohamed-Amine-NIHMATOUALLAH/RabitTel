import { useState } from 'react'
import { vpn4GService } from '../../services'
import type { VPN4GLineResponse, VPN4GLineCreateRequest, VPN4GLineUpdateRequest, AgencyResponse, ContractResponse } from '../../types'
import { LineStatus, LineType } from '../../types'
import LinePage from './LinePage'
import { useRefData } from './useRefData'
import ErrorMsg from '../../components/ErrorMsg'

function CreateForm({ onSubmit, isPending, error, onCancel }: {
  onSubmit: (dto: VPN4GLineCreateRequest) => void; isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies } = useRefData()
  const [f, setF] = useState({
    lineNumber: '', contractualAmount: '', agencyId: '',
    equipment: '', ipAddress: '', serialNumber: '', deliveryDate: ''
  })
  const [err, setErr] = useState('')
  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!f.lineNumber || !f.contractualAmount || !f.agencyId || !f.equipment || !f.ipAddress || !f.serialNumber || !f.deliveryDate) {
      setErr('Tous les champs sont obligatoires'); return
    }
    onSubmit({ ...f, contractualAmount: Number(f.contractualAmount), lineType: LineType.G4_VPN, lineStatus: LineStatus.ACTIVE })
  }
  return (
    <form onSubmit={handleSubmit}>
      <div><label>Numéro de ligne<br /><input value={f.lineNumber} onChange={e => setF(p => ({ ...p, lineNumber: e.target.value }))} /></label></div>
      <div><label>Montant contractuel<br /><input type="number" step="0.01" value={f.contractualAmount} onChange={e => setF(p => ({ ...p, contractualAmount: e.target.value }))} /></label></div>
      <div><label>Direction (Agence)<br /><select value={f.agencyId} onChange={e => setF(p => ({ ...p, agencyId: e.target.value }))}>
        <option value="">-- sélectionner --</option>
        {agencies.map((a: AgencyResponse) => <option key={a.id} value={a.id}>{a.name}</option>)}
      </select></label></div>
      <div><label>Équipement<br /><input value={f.equipment} onChange={e => setF(p => ({ ...p, equipment: e.target.value }))} /></label></div>
      <div><label>Adresse IP<br /><input value={f.ipAddress} placeholder="ex: 192.168.1.1" onChange={e => setF(p => ({ ...p, ipAddress: e.target.value }))} /></label></div>
      <div><label>N° Série<br /><input value={f.serialNumber} onChange={e => setF(p => ({ ...p, serialNumber: e.target.value }))} /></label></div>
      <div><label>Date de livraison<br /><input type="date" value={f.deliveryDate} onChange={e => setF(p => ({ ...p, deliveryDate: e.target.value }))} /></label></div>
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
  initial: VPN4GLineResponse; onSubmit: (dto: VPN4GLineUpdateRequest) => void; isPending: boolean; error: unknown; onCancel: () => void
}) {
  const { agencies, contracts } = useRefData()
  const [f, setF] = useState({
    lineNumber: initial.lineNumber, lineStatus: initial.lineStatus,
    contractualAmount: String(initial.contractualAmount),
    agencyId: initial.agencyId,
    contractId: initial.contractId ?? '',
    equipment: initial.equipment, ipAddress: initial.ipAddress,
    serialNumber: initial.serialNumber,
    deliveryDate: initial.deliveryDate?.slice(0, 10) ?? '',
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
      <div><label>Contrat (optionnel)<br /><select value={f.contractId} onChange={e => setF(p => ({ ...p, contractId: e.target.value }))}>
        <option value="">Aucun</option>
        {contracts.map((c: ContractResponse) => <option key={c.id} value={c.id}>{c.id.slice(0, 8)} — {c.endDate}</option>)}
      </select></label></div>
      <div><label>Équipement<br /><input value={f.equipment} onChange={e => setF(p => ({ ...p, equipment: e.target.value }))} /></label></div>
      <div><label>Adresse IP<br /><input value={f.ipAddress} onChange={e => setF(p => ({ ...p, ipAddress: e.target.value }))} /></label></div>
      <div><label>N° Série<br /><input value={f.serialNumber} onChange={e => setF(p => ({ ...p, serialNumber: e.target.value }))} /></label></div>
      <div><label>Date de livraison<br /><input type="date" value={f.deliveryDate} onChange={e => setF(p => ({ ...p, deliveryDate: e.target.value }))} /></label></div>
      <ErrorMsg error={error} />
      <div style={{ marginTop: 8 }}>
        <button type="submit" disabled={isPending}>{isPending ? 'Enregistrement…' : 'Enregistrer'}</button>
        {' '}<button type="button" onClick={onCancel}>Annuler</button>
      </div>
    </form>
  )
}

export default function VPN4GLines() {
  return (
    <LinePage<VPN4GLineResponse, VPN4GLineCreateRequest, VPN4GLineUpdateRequest>
      title="Lignes 4G VPN"
      queryKey="4g-vpn"
      fetchAll={vpn4GService.getAll}
      fetchBillable={vpn4GService.getBillable}
      createFn={vpn4GService.create}
      updateFn={vpn4GService.update}
      terminateFn={vpn4GService.terminate}
      deleteFn={vpn4GService.delete}
      extraColumns={[
        { header: 'Équipement', cell: (r: VPN4GLineResponse) => r.equipment },
        { header: 'Adresse IP', cell: (r: VPN4GLineResponse) => r.ipAddress },
        { header: 'N° Série', cell: (r: VPN4GLineResponse) => r.serialNumber },
        { header: 'Date livraison', cell: (r: VPN4GLineResponse) => r.deliveryDate?.slice(0, 10) },
      ]}
      CreateForm={CreateForm}
      UpdateForm={UpdateForm}
    />
  )
}

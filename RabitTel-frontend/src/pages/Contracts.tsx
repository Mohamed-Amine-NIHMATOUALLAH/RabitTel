import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { contractService } from '../services'
import type { ContractResponse, ContractCreateRequest, ContractRenewalRequest } from '../types'
import { ContractStatus } from '../types'
import ErrorMsg from '../components/ErrorMsg'
import ConfirmDialog from '../components/ConfirmDialog'
import { useAuth, isAdmin } from '../auth/AuthContext'

type Mode = 'list' | 'create' | 'renew'

export default function Contracts() {
  const qc = useQueryClient()
  const { user } = useAuth()
  const admin = isAdmin(user)

  const [mode, setMode] = useState<Mode>('list')
  const [selected, setSelected] = useState<ContractResponse | null>(null)
  const [confirmId, setConfirmId] = useState<string | null>(null)
  const [tab, setTab] = useState<'all' | 'active' | 'expired' | 'expiring'>('all')

  const [filterStatus, setFilterStatus] = useState('')
  const [startFrom, setStartFrom] = useState('')
  const [startTo, setStartTo] = useState('')
  const [endFrom, setEndFrom] = useState('')
  const [endTo, setEndTo] = useState('')
  const [daysThreshold, setDaysThreshold] = useState('30')

  const params = {
    ...(filterStatus ? { status: filterStatus as ContractStatus } : {}),
    ...(startFrom ? { startDateFrom: startFrom } : {}),
    ...(startTo ? { startDateTo: startTo } : {}),
    ...(endFrom ? { endDateFrom: endFrom } : {}),
    ...(endTo ? { endDateTo: endTo } : {}),
  }

  const allQ = useQuery({ queryKey: ['contracts', params], queryFn: () => contractService.getAll(params), enabled: tab === 'all' })
  const activeQ = useQuery({ queryKey: ['contracts-active'], queryFn: () => contractService.getActive(), enabled: tab === 'active' })
  const expiredQ = useQuery({ queryKey: ['contracts-expired'], queryFn: () => contractService.getExpired(), enabled: tab === 'expired' })
  const expiringQ = useQuery({ queryKey: ['contracts-expiring', daysThreshold], queryFn: () => contractService.getExpiring(Number(daysThreshold)), enabled: tab === 'expiring' })

  const currentData = tab === 'all' ? allQ.data : tab === 'active' ? activeQ.data : tab === 'expired' ? expiredQ.data : expiringQ.data
  const isLoading = allQ.isLoading || activeQ.isLoading || expiredQ.isLoading || expiringQ.isLoading
  const error = allQ.error ?? activeQ.error ?? expiredQ.error ?? expiringQ.error

  const createMut = useMutation({
    mutationFn: (dto: ContractCreateRequest) => contractService.create(dto),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['contracts'] }); setMode('list') },
  })
  const renewMut = useMutation({
    mutationFn: ({ id, dto }: { id: string; dto: ContractRenewalRequest }) => contractService.renew(id, dto),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['contracts'] }); setMode('list') },
  })
  const deleteMut = useMutation({
    mutationFn: (id: string) => contractService.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['contracts'] }),
  })

  const [form, setForm] = useState({ startDate: '', durationMonths: '' })
  const [renewMonths, setRenewMonths] = useState('')
  const [formErr, setFormErr] = useState('')

  function openCreate() {
    setForm({ startDate: '', durationMonths: '' })
    setFormErr('')
    setMode('create')
  }
  function openRenew(c: ContractResponse) {
    setSelected(c)
    setRenewMonths('')
    setFormErr('')
    setMode('renew')
  }

  // Create — ADMIN only
  if (admin && mode === 'create') {
    return (
      <div>
        <h1>Create Contract</h1>
        <form onSubmit={e => {
          e.preventDefault()
          if (!form.startDate || !form.durationMonths) { setFormErr('All fields required'); return }
          createMut.mutate({ startDate: form.startDate, durationMonths: Number(form.durationMonths) })
        }}>
          <div><label>Start Date<br /><input type="date" value={form.startDate} onChange={e => setForm(f => ({ ...f, startDate: e.target.value }))} /></label></div>
          <div><label>Duration (months)<br /><input type="number" min={1} value={form.durationMonths} onChange={e => setForm(f => ({ ...f, durationMonths: e.target.value }))} /></label></div>
          {formErr && <p style={{ color: 'red' }}>{formErr}</p>}
          <ErrorMsg error={createMut.error} />
          <div style={{ marginTop: 8 }}>
            <button type="submit" disabled={createMut.isPending}>{createMut.isPending ? 'Saving…' : 'Save'}</button>
            {' '}<button type="button" onClick={() => setMode('list')}>Cancel</button>
          </div>
        </form>
      </div>
    )
  }

  // Renew — ADMIN only
  if (admin && mode === 'renew' && selected) {
    return (
      <div>
        <h1>Renew Contract</h1>
        <p>Contract: {selected.id.slice(0, 8)}… | Ends: {selected.endDate}</p>
        <form onSubmit={e => {
          e.preventDefault()
          if (!renewMonths) { setFormErr('Duration required'); return }
          renewMut.mutate({ id: selected.id, dto: { newDurationMonths: Number(renewMonths) } })
        }}>
          <div><label>New Duration (months)<br /><input type="number" min={1} value={renewMonths} onChange={e => setRenewMonths(e.target.value)} /></label></div>
          {formErr && <p style={{ color: 'red' }}>{formErr}</p>}
          <ErrorMsg error={renewMut.error} />
          <div style={{ marginTop: 8 }}>
            <button type="submit" disabled={renewMut.isPending}>{renewMut.isPending ? 'Saving…' : 'Renew'}</button>
            {' '}<button type="button" onClick={() => setMode('list')}>Cancel</button>
          </div>
        </form>
      </div>
    )
  }

  return (
    <div>
      <h1>Contracts</h1>
      <div style={{ marginBottom: 8 }}>
        <button onClick={() => setTab('all')}>{tab === 'all' ? '[All]' : 'All'}</button>{' '}
        <button onClick={() => setTab('active')}>{tab === 'active' ? '[Active]' : 'Active'}</button>{' '}
        <button onClick={() => setTab('expired')}>{tab === 'expired' ? '[Expired]' : 'Expired'}</button>{' '}
        <button onClick={() => setTab('expiring')}>{tab === 'expiring' ? '[Expiring]' : 'Expiring'}</button>{' '}
        {/* Create — ADMIN only */}
        {admin && <button onClick={openCreate}>+ New Contract</button>}
      </div>

      {tab === 'all' && (
        <div style={{ marginBottom: 8 }}>
          <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)} style={{ marginRight: 4 }}>
            <option value="">All statuses</option>
            {Object.values(ContractStatus).map(s => <option key={s} value={s}>{s}</option>)}
          </select>
          <input type="date" value={startFrom} onChange={e => setStartFrom(e.target.value)} style={{ marginRight: 4 }} title="Start from" />
          <input type="date" value={startTo} onChange={e => setStartTo(e.target.value)} style={{ marginRight: 4 }} title="Start to" />
          <input type="date" value={endFrom} onChange={e => setEndFrom(e.target.value)} style={{ marginRight: 4 }} title="End from" />
          <input type="date" value={endTo} onChange={e => setEndTo(e.target.value)} style={{ marginRight: 4 }} title="End to" />
        </div>
      )}

      {tab === 'expiring' && (
        <div style={{ marginBottom: 8 }}>
          <label>Days threshold: <input type="number" value={daysThreshold} onChange={e => setDaysThreshold(e.target.value)} style={{ width: 60 }} /></label>
        </div>
      )}

      {isLoading && <p>Loading…</p>}
      <ErrorMsg error={error} />
      <ErrorMsg error={deleteMut.error} />

      <table border={1} cellPadding={6} style={{ borderCollapse: 'collapse', width: '100%' }}>
        <thead>
          <tr>
            <th>ID</th><th>Start</th><th>End</th><th>Duration</th><th>Status</th><th>Lines</th>
            {admin && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {currentData?.map((c: ContractResponse) => (
            <tr key={c.id}>
              <td title={c.id}>{c.id.slice(0, 8)}…</td>
              <td>{c.startDate}</td>
              <td>{c.endDate}</td>
              <td>{c.durationMonths} mo</td>
              <td>{c.status}</td>
              <td>{c.linesCount}</td>
              {admin && (
                <td>
                  <button onClick={() => openRenew(c)}>Renew</button>
                  {' '}
                  <button onClick={() => setConfirmId(c.id)}>Delete</button>
                </td>
              )}
            </tr>
          ))}
          {currentData?.length === 0 && <tr><td colSpan={admin ? 7 : 6}>No contracts found</td></tr>}
        </tbody>
      </table>

      {admin && confirmId && (
        <ConfirmDialog
          message="Delete this contract?"
          onConfirm={() => { deleteMut.mutate(confirmId); setConfirmId(null) }}
          onCancel={() => setConfirmId(null)}
        />
      )}
    </div>
  )
}

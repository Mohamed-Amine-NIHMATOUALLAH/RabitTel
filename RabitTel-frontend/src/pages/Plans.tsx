import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { planService } from '../services'
import type { PlanResponse, PlanCreateRequest, PlanUpdateRequest } from '../types'
import ErrorMsg from '../components/ErrorMsg'
import ConfirmDialog from '../components/ConfirmDialog'
import { useAuth, isAdmin } from '../auth/AuthContext'

type Mode = 'list' | 'create' | 'edit'

export default function Plans() {
  const qc = useQueryClient()
  const { user } = useAuth()
  const admin = isAdmin(user)

  const [mode, setMode] = useState<Mode>('list')
  const [selected, setSelected] = useState<PlanResponse | null>(null)
  const [confirmId, setConfirmId] = useState<string | null>(null)

  const [filterName, setFilterName] = useState('')
  const [filterActive, setFilterActive] = useState('')
  const [filterPriceFrom, setFilterPriceFrom] = useState('')
  const [filterPriceTo, setFilterPriceTo] = useState('')

  const params = {
    ...(filterName ? { name: filterName } : {}),
    ...(filterActive !== '' ? { active: filterActive === 'true' } : {}),
    ...(filterPriceFrom ? { priceFrom: Number(filterPriceFrom) } : {}),
    ...(filterPriceTo ? { priceTo: Number(filterPriceTo) } : {}),
  }

  const { data, isLoading, error } = useQuery({
    queryKey: ['plans', params],
    queryFn: () => planService.getAll(params),
  })

  const createMut = useMutation({
    mutationFn: (dto: PlanCreateRequest) => planService.create(dto),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['plans'] }); setMode('list') },
  })
  const updateMut = useMutation({
    mutationFn: ({ id, dto }: { id: string; dto: PlanUpdateRequest }) => planService.update(id, dto),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['plans'] }); setMode('list') },
  })
  const deleteMut = useMutation({
    mutationFn: (id: string) => planService.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['plans'] }),
  })

  const [form, setForm] = useState({ name: '', price: '', description: '', active: true })
  const [formErr, setFormErr] = useState('')

  function openCreate() {
    setForm({ name: '', price: '', description: '', active: true })
    setFormErr('')
    setMode('create')
  }
  function openEdit(p: PlanResponse) {
    setSelected(p)
    setForm({ name: p.name, price: String(p.price), description: p.description ?? '', active: p.active })
    setFormErr('')
    setMode('edit')
  }
  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!form.name || !form.price) { setFormErr('Name and price are required'); return }
    if (mode === 'create') {
      createMut.mutate({ name: form.name, price: Number(form.price), description: form.description || undefined })
    } else if (mode === 'edit' && selected) {
      updateMut.mutate({ id: selected.id, dto: { name: form.name, price: Number(form.price), description: form.description || undefined, active: form.active } })
    }
  }

  // Create / Edit form — ADMIN only
  if (admin && mode !== 'list') {
    return (
      <div>
        <h1>{mode === 'create' ? 'Create Plan' : 'Edit Plan'}</h1>
        <form onSubmit={handleSubmit}>
          <div><label>Name<br /><input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} /></label></div>
          <div><label>Price<br /><input type="number" step="0.01" value={form.price} onChange={e => setForm(f => ({ ...f, price: e.target.value }))} /></label></div>
          <div><label>Description<br /><textarea value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} /></label></div>
          {mode === 'edit' && (
            <div><label><input type="checkbox" checked={form.active} onChange={e => setForm(f => ({ ...f, active: e.target.checked }))} /> Active</label></div>
          )}
          {formErr && <p style={{ color: 'red' }}>{formErr}</p>}
          <ErrorMsg error={createMut.error ?? updateMut.error} />
          <div style={{ marginTop: 8 }}>
            <button type="submit" disabled={createMut.isPending || updateMut.isPending}>
              {createMut.isPending || updateMut.isPending ? 'Saving…' : 'Save'}
            </button>
            {' '}<button type="button" onClick={() => setMode('list')}>Cancel</button>
          </div>
        </form>
      </div>
    )
  }

  return (
    <div>
      <h1>Plans</h1>
      <div style={{ marginBottom: 8 }}>
        <input placeholder="Name" value={filterName} onChange={e => setFilterName(e.target.value)} style={{ marginRight: 4 }} />
        <select value={filterActive} onChange={e => setFilterActive(e.target.value)} style={{ marginRight: 4 }}>
          <option value="">All</option><option value="true">Active</option><option value="false">Inactive</option>
        </select>
        <input placeholder="Price from" type="number" value={filterPriceFrom} onChange={e => setFilterPriceFrom(e.target.value)} style={{ marginRight: 4, width: 90 }} />
        <input placeholder="Price to" type="number" value={filterPriceTo} onChange={e => setFilterPriceTo(e.target.value)} style={{ marginRight: 8, width: 90 }} />
        {/* Create — ADMIN only */}
        {admin && <button onClick={openCreate}>+ New Plan</button>}
      </div>

      {isLoading && <p>Loading…</p>}
      <ErrorMsg error={error} />
      <ErrorMsg error={deleteMut.error} />

      <table border={1} cellPadding={6} style={{ borderCollapse: 'collapse', width: '100%' }}>
        <thead>
          <tr>
            <th>Name</th><th>Price</th><th>Description</th><th>Active</th><th>Lines</th>
            {admin && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {data?.map((p: PlanResponse) => (
            <tr key={p.id}>
              <td>{p.name}</td>
              <td>{p.price}</td>
              <td>{p.description}</td>
              <td>{p.active ? 'Yes' : 'No'}</td>
              <td>{p.linesCount}</td>
              {admin && (
                <td>
                  <button onClick={() => openEdit(p)}>Edit</button>
                  {' '}
                  <button onClick={() => setConfirmId(p.id)}>Delete</button>
                </td>
              )}
            </tr>
          ))}
          {data?.length === 0 && <tr><td colSpan={admin ? 6 : 5}>No plans found</td></tr>}
        </tbody>
      </table>

      {admin && confirmId && (
        <ConfirmDialog
          message="Delete this plan?"
          onConfirm={() => { deleteMut.mutate(confirmId); setConfirmId(null) }}
          onCancel={() => setConfirmId(null)}
        />
      )}
    </div>
  )
}

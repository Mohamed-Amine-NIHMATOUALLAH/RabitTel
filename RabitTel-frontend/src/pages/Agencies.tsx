import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { agencyService } from '../services'
import type { AgencyResponse, AgencyCreateRequest, AgencyUpdateRequest } from '../types'
import ErrorMsg from '../components/ErrorMsg'
import ConfirmDialog from '../components/ConfirmDialog'

type AnyEvent = React.FormEvent<HTMLFormElement>

type Mode = 'list' | 'create' | 'edit'

export default function Agencies() {
  const qc = useQueryClient()
  const [mode, setMode] = useState<Mode>('list')
  const [selected, setSelected] = useState<AgencyResponse | null>(null)
  const [confirmId, setConfirmId] = useState<string | null>(null)
  const [confirmAction, setConfirmAction] = useState<'deactivate' | 'delete' | null>(null)

  // list
  const [filterName, setFilterName] = useState('')
  const [filterRegion, setFilterRegion] = useState('')
  const [filterCode, setFilterCode] = useState('')
  const [filterActive, setFilterActive] = useState('')

  const params = {
    ...(filterName ? { name: filterName } : {}),
    ...(filterRegion ? { region: filterRegion } : {}),
    ...(filterCode ? { directorateCode: filterCode } : {}),
    ...(filterActive !== '' ? { active: filterActive === 'true' } : {}),
  }

  const { data, isLoading, error } = useQuery({
    queryKey: ['agencies', params],
    queryFn: () => agencyService.getAll(params),
  })

  const createMut = useMutation({
    mutationFn: (dto: AgencyCreateRequest) => agencyService.create(dto),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['agencies'] }); setMode('list') },
  })
  const updateMut = useMutation({
    mutationFn: ({ id, dto }: { id: string; dto: AgencyUpdateRequest }) => agencyService.update(id, dto),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['agencies'] }); setMode('list') },
  })
  const deactivateMut = useMutation({
    mutationFn: (id: string) => agencyService.deactivate(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['agencies'] }),
  })
  const deleteMut = useMutation({
    mutationFn: (id: string) => agencyService.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['agencies'] }),
  })

  // form state
  const [form, setForm] = useState({ name: '', directorateCode: '', region: '', active: true })
  const [formErr, setFormErr] = useState('')

  function openCreate() {
    setForm({ name: '', directorateCode: '', region: '', active: true })
    setFormErr('')
    setMode('create')
  }

  function openEdit(a: AgencyResponse) {
    setSelected(a)
    setForm({ name: a.name, directorateCode: a.directorateCode, region: a.region, active: a.active })
    setFormErr('')
    setMode('edit')
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!form.name || !form.directorateCode || !form.region) {
      setFormErr('All fields are required')
      return
    }
    if (mode === 'create') {
      createMut.mutate({ name: form.name, directorateCode: form.directorateCode, region: form.region })
    } else if (mode === 'edit' && selected) {
      updateMut.mutate({ id: selected.id, dto: form })
    }
  }

  if (mode !== 'list') {
    return (
      <div>
        <h1>{mode === 'create' ? 'Create Agency' : 'Edit Agency'}</h1>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Name<br />
              <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} />
            </label>
          </div>
          <div>
            <label>Directorate Code<br />
              <input value={form.directorateCode} onChange={e => setForm(f => ({ ...f, directorateCode: e.target.value }))} />
            </label>
          </div>
          <div>
            <label>Region<br />
              <input value={form.region} onChange={e => setForm(f => ({ ...f, region: e.target.value }))} />
            </label>
          </div>
          {mode === 'edit' && (
            <div>
              <label>
                <input type="checkbox" checked={form.active} onChange={e => setForm(f => ({ ...f, active: e.target.checked }))} />
                {' '}Active
              </label>
            </div>
          )}
          {formErr && <p style={{ color: 'red' }}>{formErr}</p>}
          <ErrorMsg error={createMut.error ?? updateMut.error} />
          <div style={{ marginTop: 8 }}>
            <button type="submit" disabled={createMut.isPending || updateMut.isPending}>
              {createMut.isPending || updateMut.isPending ? 'Saving…' : 'Save'}
            </button>
            {' '}
            <button type="button" onClick={() => setMode('list')}>Cancel</button>
          </div>
        </form>
      </div>
    )
  }

  return (
    <div>
      <h1>Agencies</h1>
      <div style={{ marginBottom: 8 }}>
        <input placeholder="Name" value={filterName} onChange={e => setFilterName(e.target.value)} style={{ marginRight: 4 }} />
        <input placeholder="Region" value={filterRegion} onChange={e => setFilterRegion(e.target.value)} style={{ marginRight: 4 }} />
        <input placeholder="Code" value={filterCode} onChange={e => setFilterCode(e.target.value)} style={{ marginRight: 4 }} />
        <select value={filterActive} onChange={e => setFilterActive(e.target.value)} style={{ marginRight: 8 }}>
          <option value="">All</option>
          <option value="true">Active</option>
          <option value="false">Inactive</option>
        </select>
        <button onClick={openCreate}>+ New Agency</button>
      </div>

      {isLoading && <p>Loading…</p>}
      <ErrorMsg error={error} />
      <ErrorMsg error={deactivateMut.error} />
      <ErrorMsg error={deleteMut.error} />

      <table border={1} cellPadding={6} style={{ borderCollapse: 'collapse', width: '100%' }}>
        <thead>
          <tr>
            <th>Name</th><th>Code</th><th>Region</th><th>Active</th><th>Lines</th><th>Created</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {data?.map((a: AgencyResponse) => (
            <tr key={a.id}>
              <td>{a.name}</td>
              <td>{a.directorateCode}</td>
              <td>{a.region}</td>
              <td>{a.active ? 'Yes' : 'No'}</td>
              <td>{a.linesCount}</td>
              <td>{a.creationDate?.slice(0, 10)}</td>
              <td>
                <button onClick={() => openEdit(a)}>Edit</button>
                {' '}
                {a.active && (
                  <button onClick={() => { setConfirmId(a.id); setConfirmAction('deactivate') }}>Deactivate</button>
                )}
                {' '}
                {!a.active && (
                  <button onClick={() => { setConfirmId(a.id); setConfirmAction('delete') }}>Delete</button>
                )}
              </td>
            </tr>
          ))}
          {data?.length === 0 && <tr><td colSpan={7}>No agencies found</td></tr>}
        </tbody>
      </table>

      {confirmId && confirmAction && (
        <ConfirmDialog
          message={`Confirm ${confirmAction} this agency?`}
          onConfirm={() => {
            if (confirmAction === 'deactivate') deactivateMut.mutate(confirmId)
            else deleteMut.mutate(confirmId)
            setConfirmId(null); setConfirmAction(null)
          }}
          onCancel={() => { setConfirmId(null); setConfirmAction(null) }}
        />
      )}
    </div>
  )
}

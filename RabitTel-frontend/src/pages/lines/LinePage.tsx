import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import type { LineResponse } from '../../types'
import { LineStatus } from '../../types'
import ErrorMsg from '../../components/ErrorMsg'
import ConfirmDialog from '../../components/ConfirmDialog'

export interface ColumnDef<T> {
  header: string
  cell: (row: T) => React.ReactNode
}

interface LinePageProps<T extends LineResponse, C, U> {
  title: string
  queryKey: string
  fetchAll: () => Promise<T[]>
  fetchBillable: () => Promise<T[]>
  createFn: (dto: C) => Promise<T>
  updateFn: (id: string, dto: U) => Promise<T>
  terminateFn: (id: string) => Promise<void>
  deleteFn: (id: string) => Promise<void>
  extraColumns: ColumnDef<T>[]
  CreateForm: React.FC<{ onSubmit: (dto: C) => void; isPending: boolean; error: unknown; onCancel: () => void }>
  UpdateForm: React.FC<{ initial: T; onSubmit: (dto: U) => void; isPending: boolean; error: unknown; onCancel: () => void }>
}

export default function LinePage<T extends LineResponse, C, U>({
  title,
  queryKey,
  fetchAll,
  fetchBillable,
  createFn,
  updateFn,
  terminateFn,
  deleteFn,
  extraColumns,
  CreateForm,
  UpdateForm,
}: LinePageProps<T, C, U>) {
  const qc = useQueryClient()
  const [mode, setMode] = useState<'list' | 'create' | 'edit'>('list')
  const [selected, setSelected] = useState<T | null>(null)
  const [confirmId, setConfirmId] = useState<string | null>(null)
  const [confirmAction, setConfirmAction] = useState<'terminate' | 'delete' | null>(null)
  const [showBillable, setShowBillable] = useState(false)
  const [filterNumber, setFilterNumber] = useState('')
  const [filterStatus, setFilterStatus] = useState('')

  const { data, isLoading, error } = useQuery({
    queryKey: [queryKey, { filterNumber, filterStatus }],
    queryFn: () => fetchAll(),
  })

  const billableQ = useQuery({
    queryKey: [queryKey + '-billable'],
    queryFn: fetchBillable,
    enabled: showBillable,
  })

  const createMut = useMutation({
    mutationFn: (dto: C) => createFn(dto),
    onSuccess: () => { qc.invalidateQueries({ queryKey: [queryKey] }); setMode('list') },
  })
  const updateMut = useMutation({
    mutationFn: ({ id, dto }: { id: string; dto: U }) => updateFn(id, dto),
    onSuccess: () => { qc.invalidateQueries({ queryKey: [queryKey] }); setMode('list') },
  })
  const terminateMut = useMutation({
    mutationFn: (id: string) => terminateFn(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: [queryKey] }),
  })
  const deleteMut = useMutation({
    mutationFn: (id: string) => deleteFn(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: [queryKey] }),
  })

  // client-side filter by lineNumber / status
  const displayed = (showBillable ? billableQ.data : data)?.filter(row => {
    const matchNum = !filterNumber || row.lineNumber.toLowerCase().includes(filterNumber.toLowerCase())
    const matchStatus = !filterStatus || row.lineStatus === filterStatus
    return matchNum && matchStatus
  }) ?? []

  if (mode === 'create') {
    return (
      <div>
        <h1>Créer {title}</h1>
        <CreateForm
          onSubmit={dto => createMut.mutate(dto)}
          isPending={createMut.isPending}
          error={createMut.error}
          onCancel={() => setMode('list')}
        />
      </div>
    )
  }

  if (mode === 'edit' && selected) {
    return (
      <div>
        <h1>Modifier {title}</h1>
        <UpdateForm
          initial={selected}
          onSubmit={dto => updateMut.mutate({ id: selected.id, dto })}
          isPending={updateMut.isPending}
          error={updateMut.error}
          onCancel={() => setMode('list')}
        />
      </div>
    )
  }

  return (
    <div>
      <h1>{title}</h1>
      <div style={{ marginBottom: 8 }}>
        <input placeholder="Numéro de ligne" value={filterNumber} onChange={e => setFilterNumber(e.target.value)} style={{ marginRight: 4 }} />
        <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)} style={{ marginRight: 8 }}>
          <option value="">Tous les états</option>
          {Object.values(LineStatus).map(s => <option key={s} value={s}>{s}</option>)}
        </select>
        <label style={{ marginRight: 8 }}>
          <input type="checkbox" checked={showBillable} onChange={e => setShowBillable(e.target.checked)} /> Facturables uniquement
        </label>
        <button onClick={() => setMode('create')}>+ Nouveau</button>
      </div>

      {(isLoading || (showBillable && billableQ.isLoading)) && <p>Chargement…</p>}
      <ErrorMsg error={error ?? billableQ.error} />
      <ErrorMsg error={terminateMut.error} />
      <ErrorMsg error={deleteMut.error} />

      <table border={1} cellPadding={6} style={{ borderCollapse: 'collapse', width: '100%' }}>
        <thead>
          <tr>
            <th>N° Ligne</th>
            <th>État</th>
            <th>Montant</th>
            <th>Direction</th>
            <th>Forfait</th>
            <th>Fin engagement</th>
            {extraColumns.map(c => <th key={c.header}>{c.header}</th>)}
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {displayed.map(row => (
            <tr key={row.id}>
              <td>{row.lineNumber}</td>
              <td>{row.lineStatus}</td>
              <td>{row.contractualAmount}</td>
              <td>{row.agencyName}</td>
              <td>{row.planName}</td>
              <td>{row.contractEndDate ?? <em style={{ color: '#999' }}>Non assigné</em>}</td>
              {extraColumns.map(c => <td key={c.header}>{c.cell(row)}</td>)}
              <td>
                <button onClick={() => { setSelected(row); setMode('edit') }}>Modifier</button>
                {' '}
                {row.lineStatus !== 'TERMINATED' && (
                  <button onClick={() => { setConfirmId(row.id); setConfirmAction('terminate') }}>Résilier</button>
                )}
                {' '}
                {row.lineStatus !== 'ACTIVE' && (
                  <button onClick={() => { setConfirmId(row.id); setConfirmAction('delete') }}>Supprimer</button>
                )}
              </td>
            </tr>
          ))}
          {displayed.length === 0 && <tr><td colSpan={7 + extraColumns.length}>Aucune ligne trouvée</td></tr>}
        </tbody>
      </table>

      {confirmId && confirmAction && (
        <ConfirmDialog
          message={`Confirmer la ${confirmAction === 'terminate' ? 'résiliation' : 'suppression'} de cette ligne ?`}
          onConfirm={() => {
            if (confirmAction === 'terminate') terminateMut.mutate(confirmId)
            else deleteMut.mutate(confirmId)
            setConfirmId(null); setConfirmAction(null)
          }}
          onCancel={() => { setConfirmId(null); setConfirmAction(null) }}
        />
      )}
    </div>
  )
}

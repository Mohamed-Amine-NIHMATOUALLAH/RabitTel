interface Props { error: unknown }

function extractMessage(error: unknown): string {
  if (!error) return ''
  if (typeof error === 'string') return error
  const e = error as { response?: { data?: { message?: string; validationErrors?: string[] } }; message?: string }
  if (e.response?.data?.validationErrors?.length) {
    return e.response.data.validationErrors.join(', ')
  }
  if (e.response?.data?.message) return e.response.data.message
  if (e.message) return e.message
  return 'An error occurred'
}

export default function ErrorMsg({ error }: Props) {
  const msg = extractMessage(error)
  if (!msg) return null
  return <p style={{ color: 'red' }}>{msg}</p>
}

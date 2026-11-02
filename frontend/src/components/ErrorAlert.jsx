export default function ErrorAlert({ error }) {
  if (!error) return null
  return <div className="alert alert-danger">{error.message || String(error)}</div>
}

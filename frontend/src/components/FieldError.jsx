// Повідомлення про помилку конкретного поля з відповіді сервера
export default function FieldError({ error, field }) {
  const message = error?.errors?.[field]
  if (!message) return null
  return <div className="invalid-feedback d-block">{message}</div>
}

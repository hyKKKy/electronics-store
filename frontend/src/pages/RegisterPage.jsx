import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import ErrorAlert from '../components/ErrorAlert'
import FieldError from '../components/FieldError'

export default function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ name: '', email: '', password: '' })
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  function update(field) {
    return (event) => setForm({ ...form, [field]: event.target.value })
  }

  async function handleSubmit(event) {
    event.preventDefault()
    setSubmitting(true)
    try {
      await register(form)
      navigate('/', { replace: true })
    } catch (err) {
      setError(err)
      setSubmitting(false)
    }
  }

  return (
    <div className="row justify-content-center">
      <div className="col-md-5">
        <h2 className="mb-3">Реєстрація</h2>
        <ErrorAlert error={error} />
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Ім'я</label>
            <input className="form-control" required value={form.name} onChange={update('name')} />
            <FieldError error={error} field="name" />
          </div>
          <div className="mb-3">
            <label className="form-label">Email</label>
            <input type="email" className="form-control" required value={form.email} onChange={update('email')} />
            <FieldError error={error} field="email" />
          </div>
          <div className="mb-3">
            <label className="form-label">Пароль</label>
            <input type="password" className="form-control" required minLength={6}
                   value={form.password} onChange={update('password')} />
            <div className="form-text">Не менше 6 символів</div>
            <FieldError error={error} field="password" />
          </div>
          <button type="submit" className="btn btn-primary" disabled={submitting}>Зареєструватися</button>
        </form>
        <p className="mt-3">
          Вже є акаунт? <Link to="/login">Увійти</Link>
        </p>
      </div>
    </div>
  )
}

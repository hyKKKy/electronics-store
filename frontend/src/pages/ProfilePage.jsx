import { useState } from 'react'
import { api } from '../api'
import { useAuth } from '../auth/AuthContext'
import ErrorAlert from '../components/ErrorAlert'
import FieldError from '../components/FieldError'

export default function ProfilePage() {
  const { user, setUser } = useAuth()

  const [name, setName] = useState(user.name)
  const [profileError, setProfileError] = useState(null)
  const [profileSaved, setProfileSaved] = useState(false)

  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '' })
  const [passwordError, setPasswordError] = useState(null)
  const [passwordSaved, setPasswordSaved] = useState(false)

  async function saveProfile(event) {
    event.preventDefault()
    setProfileSaved(false)
    try {
      setUser(await api('/users/me', { method: 'PUT', body: { name } }))
      setProfileError(null)
      setProfileSaved(true)
    } catch (err) {
      setProfileError(err)
    }
  }

  async function changePassword(event) {
    event.preventDefault()
    setPasswordSaved(false)
    try {
      await api('/users/me/password', { method: 'PUT', body: passwords })
      setPasswords({ currentPassword: '', newPassword: '' })
      setPasswordError(null)
      setPasswordSaved(true)
    } catch (err) {
      setPasswordError(err)
    }
  }

  return (
    <div className="row">
      <div className="col-md-5 mb-4">
        <h4>Профіль</h4>
        <p className="text-muted">{user.email} · {user.role === 'ADMIN' ? 'адміністратор' : 'покупець'}</p>
        <ErrorAlert error={profileError} />
        {profileSaved && <div className="alert alert-success">Збережено</div>}
        <form onSubmit={saveProfile}>
          <div className="mb-3">
            <label className="form-label">Ім'я</label>
            <input className="form-control" required value={name} onChange={(e) => setName(e.target.value)} />
            <FieldError error={profileError} field="name" />
          </div>
          <button type="submit" className="btn btn-primary">Зберегти</button>
        </form>
      </div>

      <div className="col-md-5 offset-md-1">
        <h4>Зміна пароля</h4>
        <ErrorAlert error={passwordError} />
        {passwordSaved && <div className="alert alert-success">Пароль змінено</div>}
        <form onSubmit={changePassword}>
          <div className="mb-3">
            <label className="form-label">Поточний пароль</label>
            <input type="password" className="form-control" required value={passwords.currentPassword}
                   onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })} />
          </div>
          <div className="mb-3">
            <label className="form-label">Новий пароль</label>
            <input type="password" className="form-control" required minLength={6} value={passwords.newPassword}
                   onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })} />
            <FieldError error={passwordError} field="newPassword" />
          </div>
          <button type="submit" className="btn btn-primary">Змінити пароль</button>
        </form>
      </div>
    </div>
  )
}

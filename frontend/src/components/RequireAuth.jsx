import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

// Пускає на сторінку лише авторизованих (а з adminOnly — лише адміністраторів)
export default function RequireAuth({ adminOnly = false, children }) {
  const { user, loading, isAdmin } = useAuth()
  const location = useLocation()

  if (loading) return <p>Завантаження...</p>
  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname }} />
  if (adminOnly && !isAdmin) return <div className="alert alert-warning">Недостатньо прав.</div>
  return children
}

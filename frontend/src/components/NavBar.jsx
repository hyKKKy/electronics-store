import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { useCart } from '../cart/CartContext'

export default function NavBar() {
  const { user, logout } = useAuth()
  const { count } = useCart()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <nav className="navbar navbar-expand navbar-dark bg-dark mb-4">
      <div className="container">
        <Link className="navbar-brand" to="/">Електроніка</Link>
        <ul className="navbar-nav me-auto">
          <li className="nav-item">
            <NavLink className="nav-link" to="/" end>Каталог</NavLink>
          </li>
          {user && (
            <li className="nav-item">
              <NavLink className="nav-link" to="/orders">Мої замовлення</NavLink>
            </li>
          )}
        </ul>
        <ul className="navbar-nav">
          <li className="nav-item">
            <NavLink className="nav-link" to="/cart">
              Кошик {count > 0 && <span className="badge text-bg-light">{count}</span>}
            </NavLink>
          </li>
          {user ? (
            <>
              <li className="nav-item">
                <NavLink className="nav-link" to="/profile">{user.name}</NavLink>
              </li>
              <li className="nav-item">
                <button className="btn btn-link nav-link" onClick={handleLogout}>Вийти</button>
              </li>
            </>
          ) : (
            <>
              <li className="nav-item">
                <NavLink className="nav-link" to="/login">Вхід</NavLink>
              </li>
              <li className="nav-item">
                <NavLink className="nav-link" to="/register">Реєстрація</NavLink>
              </li>
            </>
          )}
        </ul>
      </div>
    </nav>
  )
}

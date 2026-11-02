import { Link, NavLink } from 'react-router-dom'

export default function NavBar() {
  return (
    <nav className="navbar navbar-expand navbar-dark bg-dark mb-4">
      <div className="container">
        <Link className="navbar-brand" to="/">Електроніка</Link>
        <ul className="navbar-nav me-auto">
          <li className="nav-item">
            <NavLink className="nav-link" to="/" end>Каталог</NavLink>
          </li>
        </ul>
      </div>
    </nav>
  )
}

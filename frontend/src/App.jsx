import { Route, Routes } from 'react-router-dom'
import NavBar from './components/NavBar'
import RequireAuth from './components/RequireAuth'
import CatalogPage from './pages/CatalogPage'
import ProductPage from './pages/ProductPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import ProfilePage from './pages/ProfilePage'
import CartPage from './pages/CartPage'
import OrdersPage from './pages/OrdersPage'
import OrderPage from './pages/OrderPage'

export default function App() {
  return (
    <>
      <NavBar />
      <main className="container mb-5">
        <Routes>
          <Route path="/" element={<CatalogPage />} />
          <Route path="/products/:id" element={<ProductPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/profile" element={<RequireAuth><ProfilePage /></RequireAuth>} />
          <Route path="/cart" element={<CartPage />} />
          <Route path="/orders" element={<RequireAuth><OrdersPage /></RequireAuth>} />
          <Route path="/orders/:id" element={<RequireAuth><OrderPage /></RequireAuth>} />
          <Route path="*" element={<p>Сторінку не знайдено.</p>} />
        </Routes>
      </main>
    </>
  )
}

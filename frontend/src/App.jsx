import { Route, Routes } from 'react-router-dom'
import NavBar from './components/NavBar'
import CatalogPage from './pages/CatalogPage'
import ProductPage from './pages/ProductPage'

export default function App() {
  return (
    <>
      <NavBar />
      <main className="container mb-5">
        <Routes>
          <Route path="/" element={<CatalogPage />} />
          <Route path="/products/:id" element={<ProductPage />} />
          <Route path="*" element={<p>Сторінку не знайдено.</p>} />
        </Routes>
      </main>
    </>
  )
}

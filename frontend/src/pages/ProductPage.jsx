import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api'
import { useCart } from '../cart/CartContext'
import { formatPrice } from '../format'
import ErrorAlert from '../components/ErrorAlert'

export default function ProductPage() {
  const { id } = useParams()
  const [product, setProduct] = useState(null)
  const [error, setError] = useState(null)
  const [quantity, setQuantity] = useState(1)
  const [added, setAdded] = useState(false)
  const { add } = useCart()

  useEffect(() => {
    api(`/products/${id}`).then(setProduct).catch(setError)
  }, [id])

  function addToCart() {
    add(product, Math.min(quantity, product.stock))
    setAdded(true)
  }

  if (error) return <ErrorAlert error={error} />
  if (!product) return <p>Завантаження...</p>

  return (
    <div>
      <nav className="mb-3">
        <Link to="/">← До каталогу</Link>
      </nav>
      <h2>{product.name}</h2>
      <table className="table w-auto">
        <tbody>
          <tr><th>Бренд</th><td>{product.brand}</td></tr>
          <tr><th>Категорія</th><td>{product.categoryName}</td></tr>
          <tr><th>Ціна</th><td className="fw-bold">{formatPrice(product.price)}</td></tr>
          <tr>
            <th>Наявність</th>
            <td>{product.stock > 0 ? `В наявності (${product.stock} шт.)` : 'Немає в наявності'}</td>
          </tr>
        </tbody>
      </table>
      {product.description && <p>{product.description}</p>}
      {product.stock > 0 && (
        <div className="d-flex gap-2 align-items-center">
          <input type="number" className="form-control" style={{ width: 90 }} min="1" max={product.stock}
                 value={quantity} onChange={(e) => setQuantity(Math.max(1, Number(e.target.value)))} />
          <button className="btn btn-primary" onClick={addToCart}>Додати в кошик</button>
          {added && <span className="text-success">Додано</span>}
        </div>
      )}
    </div>
  )
}

import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { useAuth } from '../auth/AuthContext'
import { useCart } from '../cart/CartContext'
import { formatPrice } from '../format'
import ErrorAlert from '../components/ErrorAlert'
import FieldError from '../components/FieldError'

export default function CartPage() {
  const { user } = useAuth()
  const { items, total, setQuantity, remove, clear } = useCart()
  const navigate = useNavigate()
  const [address, setAddress] = useState('')
  const [error, setError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  async function checkout(event) {
    event.preventDefault()
    setSubmitting(true)
    try {
      const order = await api('/orders', {
        method: 'POST',
        body: {
          address,
          items: items.map((i) => ({ productId: i.productId, quantity: i.quantity })),
        },
      })
      clear()
      navigate(`/orders/${order.id}`)
    } catch (err) {
      setError(err)
      setSubmitting(false)
    }
  }

  if (items.length === 0) {
    return (
      <div>
        <h2>Кошик</h2>
        <p>Кошик порожній. <Link to="/">Перейти до каталогу</Link></p>
      </div>
    )
  }

  return (
    <div>
      <h2 className="mb-3">Кошик</h2>
      <table className="table align-middle">
        <thead>
          <tr>
            <th>Товар</th>
            <th>Ціна</th>
            <th style={{ width: 120 }}>Кількість</th>
            <th>Сума</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.productId}>
              <td><Link to={`/products/${item.productId}`}>{item.name}</Link></td>
              <td>{formatPrice(item.price)}</td>
              <td>
                <input type="number" className="form-control form-control-sm" min="1" max={item.stock}
                       value={item.quantity}
                       onChange={(e) => setQuantity(item.productId, Math.max(1, Number(e.target.value)))} />
              </td>
              <td>{formatPrice(item.price * item.quantity)}</td>
              <td className="text-end">
                <button className="btn btn-sm btn-outline-danger" onClick={() => remove(item.productId)}>
                  Видалити
                </button>
              </td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr>
            <th colSpan="3" className="text-end">Разом:</th>
            <th colSpan="2">{formatPrice(total)}</th>
          </tr>
        </tfoot>
      </table>

      <div className="row">
        <div className="col-md-6">
          <h5>Оформлення замовлення</h5>
          <ErrorAlert error={error} />
          {user ? (
            <form onSubmit={checkout}>
              <div className="mb-3">
                <label className="form-label">Адреса доставки</label>
                <textarea className="form-control" rows="2" required value={address}
                          onChange={(e) => setAddress(e.target.value)} />
                <FieldError error={error} field="address" />
              </div>
              <button type="submit" className="btn btn-success" disabled={submitting}>
                Оформити замовлення
              </button>
            </form>
          ) : (
            <p>
              Щоб оформити замовлення, <Link to="/login" state={{ from: '/cart' }}>увійдіть</Link> або{' '}
              <Link to="/register">зареєструйтеся</Link>.
            </p>
          )}
        </div>
      </div>
    </div>
  )
}

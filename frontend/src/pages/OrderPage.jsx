import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api'
import { formatDate, formatPrice } from '../format'
import { statusLabel } from '../orderStatus'
import ErrorAlert from '../components/ErrorAlert'
import StatusBadge from '../components/StatusBadge'

// Деталі замовлення та відстеження статусу
export default function OrderPage() {
  const { id } = useParams()
  const [order, setOrder] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    api(`/orders/${id}`).then(setOrder).catch(setError)
  }, [id])

  async function cancel() {
    if (!window.confirm('Скасувати замовлення?')) return
    try {
      setOrder(await api(`/orders/${id}/cancel`, { method: 'POST' }))
      setError(null)
    } catch (err) {
      setError(err)
    }
  }

  if (!order) return <ErrorAlert error={error} />

  return (
    <div>
      <nav className="mb-3">
        <Link to="/orders">← Мої замовлення</Link>
      </nav>
      <h2>
        Замовлення №{order.id} <StatusBadge status={order.status} />
      </h2>
      <p className="text-muted">
        від {formatDate(order.createdAt)} · доставка: {order.address}
      </p>
      <ErrorAlert error={error} />

      <div className="row">
        <div className="col-md-8">
          <table className="table">
            <thead>
              <tr>
                <th>Товар</th>
                <th>Ціна</th>
                <th>Кількість</th>
                <th>Сума</th>
              </tr>
            </thead>
            <tbody>
              {order.items.map((item) => (
                <tr key={item.productId}>
                  <td><Link to={`/products/${item.productId}`}>{item.productName}</Link></td>
                  <td>{formatPrice(item.price)}</td>
                  <td>{item.quantity}</td>
                  <td>{formatPrice(item.subtotal)}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <th colSpan="3" className="text-end">Разом:</th>
                <th>{formatPrice(order.totalPrice)}</th>
              </tr>
            </tfoot>
          </table>
          {order.status === 'NEW' && (
            <button className="btn btn-outline-danger" onClick={cancel}>Скасувати замовлення</button>
          )}
        </div>

        <div className="col-md-4">
          <h5>Історія статусів</h5>
          <ul className="list-group">
            {order.statusHistory.map((change, index) => (
              <li key={index} className="list-group-item d-flex justify-content-between">
                <span>{statusLabel(change.status)}</span>
                <span className="text-muted small">{formatDate(change.changedAt)}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  )
}

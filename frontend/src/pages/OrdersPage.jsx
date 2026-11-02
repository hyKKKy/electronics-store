import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'
import { formatDate, formatPrice } from '../format'
import ErrorAlert from '../components/ErrorAlert'
import StatusBadge from '../components/StatusBadge'

export default function OrdersPage() {
  const [orders, setOrders] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    api('/orders').then(setOrders).catch(setError)
  }, [])

  return (
    <div>
      <h2 className="mb-3">Мої замовлення</h2>
      <ErrorAlert error={error} />
      {orders && orders.length === 0 && <p>Замовлень ще немає.</p>}
      {orders && orders.length > 0 && (
        <table className="table">
          <thead>
            <tr>
              <th>№</th>
              <th>Дата</th>
              <th>Товарів</th>
              <th>Сума</th>
              <th>Статус</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => (
              <tr key={order.id}>
                <td><Link to={`/orders/${order.id}`}>{order.id}</Link></td>
                <td>{formatDate(order.createdAt)}</td>
                <td>{order.items.reduce((sum, i) => sum + i.quantity, 0)}</td>
                <td>{formatPrice(order.totalPrice)}</td>
                <td><StatusBadge status={order.status} /></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}

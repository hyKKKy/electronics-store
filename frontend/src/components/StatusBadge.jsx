import { ORDER_STATUSES, statusLabel } from '../orderStatus'

export default function StatusBadge({ status }) {
  const color = ORDER_STATUSES[status]?.color || 'secondary'
  return <span className={`badge text-bg-${color}`}>{statusLabel(status)}</span>
}

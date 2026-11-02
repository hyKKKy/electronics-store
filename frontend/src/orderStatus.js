// Назви та кольори статусів замовлення
export const ORDER_STATUSES = {
  NEW: { label: 'Нове', color: 'secondary' },
  CONFIRMED: { label: 'Підтверджене', color: 'primary' },
  SHIPPED: { label: 'Відправлене', color: 'info' },
  DELIVERED: { label: 'Доставлене', color: 'success' },
  CANCELLED: { label: 'Скасоване', color: 'danger' },
}

export function statusLabel(status) {
  return ORDER_STATUSES[status]?.label || status
}

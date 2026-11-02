const priceFormat = new Intl.NumberFormat('uk-UA', { minimumFractionDigits: 0, maximumFractionDigits: 2 })

export function formatPrice(value) {
  return priceFormat.format(value) + ' ₴'
}

export function formatDate(value) {
  return new Date(value).toLocaleString('uk-UA', { dateStyle: 'short', timeStyle: 'short' })
}

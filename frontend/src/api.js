// Обгортка над fetch для запитів до бекенду

export class ApiError extends Error {
  constructor(status, message, errors) {
    super(message)
    this.status = status
    // Помилки по полях форми, наприклад { email: 'Некоректний email' }
    this.errors = errors || {}
  }
}

export async function api(path, { method = 'GET', body, params } = {}) {
  let url = '/api' + path
  if (params) {
    const query = new URLSearchParams()
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') query.append(key, value)
    })
    if (query.toString()) url += '?' + query
  }

  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'

  let response
  try {
    response = await fetch(url, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    })
  } catch {
    throw new ApiError(0, 'Сервер недоступний')
  }

  if (response.status === 204) return null
  const data = await response.json().catch(() => null)
  if (!response.ok) {
    throw new ApiError(response.status, data?.message || 'Помилка сервера', data?.errors)
  }
  return data
}

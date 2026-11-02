// Обгортка над fetch для запитів до бекенду.
// JWT-токен зберігається в localStorage і додається до кожного запиту.

const TOKEN_KEY = 'token'

export function getToken() {
  try {
    return localStorage.getItem(TOKEN_KEY)
  } catch {
    return null
  }
}

export function setToken(token) {
  try {
    if (token) localStorage.setItem(TOKEN_KEY, token)
    else localStorage.removeItem(TOKEN_KEY)
  } catch {
    // localStorage недоступний (приватний режим) — токен житиме до перезавантаження
  }
}

// Викликається, коли сервер відповів 401 на запит з токеном (токен прострочений)
let onUnauthorized = () => {}
export function setUnauthorizedHandler(handler) {
  onUnauthorized = handler
}

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

  const token = getToken()
  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (token) headers['Authorization'] = 'Bearer ' + token

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
    if (response.status === 401 && token) onUnauthorized()
    throw new ApiError(response.status, data?.message || 'Помилка сервера', data?.errors)
  }
  return data
}

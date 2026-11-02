import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { api } from '../api'
import { formatPrice } from '../format'
import Pagination from '../components/Pagination'
import ErrorAlert from '../components/ErrorAlert'

const SORT_OPTIONS = [
  { value: 'id,asc', label: 'За замовчуванням' },
  { value: 'price,asc', label: 'Спочатку дешевші' },
  { value: 'price,desc', label: 'Спочатку дорожчі' },
  { value: 'name,asc', label: 'За назвою' },
]

// Фільтри зберігаються в адресі сторінки, тому посиланням можна поділитися
const FILTER_KEYS = ['search', 'categoryId', 'brand', 'minPrice', 'maxPrice', 'inStock', 'sort']

export default function CatalogPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [categories, setCategories] = useState([])
  const [brands, setBrands] = useState([])
  const [result, setResult] = useState(null)
  const [error, setError] = useState(null)

  // Значення форми до натискання "Застосувати"
  const [form, setForm] = useState(() => readForm(searchParams))

  useEffect(() => {
    api('/categories').then(setCategories).catch(setError)
    api('/products/brands').then(setBrands).catch(setError)
  }, [])

  useEffect(() => {
    const params = Object.fromEntries(searchParams)
    api('/products', { params: { ...params, size: 12 } })
      .then((data) => {
        setResult(data)
        setError(null)
      })
      .catch(setError)
  }, [searchParams])

  function applyFilters(event) {
    event.preventDefault()
    const next = {}
    FILTER_KEYS.forEach((key) => {
      if (form[key]) next[key] = form[key]
    })
    setSearchParams(next)
  }

  function resetFilters() {
    setForm(readForm(new URLSearchParams()))
    setSearchParams({})
  }

  function changePage(page) {
    const next = Object.fromEntries(searchParams)
    next.page = page
    setSearchParams(next)
  }

  function update(field) {
    return (event) => {
      const value = event.target.type === 'checkbox' ? (event.target.checked ? 'true' : '') : event.target.value
      setForm({ ...form, [field]: value })
    }
  }

  return (
    <div className="row">
      <div className="col-md-3 mb-4">
        <form onSubmit={applyFilters}>
          <h5>Фільтри</h5>
          <div className="mb-2">
            <input className="form-control" placeholder="Пошук" value={form.search} onChange={update('search')} />
          </div>
          <div className="mb-2">
            <select className="form-select" value={form.categoryId} onChange={update('categoryId')}>
              <option value="">Усі категорії</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
          <div className="mb-2">
            <select className="form-select" value={form.brand} onChange={update('brand')}>
              <option value="">Усі бренди</option>
              {brands.map((b) => (
                <option key={b} value={b}>{b}</option>
              ))}
            </select>
          </div>
          <div className="row g-2 mb-2">
            <div className="col">
              <input type="number" min="0" className="form-control" placeholder="Ціна від"
                     value={form.minPrice} onChange={update('minPrice')} />
            </div>
            <div className="col">
              <input type="number" min="0" className="form-control" placeholder="до"
                     value={form.maxPrice} onChange={update('maxPrice')} />
            </div>
          </div>
          <div className="form-check mb-2">
            <input id="inStock" type="checkbox" className="form-check-input"
                   checked={form.inStock === 'true'} onChange={update('inStock')} />
            <label htmlFor="inStock" className="form-check-label">Тільки в наявності</label>
          </div>
          <div className="mb-3">
            <select className="form-select" value={form.sort} onChange={update('sort')}>
              {SORT_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </select>
          </div>
          <button type="submit" className="btn btn-primary me-2">Застосувати</button>
          <button type="button" className="btn btn-outline-secondary" onClick={resetFilters}>Скинути</button>
        </form>
      </div>

      <div className="col-md-9">
        <ErrorAlert error={error} />
        {result && (
          <>
            <p className="text-muted">Знайдено товарів: {result.totalElements}</p>
            {result.content.length === 0 && <p>Нічого не знайдено.</p>}
            <div className="row row-cols-1 row-cols-sm-2 row-cols-lg-3 g-3 mb-3">
              {result.content.map((product) => (
                <div className="col" key={product.id}>
                  <div className="card h-100">
                    <div className="card-body">
                      <h6 className="card-title">
                        <Link to={`/products/${product.id}`}>{product.name}</Link>
                      </h6>
                      <p className="card-text text-muted small mb-1">
                        {product.brand} · {product.categoryName}
                      </p>
                      <p className="card-text fw-bold mb-1">{formatPrice(product.price)}</p>
                      {product.stock > 0
                        ? <span className="text-success small">В наявності</span>
                        : <span className="text-danger small">Немає в наявності</span>}
                    </div>
                  </div>
                </div>
              ))}
            </div>
            <Pagination page={result.page} totalPages={result.totalPages} onChange={changePage} />
          </>
        )}
      </div>
    </div>
  )
}

function readForm(searchParams) {
  const form = {}
  FILTER_KEYS.forEach((key) => {
    form[key] = searchParams.get(key) || ''
  })
  if (!form.sort) form.sort = 'id,asc'
  return form
}

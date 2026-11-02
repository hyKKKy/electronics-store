// Пагінація для відповідей виду { page, totalPages }
export default function Pagination({ page, totalPages, onChange }) {
  if (totalPages <= 1) return null
  const pages = Array.from({ length: totalPages }, (_, i) => i)

  return (
    <nav>
      <ul className="pagination">
        <li className={'page-item' + (page === 0 ? ' disabled' : '')}>
          <button className="page-link" onClick={() => onChange(page - 1)}>«</button>
        </li>
        {pages.map((p) => (
          <li key={p} className={'page-item' + (p === page ? ' active' : '')}>
            <button className="page-link" onClick={() => onChange(p)}>{p + 1}</button>
          </li>
        ))}
        <li className={'page-item' + (page >= totalPages - 1 ? ' disabled' : '')}>
          <button className="page-link" onClick={() => onChange(page + 1)}>»</button>
        </li>
      </ul>
    </nav>
  )
}

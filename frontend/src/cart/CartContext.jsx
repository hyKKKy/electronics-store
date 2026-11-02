import { createContext, useContext, useEffect, useState } from 'react'

// Кошик зберігається тільки в браузері, на сервер іде вже при оформленні замовлення
const CART_KEY = 'cart'
const CartContext = createContext(null)

function loadCart() {
  try {
    return JSON.parse(localStorage.getItem(CART_KEY)) || []
  } catch {
    return []
  }
}

export function CartProvider({ children }) {
  // Елемент кошика: { productId, name, price, quantity, stock }
  const [items, setItems] = useState(loadCart)

  useEffect(() => {
    try {
      localStorage.setItem(CART_KEY, JSON.stringify(items))
    } catch {
      // без localStorage кошик просто не переживе перезавантаження
    }
  }, [items])

  function add(product, quantity = 1) {
    setItems((current) => {
      const existing = current.find((i) => i.productId === product.id)
      if (existing) {
        return current.map((i) => i.productId === product.id
          ? { ...i, quantity: Math.min(i.quantity + quantity, product.stock) }
          : i)
      }
      return [...current, {
        productId: product.id,
        name: product.name,
        price: product.price,
        stock: product.stock,
        quantity: Math.min(quantity, product.stock),
      }]
    })
  }

  function setQuantity(productId, quantity) {
    setItems((current) => current.map((i) => i.productId === productId ? { ...i, quantity } : i))
  }

  function remove(productId) {
    setItems((current) => current.filter((i) => i.productId !== productId))
  }

  function clear() {
    setItems([])
  }

  const count = items.reduce((sum, i) => sum + i.quantity, 0)
  const total = items.reduce((sum, i) => sum + i.price * i.quantity, 0)

  return (
    <CartContext.Provider value={{ items, count, total, add, setQuantity, remove, clear }}>
      {children}
    </CartContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components
export function useCart() {
  return useContext(CartContext)
}

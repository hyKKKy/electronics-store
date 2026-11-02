# electronics-store

Онлайн-магазин електроніки (Варіант 2).

- **backend** — Spring Boot 4, Spring Security + JWT, Spring Data JPA, MySQL
- **frontend** — React + Vite

## Функціональність

- Каталог товарів з пошуком, фільтрацією (категорія, бренд, ціна, наявність), сортуванням і пагінацією
- Реєстрація, вхід (JWT), профіль користувача, керування користувачами для адміністратора
- Оформлення замовлень, історія статусів, скасування, керування статусами для адміністратора
- Облік товарів на складі: журнал руху (надходження, списання під замовлення, повернення, інвентаризація), залишки, що закінчуються, загальний стан складу

## Запуск бекенду

Потрібні Java 25+ і MySQL 8.

1. Створити базу:
   ```sql
   CREATE DATABASE electronics_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Створити файл `backend/src/main/resources/application-secret.properties` (не потрапляє в git):
   ```properties
   spring.datasource.password=пароль_від_mysql
   ```
3. Запустити:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

API доступне на `http://localhost:8080`. Таблиці створюються автоматично, при першому запуску
додаються тестові категорії, товари та адміністратор `admin@store.local` / `admin123`.

Для продакшену секрет JWT задається змінною середовища `JWT_SECRET` (Base64, мінімум 32 байти).

## Авторизація

Після реєстрації або входу сервер повертає токен. Його треба передавати в заголовку:

```
Authorization: Bearer <token>
```

Ролі: `USER` — покупець, `ADMIN` — адміністратор магазину.

## API

Усі помилки повертаються у форматі:

```json
{ "status": 400, "message": "Помилка валідації", "errors": { "email": "Некоректний email" } }
```

### Авторизація — `/api/auth` (доступно всім)

| Метод | Шлях | Опис |
|---|---|---|
| POST | `/api/auth/register` | Реєстрація `{ email, password, name }` → `{ token, user }` |
| POST | `/api/auth/login` | Вхід `{ email, password }` → `{ token, user }` |

### Каталог (читання — всім, зміна — ADMIN)

| Метод | Шлях | Опис |
|---|---|---|
| GET | `/api/categories` | Список категорій |
| GET | `/api/categories/{id}` | Категорія |
| POST / PUT / DELETE | `/api/categories[/{id}]` | Створення / зміна / видалення категорії |
| GET | `/api/products` | Пошук товарів (див. параметри нижче) |
| GET | `/api/products/brands` | Список брендів для фільтра |
| GET | `/api/products/{id}` | Товар |
| POST / PUT / DELETE | `/api/products[/{id}]` | Створення / зміна / видалення товару |

Параметри `GET /api/products`:
`search` (назва або бренд), `categoryId`, `brand`, `minPrice`, `maxPrice`, `inStock=true`,
`page`, `size` (до 50), `sort` (наприклад `price,asc`, `name,desc`).

### Профіль — `/api/users/me` (авторизований користувач)

| Метод | Шлях | Опис |
|---|---|---|
| GET | `/api/users/me` | Мій профіль |
| PUT | `/api/users/me` | Змінити ім'я `{ name }` |
| PUT | `/api/users/me/password` | Змінити пароль `{ currentPassword, newPassword }` |

### Замовлення — `/api/orders` (авторизований користувач)

| Метод | Шлях | Опис |
|---|---|---|
| POST | `/api/orders` | Оформити `{ address, items: [{ productId, quantity }] }` |
| GET | `/api/orders` | Мої замовлення |
| GET | `/api/orders/{id}` | Замовлення з позиціями та історією статусів |
| POST | `/api/orders/{id}/cancel` | Скасувати (тільки у статусі `NEW`) |

Статуси: `NEW → CONFIRMED → SHIPPED → DELIVERED`, скасування (`CANCELLED`) можливе з `NEW` і `CONFIRMED`.
При оформленні товар списується зі складу, при скасуванні — повертається.

### Адміністрування — `/api/admin/**` (тільки ADMIN)

| Метод | Шлях | Опис |
|---|---|---|
| GET | `/api/admin/users?search=` | Користувачі з пошуком і пагінацією |
| GET | `/api/admin/users/{id}` | Користувач |
| PATCH | `/api/admin/users/{id}/role` | Змінити роль `{ role }` |
| DELETE | `/api/admin/users/{id}` | Видалити (якщо немає замовлень) |
| GET | `/api/admin/orders?status=` | Усі замовлення з фільтром за статусом |
| PATCH | `/api/admin/orders/{id}/status` | Змінити статус `{ status }` |
| GET | `/api/admin/warehouse/summary` | Стан складу: кількість, вартість, відсутні товари |
| GET | `/api/admin/warehouse/low-stock?threshold=5` | Товари, що закінчуються |
| GET | `/api/admin/warehouse/movements?productId=&type=` | Журнал руху товарів |
| POST | `/api/admin/warehouse/products/{id}/income` | Надходження `{ quantity, comment }` |
| POST | `/api/admin/warehouse/products/{id}/adjustment` | Інвентаризація `{ actualStock, comment }` |

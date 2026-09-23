# Running Vantair (frontend + backend)

The storefront (this folder) and the REST backend (`../vantair-backend`) run as two
servers. The frontend server reverse-proxies `/api` to the backend, so the browser
talks to a single origin — no CORS setup needed.

## Architecture / flow diagram

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                                   BROWSER                                       │
│                                                                                 │
│   Storefront pages              Admin panel (admin.html)                        │
│   index / shop / product        ├─ Dashboard      ├─ Products (add/edit)        │
│   cart / checkout / account     ├─ Traffic 📈     ├─ Refunds ↩️                  │
│   track / wishlist / ...        └─ Orders/Customers/Delivery/OTP/Coupons/...    │
│         │                                  │                                     │
│   js/data.js  js/api.js  js/auth.js  js/otp.js  js/components.js                 │
│         │  (every page fires a fire-and-forget traffic beacon ↓)                │
└─────────┼──────────────────────────────────┼───────────────────────────────────┘
          │  all HTTP (same-origin)           │
          ▼                                   ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│            FRONTEND SERVER — server.js  (Node, zero-dep)  :5500                  │
│   • serves static files from disk                                               │
│   • reverse-proxies /api/*, /v3/api-docs, /swagger-ui* ──────────┐              │
└──────────────────────────────────────────────────────────────────┼─────────────┘
                                                                     │ /api/*
                                                                     ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│         SPRING BOOT BACKEND — vantair-api  :8080  (Swagger /swagger-ui.html)    │
│                                                                                 │
│  Controllers ─────────────▶ Services ───────────────▶ Repositories (Spring Data)│
│  Catalog / Auth / User      Catalog / User / Order      Product / User / Order   │
│  Order / Coupon / Delivery  Coupon / RefundCoupon       Coupon / RefundCoupon    │
│  Engagement / Admin         Delivery / Otp              Contact / Newsletter     │
│  Analytics (beacon)         Analytics / Engagement      Partner / PageView       │
│                                       │                                          │
└───────────────────────────────────────┼─────────────────────────────────────────┘
                                         ▼
                          ┌────────────────────────────┐
                          │   PostgreSQL  :5433/vantair │
                          │   (DataSeeder seeds catalog │
                          │    on first run)            │
                          └────────────────────────────┘
```

## 1. Start Postgres

Postgres must be running on `localhost:5433` with a `vantair` database
(user `postgres`, password `postgres`). See `../vantair-backend/README.md`.

## 2. Start the backend

```sh
cd ../vantair-backend
mvn spring-boot:run        # → http://localhost:8080  (Swagger at /swagger-ui.html)
```

## 3. Start the frontend

```sh
cd vantair
npm start                  # → http://localhost:5500   (same as: node server.js)
```

Then open **http://localhost:5500**.

### Configuration

- `PORT` — frontend port (default `5500`)
- `API` — backend base URL the proxy targets (default `http://localhost:8080`)

```sh
PORT=3000 API=http://localhost:8080 node server.js
```

The server is plain Node with **no dependencies** (`npm install` is not required).

## How the integration works

- **`js/api.js`** — fetch-based client for every backend endpoint, plus the
  browser session (`vantair_session_user`).
- **`js/data.js`** — keeps the bundled catalog as an instant-render fallback, then
  refreshes `VANTAIR.products/categories/coupons` from the API and fires a
  `vantair:catalog` event so listing pages re-render with live data. The cart and
  wishlist stay in `localStorage` (wishlist also syncs to the API when signed in).
- **`js/auth.js` / `js/otp.js`** — auth, profile, addresses, orders and the delivery
  OTP all go through the API.

If the backend is down, the storefront still renders products from the bundled
fallback data; only the dynamic flows (sign in, orders, etc.) will show an error.

## Data-flow diagrams

### A. Order + coupon / refund-coupon redemption

```
Cart page                  Checkout page                Backend (OrderService.placeOrder)
─────────                  ─────────────                ──────────────────────────────────
applyCoupon(code)
  ├─ refund coupon? ──────────────────────────────────▶ (validated again server-side)
  └─ standard coupon?
  saves vantair_order_summary
  {subtotal,discount,total,coupon}
        │
        ▼ Proceed
                           reads summary, shows totals
                           (₹0 total ⇒ payment step hidden)
                           POST /orders {couponCode, items, addressId, ...}
                                         │
                                         ▼
                              recompute subtotal from DB prices (never trust client)
                              ├─ standard coupon → discount or free shipping
                              └─ else refund coupon for THIS user & unused
                                     → flat discount, mark used after save
                              delivery = free-above-₹999 on SUBTOTAL (pre-discount)
                              persist Order (+ delivery OTP) ─▶ PostgreSQL
                                         │
                                         ▼
                           order-confirm.html?id=…  ◀── GET /orders/{id}
```

### B. Admin — add / edit product

```
admin.html (Products)        api.js                 Backend                  Postgres
────────────────────         ──────                 ───────                  ────────
"Add Product" / "Edit"
  fills modal, saveProduct()
        │
        ├─ create ─▶ adminCreateProduct ─▶ POST /admin/products ─▶ CatalogService.create ─▶ products
        └─ edit   ─▶ adminUpdateProduct ─▶ PUT  /admin/products/{id} ─▶ ...update ─────────▶ products
        │
        ▼  on success: VANTAIR.products = await api.getProducts()  (live catalog refresh)
           grid re-renders; storefront shows the change on next load
```

### C. Admin — issue refund coupon (then redeemable in flow A)

```
admin.html (Refunds)   ─▶ POST /admin/refunds {email, amount, reason}
                            └─ RefundCouponService.issue
                                 ├─ find user by email (404 if none)
                                 ├─ generate unique REF…… code
                                 └─ save RefundCoupon(user) ─▶ refund_coupons
                          GET /admin/refunds ─▶ history table (Active / Redeemed)
```

### D. Traffic & analytics

```
Every storefront page (js/data.js)            Backend                     Postgres
──────────────────────────────────            ───────                     ────────
trackPageView()  (skips admin.html)
  POST /analytics/track {path, sessionId,  ─▶ AnalyticsService.track ────▶ page_views
    userId?, device, referrer}                 (fire-and-forget, 202)

admin.html (Traffic panel + sidebar badge)
  GET /admin/analytics ──────────────────────▶ AnalyticsService.dashboard
                                                 aggregates page_views:
                                                 • active sessions / customers online (5-min window)
                                                 • views + unique visitors today
                                                 • top pages, device & referrer breakdown
                                                 • 7-day daily traffic
```

## Pages and the endpoints they use

| Page | Backend calls |
|------|---------------|
| index / shop / product / wishlist | `GET /products`, `/categories` (+ wishlist sync) |
| signup / signin | `POST /auth/signup`, `/auth/signin` |
| account | `GET /users/{id}`, profile/address ops, `GET /orders?userId=`, `reveal-otp`, cancel |
| cart → checkout | `POST /coupons/apply`, `POST /orders` |
| order-confirm / track | `GET /orders/{id}` |
| delivery-validate | `POST /delivery/{id}/validate-otp` |
| contact / partner / newsletter | `POST /contact`, `/partner`, `/newsletter` |
| every page (traffic beacon) | `POST /analytics/track` (fire-and-forget) |
| admin | `GET /admin/*`, `POST/PUT /admin/products`, `DELETE /admin/products/{id}`, `POST/GET /admin/refunds`, `GET /admin/analytics`, `PUT /orders/{id}/status` |

## Notes

- **Admin login is still client-side** (`admin` / `vantair2025`) and the admin API is
  unauthenticated — fine for local use, must be secured before any deployment.
- **Add/Edit Product** is now backed by `POST/PUT /admin/products`. Edits and new
  products refresh the live catalog immediately.
- **Refund coupons** are issued from the admin Refunds panel (`POST /admin/refunds`,
  keyed by customer email), tracked in refund history, and are **redeemable at
  checkout** — the order flow validates the code against the customer's unused
  refund coupons and marks it used once redeemed.
- **Traffic & Analytics**: every storefront page posts a fire-and-forget view via
  `js/data.js`. The admin Traffic dashboard (`GET /admin/analytics`) shows live
  visitors online, signed-in customers online, views/unique visitors today, top
  pages, device and referrer breakdowns, and a 7-day traffic chart.
- Admin password change and bulk data clear still show an explanatory message
  (no backend endpoint yet — these need admin auth first).

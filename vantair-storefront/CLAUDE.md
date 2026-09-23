# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`vantair/` is the **storefront frontend** — static multi-page HTML/CSS/vanilla-JS for an Indian D2C beauty/cosmetics brand. It is one half of a two-repo system; the REST backend lives in the sibling repo `../vantair-backend` (Spring Boot + PostgreSQL). There is no build step, framework, or bundler — pages are plain `.html` files that share four global scripts in `js/`.

`RUNNING.md` is the authoritative source for end-to-end flows (order/coupon/refund, admin product CRUD, analytics) with diagrams and a page→endpoint table — read it before tracing any feature across the stack.

## Running

```sh
npm start          # node server.js → http://localhost:5500 (zero deps; npm install not needed)
PORT=3000 API=http://host:8080 node server.js   # override frontend port / backend target
```

`server.js` is a hand-rolled zero-dependency Node server that (1) serves static files from disk and (2) reverse-proxies `/api/*`, `/v3/api-docs`, and `/swagger-ui*` to the backend (default `http://localhost:8080`). Serving site + API under one origin means the browser makes **same-origin** calls — there is no CORS config anywhere. The full stack needs Postgres on `localhost:5433/vantair` and the backend (`cd ../vantair-backend && mvn spring-boot:run`) — see `../vantair-backend/README.md`.

There are no frontend tests or linters in this repo. JS files can be syntax-checked with `node -c js/api.js`. Backend tests run from `../vantair-backend` via Maven.

## Architecture

Every page loads three globals in this **required order** (`api.js` → `data.js` → `components.js`); breaking it breaks the site:

- **`js/api.js`** — the only thing that talks to the backend. A thin `fetch` wrapper (`apiRequest`) plus the `api.*` method object (one method per endpoint). Errors throw an `Error` whose `.message` is the backend's human-readable message, so call sites use `catch (e) { showToast(e.message) }`. Also owns the **browser session**: the signed-in user is cached in `localStorage['vantair_session_user']` (`getSessionUser`/`setSessionUser`/`refreshSessionUser`) so synchronous code (navbar, `requireAuth`) keeps working without an async call.

- **`js/data.js`** — the `VANTAIR` global: brand info, delivery rules, currencies, languages, and a **hardcoded product/category/coupon catalog that mirrors the backend seed**. This is an instant-render fallback: on load, `VANTAIR.ready` re-fetches the live catalog from the API, overwrites `VANTAIR.products/categories/coupons`, and dispatches a `vantair:catalog` event. Listing pages either `await VANTAIR.ready` or listen for that event to re-render with live data. If the backend is down the site still renders from the bundled fallback (only dynamic flows error). Also holds all cart/wishlist helpers (cart & wishlist live in `localStorage`; wishlist also syncs to the API when signed in) and fires the fire-and-forget analytics page-view beacon (`trackPageView`, skips `admin.html`).

- **`js/components.js`** — injects shared navbar/footer/announcement/language-currency bar into placeholder elements (`#navbar`, `#footer`, etc.) on each page.

- **`js/auth.js`** (loaded on auth/account pages) — sign up/in/out, profile, addresses. All async, all hit the API and refresh the cached session, all return `{ success, user?, error? }`.

- **`js/otp.js`** (loaded on account/delivery pages) — delivery OTP. The OTP lives **server-side**: customers reveal it via `POST /orders/{id}/reveal-otp` (backend caps at 3 views), delivery agents validate via `POST /delivery/{orderId}/validate-otp`.

### Key conventions & gotchas

- **Never trust client-side prices/totals.** The order flow saves a `vantair_order_summary` in localStorage for display, but the backend recomputes subtotal, discount, and delivery from DB prices on `POST /orders`. Coupon and refund-coupon validation happens server-side.
- **Field name mismatch:** backend serves `description`, frontend templates read `desc`. `applyCatalog()` in `data.js` maps `description → desc` for categories and coupons — preserve this when touching catalog code.
- **Currency:** all prices are stored in INR; `formatPrice()` converts using `VANTAIR.currencies[code].rate` and the user's selected currency (auto-detected from timezone on first visit).
- **Admin auth is client-side only** (`admin`/`vantair2025`, gated via `localStorage['vantair_admin_auth']` — see `_adminshot.html`) and the admin API is unauthenticated. Fine for local dev; **must be secured before any deployment.**
- **`admin.html` is a single ~70KB self-contained page** (dashboard, products, orders, customers, refunds, ads, traffic analytics) — it does not use the shared component scripts the same way the storefront does.
- Adding a new backend endpoint means adding a method to the `api` object in `js/api.js`; don't call `fetch` directly from page code.

## Backend integration reference

`README.txt` lists the catalog seed data, coupon codes, and the planned (not-yet-wired) third-party integrations (Firebase/Razorpay/MSG91/Shiprocket — currently the Spring Boot + Postgres backend stands in for these). For the exhaustive page→endpoint mapping and per-flow diagrams, see `RUNNING.md`.

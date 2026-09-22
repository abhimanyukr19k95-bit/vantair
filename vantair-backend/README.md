# Vantair Backend API

REST backend for the Vantair e-commerce storefront (the static site in `../vantair`).
Java 21 · Spring Boot 3.2 · Spring Data JPA · PostgreSQL.

## Prerequisites

- JDK 21
- Maven 3.8+
- PostgreSQL running on `localhost:5433` with a database named `vantair`
  (user `postgres`, password `postgres`). Create it with:
  ```sh
  createdb -h localhost -p 5433 -U postgres vantair
  ```

DB connection is configured in `src/main/resources/application.yml`.

## Run

```sh
mvn spring-boot:run
```

On first start, Hibernate creates the schema (`ddl-auto=update`) and a seeder
(`config/DataSeeder`) loads 4 categories, 32 products (with variants) and 6 coupons.
The seeder is idempotent — it only writes when the tables are empty.

App listens on `http://localhost:8080`.

## Tests & coverage

```sh
mvn test       # run the unit/slice test suite (179 tests)
mvn verify     # run tests + enforce the JaCoCo coverage gate
```

- **No database needed** — tests run against an in-memory **H2** (PostgreSQL mode,
  configured in `src/test/resources/application.yml`).
- Layers covered: every service (Mockito unit tests), every controller
  (`@WebMvcTest`, which also exercises `GlobalExceptionHandler`), repository finders
  (`@DataJpaTest`), the config classes, `DataSeeder`, and a full-context smoke test
  (`ApplicationContextTest`, which also checks CORS).
- **Coverage**: [JaCoCo](https://www.jacoco.org/) runs on every `test`, writing an
  HTML report to `target/site/jacoco/index.html`. `mvn verify` **fails the build if
  line coverage drops below 95%** (currently ~99%). Lombok-generated accessors and
  the `main` class are excluded so the metric reflects hand-written logic.

## Interactive API docs (Swagger)

With the app running:

- **Swagger UI**: http://localhost:8080/swagger-ui.html — browse every endpoint
  grouped by area, see pre-filled example request bodies, and "Try it out" live.
- **Raw OpenAPI spec**: http://localhost:8080/v3/api-docs (JSON)

Example bodies and field examples come from `@Schema(example = ...)` annotations on
the request records in `dto/Dtos.java`; the API title/description live in
`config/OpenApiConfig`.

## API

Base path: `/api`

### Catalog
| Method | Path | Notes |
|--------|------|-------|
| GET | `/products` | optional `?category=perfumes\|toiletries\|skincare\|cosmetics` |
| GET | `/products/{id}` | e.g. `P001` |
| GET | `/categories` | |

### Coupons
| Method | Path | Body |
|--------|------|------|
| GET | `/coupons` | |
| POST | `/coupons/apply` | `{ "code":"VANTAIR10", "subtotal":1000 }` |

### Auth & Users
| Method | Path | Body |
|--------|------|------|
| POST | `/auth/signup` | `{name,email,password,phone?,newsletter?,address?}` → 201, returns user (no password) |
| POST | `/auth/signin` | `{email,password}` |
| GET | `/users/{id}` | |
| PUT | `/users/{id}` | `{name?,phone?,photo?,newsletter?}` |
| POST | `/users/{id}/change-password` | `{currentPassword,newPassword}` |
| POST | `/users/{id}/addresses` | address body (max 3) |
| DELETE | `/users/{id}/addresses/{addressId}` | |
| PUT | `/users/{id}/addresses/{addressId}/default` | |
| POST | `/users/{id}/wishlist/{productId}` | toggles; returns wishlist |

### Orders
| Method | Path | Notes |
|--------|------|-------|
| POST | `/orders` | `{userId,addressId,payment:"upi"\|"cod",couponCode?,deliveryType?,items:[{productId,variantIndex,qty}]}`. Totals are recomputed server-side; a delivery OTP is generated. `couponCode` accepts a standard coupon **or** one of the customer's unused refund coupons (redeemed as a flat discount, then marked used). Free shipping is applied when the **subtotal** (pre-discount) is ≥ ₹999. |
| GET | `/orders` | all orders; `?userId=` filters to one customer |
| GET | `/orders/{id}` | |
| PUT | `/orders/{id}/status` | `{status:"Shipped"}` (Confirmed/Processing/Shipped/OutForDelivery/Delivered/Cancelled) |
| POST | `/orders/{id}/reveal-otp?userId=` | returns plaintext OTP to the owner; capped at 3 views |

### Delivery (agent)
| Method | Path | Body |
|--------|------|------|
| POST | `/delivery/{orderId}/validate-otp` | `{otp}` — marks the order Delivered; rejects replays |

### Engagement
| Method | Path | Body |
|--------|------|------|
| POST | `/newsletter` | `{email}` |
| POST | `/contact` | `{name,email,phone?,type?,message}` |
| POST | `/partner` | `{name,company?,email,phone?,type?,volume?,message?}` |

### Analytics & Ads (public)
| Method | Path | Body |
|--------|------|------|
| POST | `/analytics/track` | `{path,title?,sessionId?,userId?,device?,referrer?}` — records a page view; fire-and-forget, always returns **202**. Posted by `js/data.js` on every storefront page (the admin panel is excluded). |
| GET | `/ads` | active promotional ads; optional `?slot=home\|shop`. Rendered into storefront ad slots by `js/data.js`. |

### Admin (⚠️ currently unauthenticated)
| Method | Path | |
|--------|------|--|
| GET | `/admin/stats` | revenue, orders, delivered, customers, products |
| GET | `/admin/orders` | all orders |
| GET | `/admin/customers` | all registered users |
| POST | `/admin/products` | create a product (full body incl. `variants:[{label,price}]`). **`id` is optional** — when blank it's auto-generated with a category prefix (P/T/S/C + next number, e.g. `P009`). |
| PUT | `/admin/products/{id}` | update a product |
| DELETE | `/admin/products/{id}` | soft-hide (active=false) |
| POST | `/admin/products/generate-content` | `{name,category?}` → `{tagline,description,benefits}` auto-written from the name (template generator, no API key) |
| POST | `/admin/refunds` | issue a refund coupon: `{email,amount,reason?}` → 201, returns `{code,value,customerEmail,...}`. 404 if no customer has that email. |
| GET | `/admin/refunds` | refund-coupon history (code, customer, amount, reason, used, issuedAt) |
| GET | `/admin/analytics` | traffic dashboard: `{totalViews, viewsToday, uniqueVisitorsToday, activeSessions, onlineCustomers, topPages[], devices[], referrers[], dailyTraffic[]}`. "Active/online" = seen within the last 5 minutes. |
| GET / POST | `/admin/ads` | list all ads / create an ad (`{title,text?,image?,link?,slot?,active?}`) |
| PUT | `/admin/ads/{id}` | update an ad |
| PUT | `/admin/ads/{id}/active?active=` | enable/disable an ad |
| DELETE | `/admin/ads/{id}` | delete an ad |
| GET | `/admin/newsletter` · `/admin/contacts` · `/admin/partners` | enquiry inboxes |

## Data model

Key JPA entities (`model/`) and their tables:

| Entity | Table | Notes |
|--------|-------|-------|
| `Product` + `ProductVariant` | `products`, `product_variants` | catalog; `active=false` hides from storefront |
| `Category`, `Coupon` | `categories`, `coupons` | seeded by `DataSeeder` |
| `User` + `Address` | `users`, `addresses` | password stored hashed; wishlist as element collection |
| `Order` + `OrderItem` | `orders`, `order_items` | embeds a shipping-address snapshot; holds the delivery OTP/hash |
| `RefundCoupon` | `refund_coupons` | store credit tied to a `User`; redeemable once at checkout, returned to the customer if the order is cancelled |
| `PageView` | `page_views` | one row per tracked storefront page view (analytics) |
| `Ad` | `ads` | admin-managed promotional banner shown in a storefront slot |
| `NewsletterSubscriber`, `ContactMessage`, `PartnerEnquiry` | resp. | engagement inboxes |

## Notes / deferred work

- **No authentication yet** (per project decision). Passwords are stored salted-SHA-256
  (`service/PasswordHasher`); the customer for OTP-reveal is identified by `?userId=`.
  Add Spring Security + JWT and role-gate `/admin/**` (and consider rate-limiting the
  public `/analytics/track` beacon) before any deployment.
- The static frontend (`../vantair`) is wired to these endpoints via `js/api.js` —
  catalog, auth, profile/addresses, orders, OTP, engagement, the admin panel
  (products / refunds / analytics) and the traffic beacon all go through the API.
  See `../vantair/RUNNING.md` for the end-to-end flow and data-flow diagrams.
- Money and delivery rules (charges, ETA, OTP hashing) are ported from the frontend's
  `js/data.js` / `js/otp.js` so the API is the single source of truth. Free-shipping
  is evaluated on the pre-discount subtotal so a coupon can take an order to ₹0.
- When a coupon (standard or refund) covers the **entire** payable amount, the order's
  `payment` is recorded as `"coupon"` rather than the unused UPI/COD selection.
- Cancelling an order (`PUT /orders/{id}/status` → `Cancelled`) returns any redeemed
  refund coupon to the customer (marks it unused again).
- Analytics aggregation (`service/AnalyticsService`) treats a session/customer as
  "online" if seen within the last 5 minutes, and reports the last 7 days of traffic.
- CORS origins for the storefront are configurable via `vantair.cors.allowed-origins`.

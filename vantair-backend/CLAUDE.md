# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

REST backend for the Vantair e-commerce storefront (a separate static site in `../vantair`).
Java 21 · Spring Boot 3.2 · Spring Data JPA · PostgreSQL. The API is the single source
of truth for money/delivery/OTP rules that were originally implemented in the frontend's
`js/data.js` and `js/otp.js` — see "Ported logic" below.

## Commands

```sh
mvn spring-boot:run    # run the app on http://localhost:8080 (needs Postgres, see below)
mvn test               # unit + slice tests against in-memory H2 (no DB needed)
mvn verify             # tests + JaCoCo coverage gate (fails build under 95% line coverage)
mvn test -Dtest=OrderServiceTest                      # single test class
mvn test -Dtest=OrderServiceTest#placesOrder          # single test method
```

- **Running the app needs Postgres** on `localhost:5433`, db `vantair`, user/pass `postgres`/`postgres`
  (`createdb -h localhost -p 5433 -U postgres vantair`). On first start Hibernate creates the
  schema (`ddl-auto=update`) and `config/DataSeeder` idempotently loads categories, products and coupons.
- **Tests need no DB** — `src/test/resources/application.yml` swaps in H2 (PostgreSQL mode, `create-drop`).
  Note the `NON_KEYWORDS=VALUE` quirk: `Coupon`/`RefundCoupon` use a `value` column that H2 reserves.
- Coverage HTML report: `target/site/jacoco/index.html`.

## Architecture

Single Maven module, package `com.vantair.api`, strict layering:

```
controller/  → @RestController, all under /api, thin: validate + delegate to a service
service/     → @Service, all business logic + @Transactional boundaries
repository/  → Spring Data JPA interfaces (derived query methods only)
model/       → JPA @Entity classes (the domain)
dto/Dtos.java → ALL request/response records live in this one file
config/      → cross-cutting: exceptions, CORS, OpenAPI, DataSeeder
```

Key conventions to follow when adding code:

- **Errors**: services throw `ApiException` via its factories (`ApiException.notFound(...)`,
  `.badRequest(...)`, `.conflict(...)`, `.unauthorized(...)`). `config/GlobalExceptionHandler`
  (`@RestControllerAdvice`) turns these — plus bean-validation failures — into a uniform JSON
  body `{timestamp,status,error,message}`. Don't build error responses in controllers.
- **DTOs**: every request/response payload is a `record` inside the single `dto/Dtos.java`.
  Add Jakarta validation annotations (`@NotBlank`, `@Email`, etc.) AND `@Schema(example=...)` —
  the latter drives Swagger UI's pre-filled example bodies.
- **Constructor injection** everywhere (no field `@Autowired`).
- **Transactions** are declared on service methods (`@Transactional`, `readOnly=true` for reads).
  `open-in-view: true` is intentional so lazy collections serialize during view rendering.
- **Lombok** generates accessors on entities; `lombok.config` adds `@lombok.Generated` so JaCoCo
  excludes that boilerplate. The metric reflects hand-written logic only — keep it that way.
- New endpoints should carry an `@Tag` (controller) and method-level Swagger annotations to stay
  documented at `/swagger-ui.html`.

## Ported logic (don't "fix" these by intuition)

These mirror the frontend so the API stays bit-compatible with existing clients — match them, don't
rewrite them:

- **OTP** (`service/OtpService`): a 6-digit OTP plus a salted djb2-style `simpleHash` (base36, upper)
  ported from `otp.js`. Reveal is capped at 3 views; delivery validation marks the OTP used and
  rejects replays.
- **Money/delivery** (`service/OrderService`, `DeliveryService`): totals are always recomputed
  server-side — never trust client amounts. **Free shipping is decided on the pre-discount subtotal**
  (≥ ₹999) so a coupon can take a cart to ₹0 without re-adding a delivery charge.
- **Coupons**: `couponCode` accepts a standard `Coupon` OR one of the customer's unused `RefundCoupon`s
  (flat store credit, redeemed once, restored if the order is later cancelled). When a coupon covers the
  entire payable amount, `Order.payment` is recorded as `"coupon"` instead of the selected upi/cod.

## Important caveats

- **No authentication yet** (deliberate project decision). Passwords are salted-SHA-256
  (`service/PasswordHasher`); OTP-reveal ownership is checked only via a `?userId=` query param;
  `/admin/**` is wide open. Don't assume a security layer exists.
- CORS is configured in `config/WebConfig` from `vantair.cors.allowed-origins` (comma-separated), for
  `/api/**` only.

The README.md has the full endpoint table and data-model reference — consult it for API specifics.

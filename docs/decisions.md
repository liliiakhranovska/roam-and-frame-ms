# Decisions

Where this file and `architecture/microservices-diagram-draft.txt` differ, this file wins
(the draft still lists a separate Auth service and an older checkout saga).

## Settled

| Area | Decision |
|---|---|
| Repository | Monorepo `roam-and-frame-ms`. The monolith `roam-and-frame` stays unchanged as the reference |
| Services | 9 services (customer, catalog, inventory, cart, order, payment, shipping, checkout, notification) + API gateway. No separate Auth service |
| Authentication | In the API gateway. RS256 token signed by the gateway (private key in `JWT_PRIVATE_KEY`); services verify with the public key from `JWT_PUBLIC_KEY`. Credentials live in the Customer service; the gateway calls it to check a login |
| API gateway | Spring Cloud Gateway Server MVC (train `2025.1.4`, built for Boot 4.0.9; works on Boot 4.1.1, no official train for 4.1 yet). One route per service in `application.properties` (`spring.cloud.gateway.server.webmvc.routes[n]`, target URL from an env variable). Routes so far: `/api/v1/products/**` → Catalog, `/api/v1/customer/**` → Customer, `/api/v1/cart/**` → Cart. Everything except login and `/actuator/health` needs a valid token; `/api/v1/admin/**` needs `ADMIN`; `/internal/**` is always denied. The `Authorization` header is passed on to the service |
| Login and token | The gateway serves `POST /api/v1/auth/token` (`email`, `password`; returns `accessToken`, `tokenType` = `Bearer`). It checks the login with Customer's `/internal/customers/authentication` (401 for wrong credentials, 503 if Customer is unavailable, `CUSTOMER_URL`, timeout `CUSTOMER_TIMEOUT`, default 2s) and signs the token. RS256, claims `sub` (customer id), `email`, `roles` (one of `CUSTOMER`, `ADMIN`), `iat`, `exp` (60 minutes, `JWT_EXPIRATION_MINUTES`); no `kid`, no JWKS endpoint. Keys are single-line base64 in env variables: `JWT_PRIVATE_KEY` (PKCS#8, gateway only) and `JWT_PUBLIC_KEY` (X.509, gateway and every service that verifies tokens) |
| Service-to-service calls | Saga steps use internal endpoints (`/internal/**`), never routed by the gateway; they carry `customerId` and the idempotency key. The user's token is verified only at the edge and at Checkout's public endpoint |
| Communication | Synchronous REST. No message broker for now (revisit for Notification and cart reminders) |
| Data | Database per service; one Postgres server locally, one database and user per service. No cross-service joins (API Composition for queries) |
| Checkout | Orchestrated saga over REST (see below); saga state in Checkout's own database; scheduled recovery job |
| Order status | `PLACED`, `SHIPPED` only (as in the monolith). The order is created after the payment is authorized, so there is no `PENDING` |
| Payment | Authorize / void authorization (as in the monolith). No charge, capture or refund |
| Customer data | Checkout fetches the customer profile once at the start and passes on what is needed: the address to Shipping, name/email/address to Notification. Shipping and Notification do not call Customer |
| Notification contract | `POST /internal/notifications/order-confirmation` returns 202. The body carries the customer (name, email, address), the order (id, time, total, items), the payment (status, reference) and the tracking number. Sending is asynchronous; a mail failure is only logged, never returned to Checkout |
| Inventory contract | Internal only (no public API; customers see stock through Catalog). `GET /internal/inventory/quantities?skus=` (0 for unknown SKU); `POST /internal/inventory/reservations` (`orderId` + items; all or nothing; 204, or 409 when stock is short; a repeat with the same `orderId` is a no-op); `DELETE /internal/inventory/reservations/{orderId}` (gives the reserved stock back; a repeat is a no-op). Reservations are stored per `orderId` and SKU; reserving again after a release is a 409 |
| Payment contract | Internal only. `POST /internal/payments/authorizations` (`orderId`, `customerId`, `amount`; returns `orderId`, `status`, `reference`; a repeat with the same `orderId` returns the same payment; 402 if declined; 409 if already voided); `DELETE /internal/payments/authorizations/{orderId}` (voids at the provider and marks the payment `VOIDED`; 204, a repeat or an unknown `orderId` is a no-op). Payment status: `AUTHORIZED`, `VOIDED` |
| Catalog contract | Public (through the gateway): `GET /api/v1/products?category=&brand=&inStock=` and `GET /api/v1/products/{sku}` (404 for an unknown SKU); every product carries its `quantity`. Internal: `GET /internal/catalog/products?skus=` (products with `quantity`; unknown SKUs are left out), for Cart. Catalog reads stock from Inventory (`INVENTORY_URL`, timeout `INVENTORY_TIMEOUT`, default 2s). If Inventory is unavailable Catalog answers 503 rather than returning products without stock (revisit with the circuit breaker) |
| Customer contract | Internal. `POST /internal/customers/authentication` (`email`, `password`; returns `customerId`, `email`, `role`, never the hash; 401 for an unknown email or a wrong password, with the same message), called by the gateway on login, which then signs the token. `GET /internal/customers/{id}` (profile with address; 404 if unknown), called by Checkout in saga step 1. Passwords are checked with BCrypt inside Customer. Public: `GET` and `PUT /api/v1/customer/profile` (`PUT` replaces all profile fields; first and last name required, `countryCode` two capital letters; 400 on invalid data). The customer id is always the token's `sub`, never taken from the URL or body. Customer verifies the token itself with `JWT_PUBLIC_KEY`, so a call that bypasses the gateway is rejected too (401); its `/internal/**` endpoints need no token (network-trusted), and every other path is denied |
| Cart contract | Public (token required, verified by Cart itself): `GET /api/v1/cart`, `DELETE /api/v1/cart` (204), `PUT /api/v1/cart/items/{sku}` with `{"quantity": n}` (0 removes the item; 409 if the quantity is above the stock, 404 for an unknown SKU, 400 for a negative quantity). Internal, for Checkout: `POST /internal/carts/{customerId}/validation` (no changes; returns the cart lines with name, unit price, quantity and total, or 409 if the cart is empty or a line is invalid: product gone or quantity above the stock) and `DELETE /internal/carts/{customerId}` (204, a repeat is a no-op; saga step 7). Cart reads product name, price and stock from Catalog's internal endpoint (`CATALOG_URL`, timeout `CATALOG_TIMEOUT`, default 2s); if Catalog is unavailable Cart answers 503, except for an empty cart. No row lock (replaced by the one-checkout-at-a-time rule) and no transaction around the Catalog call. No sample carts, `seed/cleanup.sql` only |
| Order and Shipping | Stay two services (a merge into one was considered: it would remove the cancel-order and cancel-shipment compensations and the cross-service "ship" action, but two services keep the saga with its full set of undo steps) |
| Shipping contract | Internal only (customers see their shipment inside their order). `POST /internal/shipments` (`orderId`, `customerId`, `address` with `addressLine1`, `addressLine2`, `city`, `region`, `postalCode`, `countryCode`; returns `orderId`, `status`, `trackingNumber`; books the carrier; a repeat with the same `orderId` returns the same shipment; 409 if it was cancelled; 400 if `addressLine1`, `city`, `postalCode` or `countryCode` is blank). `DELETE /internal/shipments/{orderId}` (cancels at the carrier and sets `CANCELLED`; 204, a repeat or an unknown `orderId` is a no-op; 409 if already shipped). `POST /internal/shipments/{orderId}/ship` (sets `SHIPPED`; a repeat is a no-op; 404 if unknown, 409 if cancelled), for Order's admin "ship". `GET /internal/shipments?orderIds=` (the shipments found), for Order's lists. Shipping does not call Customer: the address comes in the request, and Checkout checks it is complete at saga step 1. Shipment status: `INITIATED`, `SHIPPED`, `CANCELLED` |
| Seed customer ids | The customers in `services/customer-service/seed/seed.sql` have fixed ids `c0000000-0000-0000-0000-00000000000N` (N = 1..6, the last is the admin), so the seeds of other services can refer to them. All passwords are `password` |
| Idempotency | `orderId` (generated first, as in the monolith) is the `checkout_id` and the idempotency key of every saga call |
| Concurrency | One in-progress checkout per customer (unique rule on the saga table); replaces the monolith's cart row lock (`FOR UPDATE`) |
| Configuration | Environment variables with local defaults: `${VARIABLE:local-default}`. Secrets never have defaults |
| Deployment | Kubernetes, after the migration. Until then services run locally |
| Ports | See the table in `README.md` |

## Checkout saga

Same steps as the monolith's `checkout()`, but every step that changes data needs its own undo
(there is no shared database transaction to roll back).

| # | Service | Action | Compensation |
|---|---|---|---|
| 1 | Customer | get profile, check the delivery address is complete | none (read) |
| 2 | Cart | validate the cart and read its lines | none (read) |
| 3 | Inventory | reserve stock | release stock (new) |
| 4 | Payment | authorize payment | void authorization |
| 5 | Order | create the order (`PLACED`) | cancel order (new) |
| 6 | Shipping | create the shipment (address from Checkout) | cancel shipment (new, including the provider call) |
| 7 | Cart | clear the cart | none: last step, retried and never undone |
| 8 | Notification | send the confirmation email | none: failure is logged and ignored |

- Failure at steps 1-6: Checkout runs the compensations of the completed steps in reverse order.
- After step 6 the order is complete: steps 7 and 8 are retried, not undone.
- New operations compared to the monolith: release stock, cancel order, cancel shipment.

## Open

- How the key pair is distributed outside a developer machine (Kubernetes secrets) and rotated; locally it is generated once with `openssl` (see `README.md`)
- Data from other services in the admin order list (monolith: customer emails via `findEmails`, not ported)
- How the client learns the checkout result (response after completion, or polling)

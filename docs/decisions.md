# Decisions

Where this file and `architecture/microservices-diagram-draft.txt` differ, this file wins
(the draft still lists a separate Auth service and an older checkout saga).

## Settled

| Area | Decision |
|---|---|
| Repository | Monorepo `roam-and-frame-ms`. The monolith `roam-and-frame` stays unchanged as the reference |
| Services | 9 services (customer, catalog, inventory, cart, order, payment, shipping, checkout, notification) + API gateway. No separate Auth service |
| Authentication | In the API gateway. RS256 token signed by the gateway (private key in `JWT_PRIVATE_KEY`); services verify with the public key from `JWT_PUBLIC_KEY`. Credentials live in the Customer service; the gateway calls it to check a login |
| Service-to-service calls | Saga steps use internal endpoints (`/internal/**`), never routed by the gateway; they carry `customerId` and the idempotency key. The user's token is verified only at the edge and at Checkout's public endpoint |
| Communication | Synchronous REST. No message broker for now (revisit for Notification and cart reminders) |
| Data | Database per service; one Postgres server locally, one database and user per service. No cross-service joins (API Composition for queries) |
| Checkout | Orchestrated saga over REST (see below); saga state in Checkout's own database; scheduled recovery job |
| Order status | `PLACED`, `SHIPPED` only (as in the monolith). The order is created after the payment is authorized, so there is no `PENDING` |
| Payment | Authorize / void authorization (as in the monolith). No charge, capture or refund |
| Customer data | Checkout fetches the customer profile once at the start and passes on what is needed: the address to Shipping, name/email/address to Notification. Shipping and Notification do not call Customer |
| Notification contract | `POST /internal/notifications/order-confirmation` returns 202. The body carries the customer (name, email, address), the order (id, time, total, items), the payment (status, reference) and the tracking number. Sending is asynchronous; a mail failure is only logged, never returned to Checkout |
| Inventory contract | Internal only (no public API; customers see stock through Catalog). `GET /internal/inventory/quantities?skus=` (0 for unknown SKU); `POST /internal/inventory/reservations` (`orderId` + items; all or nothing; 204, or 409 when stock is short; a repeat with the same `orderId` is a no-op); `DELETE /internal/inventory/reservations/{orderId}` (gives the reserved stock back; a repeat is a no-op). Reservations are stored per `orderId` and SKU; reserving again after a release is a 409 |
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

- Where the customer's credentials are stored in detail, and the token contents and lifetime (monolith: 60 minutes)
- Whether all modules need to be separate services (e.g. Order + Shipping)
- How the client learns the checkout result (response after completion, or polling)

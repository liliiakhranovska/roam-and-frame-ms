# Decisions

Where this file and `architecture/microservices-diagram-draft.txt` differ, this file wins
(the draft still lists a separate Auth service).

## Settled

| Area | Decision |
|---|---|
| Repository | Monorepo `roam-and-frame-ms`. The monolith `roam-and-frame` stays unchanged as the reference |
| Services | 9 services (customer, catalog, inventory, cart, order, payment, shipping, checkout, notification) + API gateway. No separate Auth service |
| Authentication | In the API gateway. RS256 token signed by the gateway (private key in `JWT_PRIVATE_KEY`); services verify with the public key from `JWT_PUBLIC_KEY`. Credentials live in the Customer service; the gateway calls it to check a login |
| Service-to-service calls | Saga steps use internal endpoints (`/internal/**`), never routed by the gateway; they carry `customerId` and an idempotency key (`checkout_id`). The user's token is verified only at the edge and at Checkout's public endpoint |
| Communication | Synchronous REST. No message broker for now (revisit for Notification and cart reminders) |
| Data | Database per service; one Postgres server locally, one database and user per service. No cross-service joins (API Composition for queries) |
| Checkout | Orchestrated saga over REST; saga state in Checkout's own database; scheduled recovery job |
| Configuration | Environment variables with local defaults: `${VARIABLE:local-default}`. Secrets never have defaults |
| Deployment | Kubernetes, after the migration. Until then services run locally |
| Ports | See the table in `README.md` |

## Open

- Where Notification gets the customer email: token claim, request body from Checkout, or a call to Customer
- Where Shipping gets the shipping address (Customer call, or passed by Checkout)
- Undo step for Shipping in the checkout saga
- Whether all modules need to be separate services (e.g. Order + Shipping)
- Token contents and lifetime (monolith: 60 minutes)

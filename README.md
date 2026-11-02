# Roam & Frame (microservices)

Microservice version of [roam-and-frame](https://github.com/liliiakhranovska/roam-and-frame) (the monolith
stays unchanged as the reference). One repository, one Gradle build, one Spring Boot application and one
database per service.

Docs: `docs/decisions.md` (settled and open decisions), `docs/architecture/` (target architecture draft).

## Prerequisites (one-time)

- **JDK 21** (Gradle itself comes with the wrapper, `./gradlew`)
- **Docker runtime: Colima**
- **Compose plugin**
- **First Colima start** - CPU/memory are remembered, later a plain `colima start` is enough
  ```sh
  colima start --cpu 2 --memory 2
  ```
  An existing VM from the monolith project can be reused.

## Layout

```
build-logic/   shared Gradle plugin `roamandframe.spring-service` (used by every service)
services/      <name>-service, one folder per service
api-gateway/   routing, authentication, token signing
deploy/        local infrastructure (compose.yaml, Postgres init script)
docs/          decisions, architecture
```

New service: add the folder under `services/`, apply the plugin in its `build.gradle`, add
`include 'services:<name>-service'` to `settings.gradle`.

## IntelliJ

Open the `settings.gradle` file as a project (each service becomes a module). Run configuration per service:

- **Working directory: `$PROJECT_DIR$`**
- **VM options: `-Duser.timezone=UTC -Xmx256m`**
- **Env variables:** none needed yet (see Configuration)

## Local start

Docker Compose is **not** started by the application (one shared Postgres for all services).

1. `colima start` - once per session / after reboot
2. `docker compose -f deploy/compose.yaml up -d` - Postgres and Mailpit
3. Run the service(s) you work on in IntelliJ, or `./gradlew :services:<name>-service:bootRun`
4. Check: `curl localhost:<port>/actuator/health` → `{"status":"UP"}`

Run only the services you need. Do not run this project and the monolith at the same time (same ports:
5432, 1025, 8025).

## Services and ports

| Service | Port | Database |
|---|---|---|
| api-gateway | 8080 | none |
| notification-service | 8081 | none |
| inventory-service | 8082 | inventory |
| payment-service | 8083 | payment |
| catalog-service | 8084 | catalog |
| customer-service | 8085 | customer |
| cart-service | 8086 | cart |
| shipping-service | 8087 | shipping |
| order-service | 8088 | orders |
| checkout-service | 8089 | checkout |

`notification-service` and `inventory-service` are ported; the other services are added as they are ported from the monolith.

## Configuration

Anything that can differ between environments is an environment variable with a local default:
`${VARIABLE:local-default}`, e.g. `server.port=${SERVER_PORT:8081}`. Locally nothing needs to be set.
Secrets (`JWT_PRIVATE_KEY` in the gateway, `JWT_PUBLIC_KEY` in services that verify tokens) have no default
and are never committed.

## Local database

One Postgres server; one database and user per service, created by `deploy/postgres/init/01-databases.sql`
on the first start with an empty volume.

| Setting | Value |
|---|---|
| Host | localhost:5432 |
| Admin | postgres / postgres |
| Service | database `<name>`, user `<name>_svc`, password `<name>` (order: `orders` / `orders_svc` / `orders`) |

Commands (from the repo root):
- `docker compose -f deploy/compose.yaml ps` / `logs postgres` - status and logs
- `docker compose -f deploy/compose.yaml exec postgres psql -U postgres -d <database>` - connect
- `docker compose -f deploy/compose.yaml down` - stop (data kept); `down -v` also deletes the data and reruns
  the init script on the next start

## Database schema

Flyway per service: `services/<name>-service/src/main/resources/db/migration` (`V<n>__description.sql`),
applied when the service starts. Never edit a migration that has already run - add `V<n+1>`.
Hibernate only validates (`ddl-auto=validate`).

Sample data: `services/<name>-service/seed/seed.sql` (safe to rerun) and `cleanup.sql` (deletes all data of the
service). Run from the repo root, after the service has started once (Flyway creates the tables):
```sh
docker compose -f deploy/compose.yaml exec -T postgres psql -U <name>_svc -d <name> -v ON_ERROR_STOP=1 < services/<name>-service/seed/seed.sql
```

# Time Deposit Refactoring Kata

A Java 17 / Spring Boot implementation of the XA bank time-deposit exercise. It exposes two REST endpoints to retrieve deposits with their withdrawals and apply a monthly interest credit to every deposit.

The project focuses on testable business rules, transactional persistence, and preserving the kata's shared model contract. It is a learning and refactoring exercise with deliberately limited scope.

## Run locally

Requirements: **JDK 17**. The Maven wrapper downloads Maven and dependencies on first use. Docker is needed only for the PostgreSQL integration tests.

```bash
git clone https://github.com/franciscaquadros/time-deposit-take-home-kata.git
cd time-deposit-take-home-kata
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

On Windows, use `mvnw.cmd` in place of `./mvnw`.

The application listens on **http://localhost:8000**. The `demo` profile seeds three deposits and sample withdrawals only when the database is empty. It never deletes existing data. The default H2 database is in memory, so its data is lost when the application stops. Without `demo`, the database starts empty.

Try the two endpoints:

```bash
curl -i http://localhost:8000/api/time-deposits
curl -i -X PUT http://localhost:8000/api/time-deposits/balances
curl -s http://localhost:8000/api/time-deposits
```

After one update, the demo balances are **1000.83** (basic), **1002.50** (student), and **1004.17** (premium).

## Business rules

Rates are annual rates; each invocation calculates one monthly credit as `balance × annual rate ÷ 12`.

| Plan | Annual rate | Eligible days |
| --- | --- | --- |
| Basic | 1% | Greater than 30 |
| Student | 3% | 31 through 365, inclusive |
| Premium | 5% | Greater than 45 |

- Interest is rounded to two decimal places using `HALF_UP` before being added to the balance.
- Deposits outside the eligible window and unrecognized plan types receive no interest.
- Plan matching is case-insensitive, as in the submitted refactoring.
- The calculator changes balances in place; IDs, plan types, and days remain unchanged.
- Withdrawals are returned as historical records. The exercise does not define withdrawal processing or require subtracting them again from the stored balance.
- Each update call credits interest again and compounds the previous balance. Days do not advance automatically.

The [original assignment](docs/assignment.md) is retained for context. Its constraints require preserving the shared `TimeDeposit` class and the `updateBalance(List<TimeDeposit>)` signature, exposing exactly two endpoints, and assuming valid input.

## API

### `GET /api/time-deposits`

Returns `200 OK` with a JSON array. An empty database returns `[]`. Ordering is unspecified.

Example element:

```json
{
  "id": 1,
  "planType": "basic",
  "balance": 1000.0,
  "days": 45,
  "withdrawals": [
    { "id": 1, "amount": 100.0, "date": "2025-07-23" }
  ]
}
```

Deposits without withdrawals have an empty `withdrawals` array. JSON monetary fields are numbers; trailing zero formatting is not part of the contract.

### `PUT /api/time-deposits/balances`

Accepts no body and returns `200 OK` with an empty body. All balance updates commit together; a failure rolls back the entire operation. Database row locks serialize concurrent updates to existing deposits, preventing lost interest credits across application instances.

**Retry behavior:** this endpoint applies another credit on every successful invocation. Although its verb is `PUT` under the exercise's contract, the operation is not idempotent. A production design would identify the accrual period and enforce a unique credit per deposit and period before supporting automatic retries.

## Design

```text
REST controller → application use cases → domain model and calculator strategies
                          ↓
                  repository port
                          ↑
                    JPA adapter → database
```

- **Domain:** plain Java models, the repository port, and one interest strategy per plan. No Spring or JPA dependencies. A new plan can supply another `InterestCalculator` bean without changing the coordinator.
- **Application:** use cases define the read and write transaction boundaries. Balance updates lock rows in ID order and save all changes in one transaction.
- **Adapters and configuration:** Spring MVC handles HTTP, JPA maps persistence entities, and configuration wires the calculator strategies.
- **Persistence:** Flyway creates the schema and Hibernate validates it. Monetary columns use `NUMERIC(19,2)`. A foreign key ties withdrawals to their deposits. Fetching deposits with withdrawals uses two queries rather than one extra query per deposit.

### Monetary compatibility

The shared `TimeDeposit` class intentionally retains its `Double` balance, as required by the assignment. Calculator arithmetic and the submitted refactoring's rounding behavior are preserved and covered by characterization tests. The persistence adapter converts monetary values to `BigDecimal` for decimal storage.

This boundary conversion does not eliminate the shared model's floating-point limitations. A production redesign would use a decimal money type throughout, with explicit currency, precision, and rounding rules. Migrating that shared API would be a separate compatibility change.

## Tests

```bash
# Calculator rules, HTTP/database contract on H2, and demo-data behavior; no Docker needed
./mvnw test

# Everything above plus the same API/database contract on a real PostgreSQL container
./mvnw clean verify
```

`verify` requires a running Docker-compatible engine and downloads `postgres:17.6-alpine` on first use. It fails if PostgreSQL integration tests cannot run; they are not silently skipped.

The tests cover:

- Every plan's eligibility boundaries, mixed plans, zero balances, unknown plans, rounding, and repeated credits.
- HTTP responses, persisted balances, withdrawal associations, empty results, and foreign-key enforcement.
- Transaction rollback after a forced database failure and two concurrent update requests.
- Opt-in demo seeding that preserves existing data.

Surefire reports are under `target/surefire-reports`; PostgreSQL Failsafe reports are under `target/failsafe-reports`. GitHub Actions runs `./mvnw -B -ntp clean verify` for pushes and pull requests.

## Use PostgreSQL locally

Point the application at a **new, empty** PostgreSQL database. Flyway owns the schema; the migration is not an upgrade path for a database created by the original Hibernate `ddl-auto=update` setup.

```bash
export DB_URL=jdbc:postgresql://localhost:5432/time_deposit
export DB_USERNAME=your_database_user
export DB_PASSWORD=your_database_password
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

To add sample records to an empty PostgreSQL database, use `-Dspring-boot.run.profiles=postgres,demo`. Keep credentials in environment variables.

## Scope and tradeoffs

The application assumes valid input, as requested by the kata. Authentication, deposit creation, withdrawal execution, scheduled accrual, and pagination are outside its scope. Both endpoints operate on the full set of deposits, so this implementation targets a small exercise dataset. Production use would also require authorization, an accrual ledger with idempotency guarantees, operational monitoring, and a strategy for batching large datasets.

The original exercise and starter history are attributed in [docs/assignment.md](docs/assignment.md). The `org.ikigaidigital` namespace is retained from that starter.

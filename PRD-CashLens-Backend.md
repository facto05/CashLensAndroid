# PRD — CashLens Backend (REST API)

## 1. Overview

**Product:** CashLens Backend
**Stack:** Spring Boot 3 (Kotlin) + Spring Security + Spring Data JPA
**DB:** PostgreSQL 16
**Deploy:** Docker + GitHub Actions CI/CD + Cloud/VPS
**Auth:** JWT (jjwt)
**Status:** Draft v1.0

Tujuan: backend REST API sebagai sumber kebenaran (source of truth) untuk data keuangan CashLens, menerima sync dari client Android, serta menyediakan aggregasi statistik.

## 2. Goals & Non-Goals

### Goals
- Auth JWT (register/login/refresh).
- CRUD resource: transaction, category, budget, recurring.
- Endpoint sync (batch upsert + delete) untuk offline-first client.
- Aggregasi statistik (per kategori, per periode).
- Multi-device: data per-user terisolasi.
- Dockerized + CI/CD.

### Non-Goals
- Realtime websocket push (v1 pakai poll/sync client).
- Payment gateway / bank integration.
- Admin web dashboard (optional nanti).

## 3. Tech Decisions

- **Language:** Kotlin, Spring Boot 3.3+.
- **Web:** Spring Web (REST controller).
- **Security:** Spring Security + `jjwt` (JWT HS256).
- **DB:** Spring Data JPA + Hibernate, PostgreSQL driver.
- **Migrations:** Flyway.
- **Validation:** `jakarta.validation` (trust boundary, server-side wajib).
- **Rate limit:** Bucket4j pada auth endpoint.
- **DTO mapping:** MapStruct.
- **Test:** JUnit 5 + MockMvc + Testcontainers (PostgreSQL).

## 4. Module Structure

```
com.facto.cashlens
├── config/          # Security, Jwt, Swagger, Flyway
├── auth/            # AuthController, AuthService, JwtProvider
├── user/            # User, UserRepository
├── category/        # Category, CategoryRepository, CategoryController/Service
├── transaction/
├── budget/
├── recurring/
├── sync/            # SyncController, SyncService
├── stats/           # StatsController, aggregation queries
└── common/          # BaseEntity, exceptions, ApiResponse
```

## 5. Data Model (PostgreSQL via JPA)

```kotlin
abstract class BaseEntity {
    val id: UUID = UUID.randomUUID()
    val createdAt: Instant = Instant.now()
    var updatedAt: Instant = Instant.now()
    var deleted: Boolean = false
}

@Entity class User(
    @Column(unique = true) var email: String,
    var passwordHash: String
)

@Entity class Category(
    @ManyToOne user: User,
    var name: String, icon: String, color: String,
    @Enumerated type: TxType, // INCOME | EXPENSE
    var isDefault: Boolean
)

@Entity class Transaction(
    @ManyToOne user: User,
    @ManyToOne category: Category,
    @Enumerated type: TxType,
    var amount: BigDecimal,           // numeric(18,2)
    var note: String?,
    var txDate: LocalDate,
    @Column(unique = true) clientId: String, // unique per user
    var updatedAt: Instant
)

@Entity class Budget(
    @ManyToOne user: User,
    var month: YearMonth,
    @ManyToOne category: Category?,    // null = total
    var limit: BigDecimal
)

@Entity class Recurring(
    @ManyToOne user: User,
    @Enumerated type: TxType,
    var amount: BigDecimal,
    @ManyToOne category: Category,
    @Enumerated frequency: Freq,       // DAILY | WEEKLY | MONTHLY
    var nextRun: LocalDate,
    var active: Boolean
)

@Entity class RefreshToken(
    @ManyToOne user: User,
    var token: String,
    var expiresAt: Instant,
    var revoked: Boolean
)
```

Unique constraint: `(user_id, client_id)` agar sync idempoten.
Index: `(user_id, updated_at)`, `(user_id, client_id)`, `(user_id, tx_date)`.

## 6. API Endpoints

Base: `https://api.cashlens.app/api`
Semua endpoint (kecuali `/auth/*`) butuh header `Authorization: Bearer <accessToken>`.

### Auth
- `POST /auth/register` {email, password} → {user, accessToken, refreshToken}
- `POST /auth/login` {email, password} → tokens
- `POST /auth/refresh` {refreshToken} → new accessToken
- `POST /auth/logout`

### Categories
- `GET /categories`
- `POST /categories`
- `PUT /categories/{id}`
- `DELETE /categories/{id}`

### Transactions
- `GET /transactions?page=&size=&type=&categoryId=&from=&to=&q=`
- `POST /transactions`
- `PUT /transactions/{id}`
- `DELETE /transactions/{id}`

### Budget
- `GET /budgets?month=`
- `POST /budgets`
- `PUT /budgets/{id}`
- `DELETE /budgets/{id}`

### Recurring
- `GET /recurrings`
- `POST /recurrings`
- `PUT /recurrings/{id}`
- `DELETE /recurrings/{id}`

### Sync (batch)
- `POST /sync` body:
  ```json
  {
    "transactions": [ {"op":"UPSERT"|"DELETE", "clientId":"...", "type":"EXPENSE", "amount":50000, "categoryId":"...", "txDate":"2026-08-28", "updatedAt":"2026-08-28T10:00:00Z"} ],
    "categories": [...], "budgets": [...], "recurrings": [...]
  }
  ```
  Response: `{ "applied": [...], "serverTime": "2026-08-28T10:00:05Z" }`

### Statistics
- `GET /stats/by-category?from=&to=` → pie data per kategori.
- `GET /stats/trend?period=MONTH|YEAR` → series income vs expense.

## 7. Auth & Security

- SEC-1: Password pakai `BCryptPasswordEncoder`.
- SEC-2: Access token JWT HS256 expiry 15m; refresh token 7d, bisa di-revoke.
- SEC-3: Tiap query wajib filter `user = currentUser` (JPA `@Where` / repo method), cegah cross-user leak.
- SEC-4: Validasi server-side: amount > 0, enum valid, email format.
- SEC-5: Rate limit login pakai Bucket4j (5 fail / 15 menit per IP).
- SEC-6: HTTPS wajib (TLS terminasi di reverse proxy), HSTS on.

## 8. Sync Protocol

1. Client kirim `POST /sync` isi perubahan lokal (pending queue).
2. Server upsert by `clientId` unique per user; delete = soft delete (`deleted=true`).
3. Response berisi `serverTime` untuk client update watermark.
4. Client pull `GET /transactions?updatedAfter=` untuk perubahan device lain.
5. Konflik: **last-write-wins** by `updatedAt` (client kirim UTC).

## 9. Non-Functional

- NFR-1: API response < 300ms (p95) di VPS standar.
- NFR-2: Index `(user_id, updated_at)` dan `(user_id, client_id)`.
- NFR-3: Backup PostgreSQL harian (pg_dump ke object storage).
- NFR-4: 0 downtime deploy via Docker rolling.

## 10. Deployment

- Multi-stage `Dockerfile` (build dengan gradle, run JRE slim).
- `docker-compose.yml`: `app` + `postgres` + `caddy` (TLS auto).
- Flyway migrate otomatis saat startup.
- CI/CD GitHub Actions:
  1. Checkout → setup JDK 21 + Gradle cache.
  2. `./gradlew test` (Testcontainers PostgreSQL).
  3. Build image, push ke registry (GHCR).
  4. SSH deploy ke VPS, `docker compose pull && up -d`.
- Env via `.env` (`SPRING_DATASOURCE_URL`, `JWT_SECRET`, `SERVER_PORT`). Secret jangan commit.

## 11. Milestones

| Sprint | Scope |
|--------|-------|
| B1 | Auth + User + JPA schema + Flyway + Docker |
| B2 | CRUD categories/transactions/budget/recurring |
| B3 | Sync endpoint + conflict resolution |
| B4 | Statistics endpoints |
| B5 | CI/CD + deploy + security hardening |

## 12. Acceptance

- Register→login→create transaction→sync dari device kedua hasilkan data sama.
- Soft delete tidak menghilangkan riwayat server.
- Endpoint terproteksi tanpa token → 401.
- `./gradlew test` hijau; deploy otomatis ke staging lewat CI.

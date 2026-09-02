# PRD — CashLens (Android Client)

## 1. Overview

**Product:** CashLens — Personal Finance & Expense Tracker
**Platform:** Android (Kotlin + Jetpack Compose)
**Architecture:** Clean Architecture (MVI/MVVM) + Hilt
**Status:** Draft v1.0

Tujuan: aplikasi Android untuk pencatatan keuangan pribadi (income/expense), budget, recurring transaction, statistik, dengan dukungan offline-first dan sync ke backend.

## 2. Goals & Non-Goals

### Goals
- Pencatatan transaksi income/expense cepat dan mudah.
- Dashboard ringkasan keuangan real-time dari data lokal (Room).
- Budget bulanan dengan alert saat melewati limit.
- Recurring transaction otomatis (WorkManager).
- Statistik + chart pengeluaran per kategori/waktu.
- Offline-first: semua fitur jalan tanpa internet, sync saat online.
- Dark mode, reminder notifikasi.

### Non-Goals
- Multi-user / family wallet sharing (v1).
- Investment / portfolio tracking.
- Web/iOS client.

## 3. Tech Stack

- Kotlin, Jetpack Compose
- MVI/MVVM + Clean Architecture (data/domain/presentation)
- Coroutines + Flow
- Hilt (DI)
- Retrofit + OkHttp (network)
- Room (local DB)
- DataStore (preferences/settings)
- Paging 3 (history list)
- WorkManager (recurring + sync + reminder)
- JWT auth via DataStore
- Unit test (JUnit/Mockk), UI test (Compose UI / Paparazzi)

## 4. User Roles

- **Guest:** bisa pakai app offline tanpa akun (data lokal only).
- **Registered User:** data tersinkron ke backend, multi-device.

## 5. Features (FR)

### 5.1 Auth
- FR-A1: Register (email+password) → simpan JWT di DataStore.
- FR-A2: Login/logout.
- FR-A3: Guest mode tanpa login (data lokal).
- FR-A4: Token refresh otomatis saat expired.

### 5.2 Dashboard
- FR-D1: Tampilkan total balance, income bulan ini, expense bulan ini.
- FR-D2: Ringkasan budget bulan berjalan (terpakai vs limit).
- FR-D3: List transaksi terbaru (5-10 item).
- FR-D4: Quick action: tambah transaksi, set budget.

### 5.3 Transaction (Income & Expense)
- FR-T1: Tambah/edit/hapus transaksi (amount, type, category, date, note).
- FR-T2: Validasi input (amount > 0, category wajib).
- FR-T3: Kategori default + kustom.
- FR-T4: Setiap perubahan → update Room + queue sync.

### 5.4 Category
- FR-C1: CRUD kategori (nama, icon, warna, tipe income/expense).
- FR-C2: Seed kategori default saat first run.

### 5.5 Budget Bulanan
- FR-B1: Set budget per bulan (total atau per kategori).
- FR-B2: Progress bar pemakaian.
- FR-B3: Notifikasi saat mencapai 80% dan 100% limit.

### 5.6 Recurring Transaction
- FR-R1: Buat transaksi berulang (harian/mingguan/bulanan).
- FR-R2: WorkManager generate transaksi saat jatuh tempo.
- FR-R3: Enable/disable/cancel recurring.

### 5.7 History & Search/Filter
- FR-H1: List transaksi pakai Paging 3.
- FR-H2: Filter by type, category, date range.
- FR-H3: Search by note/amount.

### 5.8 Statistik & Chart
- FR-S1: Pie chart pengeluaran per kategori.
- FR-S2: Bar/line chart tren income vs expense per bulan.
- FR-S3: Filter periode (minggu/bulan/tahun).

### 5.9 Offline & Sync
- FR-O1: Semua read/write ke Room dulu (offline-first).
- FR-O2: Sync queue (pending ops) dikirim saat online.
- FR-O3: Conflict resolution: last-write-wins by updatedAt.
- FR-O4: Indikator status sync (synced/syncing/pending).

### 5.10 Settings
- FR-ST1: Dark mode (system/light/dark).
- FR-ST2: Currency & locale.
- FR-ST3: Manage reminder (on/off, time).

### 5.11 Notification / Reminder
- FR-N1: Reminder input transaksi harian (jam tertentu).
- FR-N2: Notifikasi budget terlampaui.
- FR-N3: Pakai WorkManager + NotificationManager.

## 6. Data Model (Lokal)

- `User(jwt, email)`
- `Transaction(id, type, amount, categoryId, date, note, syncState, updatedAt, deleted)`
- `Category(id, name, icon, color, type, isDefault)`
- `Budget(id, month, categoryId?, limit, spent)`
- `Recurring(id, type, amount, categoryId, freq, nextRun, active)`
- `SyncQueue(op, payload, status)`

## 7. Non-Functional

- NFR-1: Startup < 2s di mid-range device.
- NFR-2: UI responsif (state dari StateFlow/Compose state).
- NFR-3: Test coverage minimal 60% untuk domain + data layer.
- NFR-4: Offline tanpa crash, sync otomatis saat koneksi kembali.

## 8. Milestones

| Sprint | Scope |
|--------|-------|
| S1 | Auth + Transaction CRUD + Room + Dashboard |
| S2 | Category + Budget + Notification |
| S3 | Recurring + Sync + Offline |
| S4 | Stats/Chart + Search/Filter + Settings |
| S5 | Testing + polish + CI |

## 9. Acceptance

- Semua FR di atas bisa dijalankan offline.
- Sync mengembalikan data konsisten antar device.
- Unit test domain lulus; UI test flow tambah transaksi lulus.

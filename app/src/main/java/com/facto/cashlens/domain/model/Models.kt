package com.facto.cashlens.domain.model

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Long,
    val categoryId: String,
    val txDate: Long,
    val note: String?,
    val syncState: SyncState = SyncState.SYNCED
)

enum class TransactionType { INCOME, EXPENSE }

enum class SyncState { SYNCED, PENDING, FAILED }

data class Category(
    val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: TransactionType,
    val isDefault: Boolean = false
)

data class Budget(
    val id: String,
    val month: String,
    val categoryId: String?,
    val limit: Long,
    val spent: Long = 0
)

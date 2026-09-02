package com.facto.cashlens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val type: String,
    val amountCents: Long,
    val categoryId: String,
    val txDate: Long,
    val note: String?,
    val syncState: String = "SYNCED",
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String,
    val color: String,
    val type: String,
    val isDefault: Boolean = false
)

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey val id: String,
    val month: String,
    val categoryId: String?,
    val limitCents: Long
)

@Entity(tableName = "recurrings")
data class RecurringEntity(
    @PrimaryKey val id: String,
    val type: String,
    val amountCents: Long,
    val categoryId: String,
    val frequency: String,
    val nextRun: Long,
    val active: Boolean = true
)

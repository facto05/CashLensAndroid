package com.facto.cashlens.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.facto.cashlens.data.local.dao.BudgetDao
import com.facto.cashlens.data.local.dao.CategoryDao
import com.facto.cashlens.data.local.dao.SyncQueueDao
import com.facto.cashlens.data.local.dao.TransactionDao
import com.facto.cashlens.data.local.entity.BudgetEntity
import com.facto.cashlens.data.local.entity.CategoryEntity
import com.facto.cashlens.data.local.entity.SyncQueueEntity
import com.facto.cashlens.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, BudgetEntity::class, SyncQueueEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CashLensDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun syncQueueDao(): SyncQueueDao
}

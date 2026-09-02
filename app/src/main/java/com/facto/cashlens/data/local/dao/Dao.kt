package com.facto.cashlens.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.facto.cashlens.data.local.entity.CategoryEntity
import com.facto.cashlens.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<TransactionEntity>)

    @Update
    suspend fun update(tx: TransactionEntity)

    @Query("UPDATE transactions SET deleted = 1, syncState = 'PENDING' WHERE id = :id")
    suspend fun softDelete(id: String)

    @Query("SELECT * FROM transactions WHERE deleted = 0 ORDER BY txDate DESC")
    fun pagingSource(): PagingSource<Int, TransactionEntity>

    @Query("SELECT * FROM transactions WHERE deleted = 0 AND (:type IS NULL OR type = :type) ORDER BY txDate DESC")
    fun pagingSourceByType(type: String?): PagingSource<Int, TransactionEntity>

    @Query("SELECT COALESCE(SUM(amountCents), 0) FROM transactions WHERE deleted = 0 AND type = :type AND txDate BETWEEN :from AND :to")
    fun sumByType(type: String, from: Long, to: Long): Flow<Long>
}

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    fun observe(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type = :type")
    suspend fun getByType(type: String): List<CategoryEntity>
}

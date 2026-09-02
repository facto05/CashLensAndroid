package com.facto.cashlens.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.facto.cashlens.data.local.dao.TransactionDao
import com.facto.cashlens.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getPaged(type: String? = null): Flow<PagingData<TransactionEntity>> =
        Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {
                if (type.isNullOrBlank()) transactionDao.pagingSource()
                else transactionDao.pagingSourceByType(type)
            }
        ).flow

    suspend fun save(tx: TransactionEntity) = transactionDao.insert(tx)
    suspend fun delete(id: String) = transactionDao.softDelete(id)
}

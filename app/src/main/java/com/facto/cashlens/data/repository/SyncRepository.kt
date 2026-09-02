package com.facto.cashlens.data.repository

import com.facto.cashlens.core.network.Resource
import com.facto.cashlens.core.network.safeApiCall
import com.facto.cashlens.data.local.dao.SyncQueueDao
import com.facto.cashlens.data.local.dao.TransactionDao
import com.facto.cashlens.data.local.entity.SyncQueueEntity
import com.facto.cashlens.data.remote.CashLensApi
import com.facto.cashlens.data.remote.model.SyncRequest
import com.facto.cashlens.data.remote.model.SyncTransaction
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val api: CashLensApi,
    private val syncQueueDao: SyncQueueDao,
    private val transactionDao: TransactionDao,
    private val gson: Gson
) {
    suspend fun enqueuePendingChanges() = withContext(Dispatchers.IO) {
        // ponytail: scan local tables for syncState=PENDING, add to queue
    }

    suspend fun syncWithServer(): Resource<Unit> = withContext(Dispatchers.IO) {
        val pending = syncQueueDao.getPending()
        if (pending.isEmpty()) return@withContext Resource.Success(Unit)

        val transactions = pending.filter { it.entityType == "TRANSACTION" }.map {
            gson.fromJson(it.payload, SyncTransaction::class.java)
        }

        val request = SyncRequest(transactions = transactions)
        when (val result = safeApiCall { api.sync(request) }) {
            is Resource.Success -> {
                pending.forEach { syncQueueDao.updateStatus(it.id, "SYNCED") }
                syncQueueDao.clearSynced()
                Resource.Success(Unit)
            }
            is Resource.Error -> result
            Resource.Loading -> Resource.Loading
        }
    }
}

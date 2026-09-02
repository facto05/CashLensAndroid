package com.facto.cashlens.domain.usecase

import androidx.paging.PagingData
import androidx.paging.map
import com.facto.cashlens.data.local.entity.TransactionEntity
import com.facto.cashlens.data.repository.TransactionRepository
import com.facto.cashlens.domain.model.Transaction
import com.facto.cashlens.domain.model.TransactionType
import com.facto.cashlens.domain.model.SyncState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(private val repo: TransactionRepository) {
    operator fun invoke(type: String? = null): Flow<PagingData<Transaction>> =
        repo.getPaged(type).map { pagingData -> pagingData.map { it.toDomain() } }
}

class SaveTransactionUseCase @Inject constructor(private val repo: TransactionRepository) {
    suspend operator fun invoke(tx: Transaction) = repo.save(tx.toEntity())
}

class DeleteTransactionUseCase @Inject constructor(private val repo: TransactionRepository) {
    suspend operator fun invoke(id: String) = repo.delete(id)
}

fun TransactionEntity.toDomain() = Transaction(
    id = id,
    type = TransactionType.valueOf(type),
    amount = amountCents,
    categoryId = categoryId,
    txDate = txDate,
    note = note,
    syncState = SyncState.valueOf(syncState)
)

fun Transaction.toEntity() = TransactionEntity(
    id = id,
    type = type.name,
    amountCents = amount,
    categoryId = categoryId,
    txDate = txDate,
    note = note,
    syncState = syncState.name,
    updatedAt = System.currentTimeMillis()
)

package com.facto.cashlens.domain.usecase

import com.facto.cashlens.data.local.dao.BudgetDao
import com.facto.cashlens.data.local.entity.BudgetEntity
import com.facto.cashlens.domain.model.Budget
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(private val dao: BudgetDao) {
    operator fun invoke(month: String): Flow<List<Budget>> =
        dao.getByMonth(month).map { list -> list.map { it.toDomain() } }
}

class SaveBudgetUseCase @Inject constructor(private val dao: BudgetDao) {
    suspend operator fun invoke(budget: Budget) = dao.insert(budget.toEntity())
}

class DeleteBudgetUseCase @Inject constructor(private val dao: BudgetDao) {
    suspend operator fun invoke(id: String) = dao.delete(id)
}

fun BudgetEntity.toDomain() = Budget(
    id = id, month = month, categoryId = categoryId, limit = limitCents, spent = 0
)

fun Budget.toEntity() = BudgetEntity(
    id = id, month = month, categoryId = categoryId, limitCents = limit
)

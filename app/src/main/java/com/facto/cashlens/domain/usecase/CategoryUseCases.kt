package com.facto.cashlens.domain.usecase

import com.facto.cashlens.data.local.dao.CategoryDao
import com.facto.cashlens.data.local.entity.CategoryEntity
import com.facto.cashlens.domain.model.Category
import com.facto.cashlens.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveCategoriesUseCase @Inject constructor(private val dao: CategoryDao) {
    operator fun invoke(): Flow<List<Category>> =
        dao.observe().map { list -> list.map { it.toDomain() } }
}

class GetCategoriesByTypeUseCase @Inject constructor(private val dao: CategoryDao) {
    suspend operator fun invoke(type: TransactionType): List<Category> =
        dao.getByType(type.name).map { it.toDomain() }
}

class SaveCategoryUseCase @Inject constructor(private val dao: CategoryDao) {
    suspend operator fun invoke(category: Category) = dao.insert(category.toEntity())
}

fun CategoryEntity.toDomain() = Category(
    id = id, name = name, icon = icon, color = color,
    type = TransactionType.valueOf(type), isDefault = isDefault
)

fun Category.toEntity() = CategoryEntity(
    id = id, name = name, icon = icon, color = color,
    type = type.name, isDefault = isDefault
)

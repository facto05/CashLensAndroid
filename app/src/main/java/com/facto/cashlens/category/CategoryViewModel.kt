package com.facto.cashlens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facto.cashlens.domain.model.Category
import com.facto.cashlens.domain.model.TransactionType
import com.facto.cashlens.domain.usecase.GetCategoriesByTypeUseCase
import com.facto.cashlens.domain.usecase.ObserveCategoriesUseCase
import com.facto.cashlens.domain.usecase.SaveCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CategoryState(
    val name: String = "",
    val icon: String = "💰",
    val color: String = "#4CAF50",
    val type: TransactionType = TransactionType.EXPENSE,
    val isSaving: Boolean = false,
    val saved: Boolean = false
)

@HiltViewModel
class CategoryFormViewModel @Inject constructor(
    private val saveCategoryUseCase: SaveCategoryUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state

    fun onNameChange(value: String) = _state.update { it.copy(name = value) }
    fun onIconChange(value: String) = _state.update { it.copy(icon = value) }
    fun onColorChange(value: String) = _state.update { it.copy(color = value) }
    fun onTypeChange(type: TransactionType) = _state.update { it.copy(type = type) }

    fun save() {
        val s = _state.value
        if (s.name.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            saveCategoryUseCase(
                Category(UUID.randomUUID().toString(), s.name, s.icon, s.color, s.type)
            )
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val observeCategoriesUseCase: ObserveCategoriesUseCase
) : ViewModel() {
    val categories = observeCategoriesUseCase()
}

package com.facto.cashlens.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facto.cashlens.domain.model.Budget
import com.facto.cashlens.domain.usecase.DeleteBudgetUseCase
import com.facto.cashlens.domain.usecase.GetBudgetsUseCase
import com.facto.cashlens.domain.usecase.SaveBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class BudgetFormState(
    val categoryId: String = "",
    val limit: String = "",
    val month: String = getCurrentMonth(),
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BudgetListViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val deleteBudgetUseCase: DeleteBudgetUseCase
) : ViewModel() {

    private val currentMonth = getCurrentMonth()
    val budgets: Flow<List<Budget>> = getBudgetsUseCase(currentMonth)

    fun delete(id: String) = viewModelScope.launch {
        deleteBudgetUseCase(id)
    }
}

@HiltViewModel
class BudgetFormViewModel @Inject constructor(
    private val saveBudgetUseCase: SaveBudgetUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BudgetFormState())
    val state: StateFlow<BudgetFormState> = _state

    fun onCategoryChange(id: String) = _state.update { it.copy(categoryId = id) }
    fun onLimitChange(value: String) = _state.update { it.copy(limit = value) }

    fun save() {
        val s = _state.value
        val limitCents = s.limit.toLongOrNull()
        if (limitCents == null || limitCents <= 0 || s.categoryId.isBlank()) {
            _state.update { it.copy(error = "Category and limit required") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            saveBudgetUseCase(
                Budget(UUID.randomUUID().toString(), s.month, s.categoryId, limitCents, 0)
            )
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}

private fun getCurrentMonth(): String =
    SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Calendar.getInstance().time)

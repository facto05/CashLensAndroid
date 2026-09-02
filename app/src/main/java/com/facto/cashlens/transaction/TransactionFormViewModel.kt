package com.facto.cashlens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facto.cashlens.domain.model.Transaction
import com.facto.cashlens.domain.model.TransactionType
import com.facto.cashlens.domain.usecase.SaveTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class TransactionFormState(
    val id: String = UUID.randomUUID().toString(),
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val categoryId: String = "",
    val txDate: Long = System.currentTimeMillis(),
    val note: String = "",
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TransactionFormViewModel @Inject constructor(
    private val saveTransactionUseCase: SaveTransactionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TransactionFormState())
    val state: StateFlow<TransactionFormState> = _state

    fun onTypeChange(type: TransactionType) = _state.update { it.copy(type = type) }
    fun onAmountChange(value: String) = _state.update { it.copy(amount = value) }
    fun onCategoryChange(id: String) = _state.update { it.copy(categoryId = id) }
    fun onNoteChange(value: String) = _state.update { it.copy(note = value) }
    fun onDateChange(date: Long) = _state.update { it.copy(txDate = date) }

    fun save() {
        val s = _state.value
        val amountCents = s.amount.toLongOrNull()
        if (amountCents == null || amountCents <= 0 || s.categoryId.isBlank()) {
            _state.update { it.copy(error = "Amount and category required") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            saveTransactionUseCase(
                Transaction(s.id, s.type, amountCents, s.categoryId, s.txDate, s.note.takeIf { it.isNotBlank() })
            )
            _state.update { it.copy(isSaving = false, saved = true) }
        }
    }
}

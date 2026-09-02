package com.facto.cashlens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facto.cashlens.data.local.dao.TransactionDao
import com.facto.cashlens.domain.model.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import java.util.Calendar

data class StatsUiState(
    val totalBalance: Long = 0,
    val incomeThisMonth: Long = 0,
    val expenseThisMonth: Long = 0,
    val isLoading: Boolean = false
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionDao: TransactionDao
) : ViewModel() {
    private val monthBounds = getCurrentMonthBounds()

    val stats: StateFlow<StatsUiState> =
        combine(
            transactionDao.sumByType(TransactionType.INCOME.name, monthBounds.first, monthBounds.second),
            transactionDao.sumByType(TransactionType.EXPENSE.name, monthBounds.first, monthBounds.second)
        ) { income, expense ->
            StatsUiState(
                totalBalance = income - expense,
                incomeThisMonth = income,
                expenseThisMonth = expense,
                isLoading = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = StatsUiState(isLoading = true)
        )

    private fun getCurrentMonthBounds(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis
        return start to end
    }
}

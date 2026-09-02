package com.facto.cashlens.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.facto.cashlens.domain.model.Transaction
import com.facto.cashlens.domain.usecase.DeleteTransactionUseCase
import com.facto.cashlens.domain.usecase.GetTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {

    val transactions: Flow<PagingData<Transaction>> =
        getTransactionsUseCase().cachedIn(viewModelScope)

    fun delete(id: String) = viewModelScope.launch {
        deleteTransactionUseCase(id)
    }
}

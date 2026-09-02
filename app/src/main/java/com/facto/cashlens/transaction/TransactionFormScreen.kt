package com.facto.cashlens.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facto.cashlens.category.CategoryListViewModel
import com.facto.cashlens.component.CategoryPicker
import com.facto.cashlens.domain.model.TransactionType

@Composable
fun TransactionFormScreen(
    onSaved: () -> Unit,
    transactionViewModel: TransactionFormViewModel = hiltViewModel(),
    categoryViewModel: CategoryListViewModel = hiltViewModel()
) {
    val state by transactionViewModel.state.collectAsStateWithLifecycle()
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Add Transaction", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            androidx.compose.foundation.layout.Row {
                TextButton(onClick = { transactionViewModel.onTypeChange(TransactionType.EXPENSE) }) {
                    Text("Expense", color = if (state.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
                TextButton(onClick = { transactionViewModel.onTypeChange(TransactionType.INCOME) }) {
                    Text("Income", color = if (state.type == TransactionType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.amount,
                onValueChange = transactionViewModel::onAmountChange,
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            CategoryPicker(
                categories = categories,
                selectedId = state.categoryId,
                onSelected = transactionViewModel::onCategoryChange
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.note,
                onValueChange = transactionViewModel::onNoteChange,
                label = { Text("Note (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (state.isSaving) {
                CircularProgressIndicator()
            } else {
                Button(onClick = transactionViewModel::save, modifier = Modifier.fillMaxWidth()) {
                    Text("Save")
                }
            }

            state.error?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(msg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

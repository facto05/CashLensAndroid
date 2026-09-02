package com.facto.cashlens.transaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.facto.cashlens.domain.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionListScreen(
    onAddClick: () -> Unit,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val transactions = viewModel.transactions.collectAsLazyPagingItems()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        TransactionList(
            transactions = transactions,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun TransactionList(
    transactions: LazyPagingItems<Transaction>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            transactions.loadState.refresh is LoadState.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            transactions.itemCount == 0 -> {
                Text("No transactions", Modifier.align(Alignment.Center))
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        count = transactions.itemCount,
                        key = transactions.itemKey { it.id }
                    ) { index ->
                        transactions[index]?.let { tx ->
                            TransactionItem(tx)
                        }
                    }
                    if (transactions.loadState.append is LoadState.Loading) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: Transaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(tx.type.name, style = MaterialTheme.typography.labelSmall)
                Text(
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(tx.txDate)),
                    style = MaterialTheme.typography.bodySmall
                )
                tx.note?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
            Text(
                "${tx.amount}",
                style = MaterialTheme.typography.titleMedium,
                color = if (tx.type.name == "INCOME") MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }
    }
}

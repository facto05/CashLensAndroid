package com.facto.cashlens.category

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.facto.cashlens.domain.model.Category
import com.facto.cashlens.domain.model.TransactionType

@Composable
fun CategoryListScreen(
    onAddClick: () -> Unit,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle(initialValue = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Text("+")
            }
        }
    ) { padding ->
        if (categories.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No categories")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(categories) { category ->
                    CategoryItem(category)
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(android.graphics.Color.parseColor(category.color)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(category.icon, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.size(12.dp))
                Column {
                    Text(category.name, style = MaterialTheme.typography.titleMedium)
                    Text(category.type.name, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun CategoryFormScreen(
    onSaved: () -> Unit,
    viewModel: CategoryFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

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
            Text("Add Category", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            Row {
                TextButton(onClick = { viewModel.onTypeChange(TransactionType.EXPENSE) }) {
                    Text("Expense", color = if (state.type == TransactionType.EXPENSE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
                TextButton(onClick = { viewModel.onTypeChange(TransactionType.INCOME) }) {
                    Text("Income", color = if (state.type == TransactionType.INCOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.icon,
                onValueChange = viewModel::onIconChange,
                label = { Text("Icon (emoji)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.color,
                onValueChange = viewModel::onColorChange,
                label = { Text("Color (hex)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            if (state.isSaving) {
                CircularProgressIndicator()
            } else {
                Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun Row(content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.Row {
        content()
    }
}

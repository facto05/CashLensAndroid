package com.facto.cashlens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.facto.cashlens.auth.AuthScreen
import com.facto.cashlens.category.CategoryFormScreen
import com.facto.cashlens.category.CategoryListScreen
import com.facto.cashlens.stats.StatsScreen
import com.facto.cashlens.transaction.TransactionFormScreen
import com.facto.cashlens.transaction.TransactionListScreen
import com.facto.cashlens.ui.theme.CashLensTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CashLensTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppRoot()
                }
            }
        }
    }
}

sealed class Screen {
    data object Auth : Screen()
    data object Home : Screen()
    data object Stats : Screen()
    data object TransactionList : Screen()
    data object TransactionForm : Screen()
    data object CategoryList : Screen()
    data object CategoryForm : Screen()
}

@Composable
fun AppRoot() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Auth) }

    when (currentScreen) {
        Screen.Auth -> AuthScreen(onAuthenticated = { currentScreen = Screen.Home })
        Screen.Home -> HomeScreen(
            onStatsClick = { currentScreen = Screen.Stats },
            onTransactionsClick = { currentScreen = Screen.TransactionList },
            onCategoriesClick = { currentScreen = Screen.CategoryList }
        )
        Screen.Stats -> StatsScreen()
        Screen.TransactionList -> TransactionListScreen(
            onAddClick = { currentScreen = Screen.TransactionForm }
        )
        Screen.TransactionForm -> TransactionFormScreen(
            onSaved = { currentScreen = Screen.TransactionList }
        )
        Screen.CategoryList -> CategoryListScreen(
            onAddClick = { currentScreen = Screen.CategoryForm }
        )
        Screen.CategoryForm -> CategoryFormScreen(
            onSaved = { currentScreen = Screen.CategoryList }
        )
    }
}

@Composable
fun HomeScreen(
    onStatsClick: () -> Unit,
    onTransactionsClick: () -> Unit,
    onCategoriesClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("CashLens Dashboard", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onStatsClick) {
                Text("View Statistics")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onTransactionsClick) {
                Text("View Transactions")
            }
            Spacer(Modifier.height(12.dp))
            Button(onClick = onCategoriesClick) {
                Text("Manage Categories")
            }
        }
    }
}
package com.facto.cashlens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facto.cashlens.core.network.Resource
import com.facto.cashlens.domain.usecase.LoginUseCase
import com.facto.cashlens.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }
    fun onErrorConsumed() = _uiState.update { it.copy(error = null) }

    fun login() = authenticate { loginUseCase(it.email, it.password) }
    fun register() = authenticate { registerUseCase(it.email, it.password) }

    private fun authenticate(call: suspend (AuthUiState) -> Resource<Unit>) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Email and password required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = call(state)) {
                is Resource.Success -> _uiState.update { it.copy(isLoading = false, isAuthenticated = true) }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                Resource.Loading -> Unit
            }
        }
    }
}

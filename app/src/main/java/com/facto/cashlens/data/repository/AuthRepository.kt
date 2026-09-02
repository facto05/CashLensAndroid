package com.facto.cashlens.data.repository

import com.facto.cashlens.core.network.Resource
import com.facto.cashlens.core.network.safeApiCall
import com.facto.cashlens.data.local.AuthPreferences
import com.facto.cashlens.data.remote.CashLensApi
import com.facto.cashlens.data.remote.model.LoginRequest
import com.facto.cashlens.data.remote.model.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: CashLensApi,
    private val authPreferences: AuthPreferences
) {
    suspend fun login(email: String, password: String): Resource<Unit> = withContext(Dispatchers.IO) {
        safeApiCall {
            val resp = api.login(LoginRequest(email, password))
            authPreferences.saveTokens(resp.accessToken, resp.refreshToken)
            authPreferences.saveEmail(email)
        }
    }

    suspend fun register(email: String, password: String): Resource<Unit> = withContext(Dispatchers.IO) {
        safeApiCall {
            val resp = api.register(RegisterRequest(email, password))
            authPreferences.saveTokens(resp.accessToken, resp.refreshToken)
            authPreferences.saveEmail(email)
        }
    }

    suspend fun logout(): Resource<Unit> = withContext(Dispatchers.IO) {
        safeApiCall { api.logout() }.also { authPreferences.clear() }
    }

    suspend fun isLoggedIn(): Boolean = authPreferences.getAccessToken().isNullOrEmpty().not()
}

package com.facto.cashlens.data.remote

import com.facto.cashlens.data.local.AuthPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {
    override fun intercept(chain: okhttp3.Interceptor.Chain): Response {
        val token = runBlocking { authPreferences.getAccessToken() }
        val request = chain.request().newBuilder().apply {
            header("Accept", "application/json")
            if (!token.isNullOrEmpty()) header("Authorization", "Bearer $token")
        }.build()
        return chain.proceed(request)
    }
}

package com.facto.cashlens.data.remote

import com.facto.cashlens.data.remote.model.AuthResponse
import com.facto.cashlens.data.remote.model.LoginRequest
import com.facto.cashlens.data.remote.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface CashLensApi {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body body: Map<String, String>): AuthResponse

    @POST("auth/logout")
    suspend fun logout()
}

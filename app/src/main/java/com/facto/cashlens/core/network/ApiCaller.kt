package com.facto.cashlens.core.network

import retrofit2.HttpException
import java.io.IOException

suspend fun <T> safeApiCall(call: suspend () -> T): Resource<T> = try {
    Resource.Success(call())
} catch (e: HttpException) {
    Resource.Error(e.message ?: "HTTP error", e.code())
} catch (e: IOException) {
    Resource.Error("No internet connection")
} catch (e: Exception) {
    Resource.Error(e.message ?: "Unexpected error")
}

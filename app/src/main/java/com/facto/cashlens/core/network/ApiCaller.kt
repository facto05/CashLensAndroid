package com.facto.cashlens.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NetworkHelper @Inject constructor(@ApplicationContext private val context: Context) {
    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

class ApiCaller @Inject constructor(private val networkHelper: NetworkHelper) {
    suspend fun <T> safeApiCall(call: suspend () -> T): Resource<T> = withContext(Dispatchers.IO) {
        if (!networkHelper.isOnline()) return@withContext Resource.Error("No internet connection")
        try {
            Resource.Success(call())
        } catch (e: HttpException) {
            Resource.Error(e.message() ?: "HTTP error", e.code())
        } catch (e: IOException) {
            Resource.Error("No internet connection")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unexpected error")
        }
    }
}

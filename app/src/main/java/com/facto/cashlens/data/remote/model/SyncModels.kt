package com.facto.cashlens.data.remote.model

import com.google.gson.annotations.SerializedName

data class SyncRequest(
    @SerializedName("transactions") val transactions: List<SyncTransaction> = emptyList(),
    @SerializedName("categories") val categories: List<SyncCategory> = emptyList(),
    @SerializedName("budgets") val budgets: List<SyncBudget> = emptyList()
)

data class SyncTransaction(
    @SerializedName("op") val op: String,
    @SerializedName("clientId") val clientId: String,
    @SerializedName("type") val type: String? = null,
    @SerializedName("amount") val amount: Long? = null,
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("txDate") val txDate: Long? = null,
    @SerializedName("note") val note: String? = null,
    @SerializedName("updatedAt") val updatedAt: Long? = null
)

data class SyncCategory(
    @SerializedName("op") val op: String,
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("type") val type: String? = null
)

data class SyncBudget(
    @SerializedName("op") val op: String,
    @SerializedName("id") val id: String,
    @SerializedName("month") val month: String? = null,
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("limit") val limit: Long? = null
)

data class SyncResponse(
    @SerializedName("applied") val applied: List<String> = emptyList(),
    @SerializedName("serverTime") val serverTime: String
)

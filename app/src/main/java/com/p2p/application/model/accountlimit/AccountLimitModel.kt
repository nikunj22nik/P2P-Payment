package com.p2p.application.model.accountlimit

data class AccountLimitModel(
    val code: Int,
    val `data`: Data?,
    val message: String,
    val success: Boolean
)
package com.p2p.application.model.accountlimit

data class Data(
    val monthly_limit: String?,
    val wallet_limit: String?,
    val currency: String?,
    var user_kyc_status: Int
)
package com.p2p.application.model.recentmerchant



data class RecentMerchantModel(
    val code: Int,
    val `data`: MutableList<Merchant>?,
    val message: String,
    val success: Boolean
)
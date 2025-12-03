package com.p2p.application.model.homemodel

data class Wallet(
    val balance: String,
    val created_at: String,
    val currency: String,
    val deleted_at: Any,
    val id: Int,
    val last_transaction_at: String,
    val status: String,
    val updated_at: String,
    val user_id: Int,
    val version: Int
)
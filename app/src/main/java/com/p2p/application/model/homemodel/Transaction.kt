package com.p2p.application.model.homemodel

data class Transaction(
    val amount: String?,
    val date: String?,
    val time: String?,
    val currency: String?,
    val id: Int,
    val status: String?,
    val transaction_type: String?,
    val user: User?
)
package com.p2p.application.model.homemodel

data class Data(
    val secret_code_status: Boolean,
    val transactions: MutableList<Transaction>?,
    val wallet: Wallet?
)
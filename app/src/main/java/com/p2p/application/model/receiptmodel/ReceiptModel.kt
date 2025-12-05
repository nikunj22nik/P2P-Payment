package com.p2p.application.model.receiptmodel

data class ReceiptModel(
    val code: Int,
    val `data`: Data?,
    val message: String,
    val success: Boolean
)
package com.p2p.application.model.receiptmodel

data class Data(
    val amount: String?,
    val currency: String?,
    val date: String?,
    val id: Int,
    val `receiver`: Receiver?,
    val reference_no: String?,
    val status: String?,
    val time: String?
)
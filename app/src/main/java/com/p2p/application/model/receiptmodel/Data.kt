package com.p2p.application.model.receiptmodel

import com.google.gson.annotations.SerializedName

data class Data(
    val amount: String?,
    val currency: String?,
    val date: String?,
    val id: Int,
    @SerializedName("user")
    val `receiver`: Receiver?,
    val reference_no: String?,
    val status: String?,
    val time: String?,
    val transaction_fee:String?,
    val transaction_type:String?
)
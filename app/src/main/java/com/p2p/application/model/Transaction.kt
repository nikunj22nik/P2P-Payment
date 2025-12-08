package com.p2p.application.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val sender_id: Int? = null,
    val receiver_id: String? = null,
    val transaction_id: Long? = null,
    val reference_no: String? = null,
    val amount: String? = null,
    val currency: String? = null,
    val status: String? = null,
    val updatedAt: String? = null,
    val createdAt: String? = null,
    val id: Int? = null,

) : Parcelable
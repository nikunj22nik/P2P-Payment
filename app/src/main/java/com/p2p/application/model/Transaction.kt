package com.p2p.application.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val senderId: Int? = null,
    val receiverId: String? = null,
    val transactionId: Long? = null,
    val referenceNo: String? = null,
    val amount: String? = null,
    val currency: String? = null,
    val status: String? = null,
    val updatedAt: String? = null,
    val createdAt: String? = null,
    val id: Int? = null
) : Parcelable
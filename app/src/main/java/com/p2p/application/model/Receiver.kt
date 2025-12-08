package com.p2p.application.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Receiver(
    val name: String?,
    val user_id: Int,
    val phone: String?,
    val user_type:String?,
    val amount:String?=null
) : Parcelable

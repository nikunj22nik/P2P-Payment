package com.p2p.application.model

data class User(
    val id: Int? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val phone: String? = null,
    val fcm_token: String? = null,
    val role: String? = null,
    val qr_code: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

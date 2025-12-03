package com.p2p.application.model

data class RegisterResponse(
    val user: User? = null,
    val token: String? = null
)

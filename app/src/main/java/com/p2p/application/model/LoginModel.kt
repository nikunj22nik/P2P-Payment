package com.p2p.application.model

data class LoginModel(
    val user: LoginUserModel,
    val token: String?,
    val first_login_status :Boolean = false
)

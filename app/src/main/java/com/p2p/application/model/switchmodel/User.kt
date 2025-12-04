package com.p2p.application.model.switchmodel

data class User(
    val business_logo: String?,
    val first_name: String?,
    val id: Int,
    val last_name: String?,
    val phone: String,
    val role: String?,
    val type: String,
    val title: String?,
    val userActive: Boolean?,
    val accountActive: Boolean?
)
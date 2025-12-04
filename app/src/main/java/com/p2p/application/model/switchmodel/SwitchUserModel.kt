package com.p2p.application.model.switchmodel

data class SwitchUserModel(
    val code: Int,
    val `data`: Data?,
    val message: String,
    val success: Boolean
)
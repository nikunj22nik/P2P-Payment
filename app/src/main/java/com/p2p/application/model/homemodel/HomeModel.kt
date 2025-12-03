package com.p2p.application.model.homemodel

data class HomeModel(
    val code: Int,
    val `data`: Data?,
    val message: String,
    val success: Boolean
)
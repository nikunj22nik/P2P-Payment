package com.p2p.application.model.newnumber

data class NewNumberModel(
    val code: Int,
    val `data`: MutableList<Data>?,
    val message: String,
    val success: Boolean
)
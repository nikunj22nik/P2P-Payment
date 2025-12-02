package com.p2p.application.model.countrymodel

data class CountryModel(
    val code: Int,
    val `data`: Data?,
    val message: String,
    val success: Boolean
)
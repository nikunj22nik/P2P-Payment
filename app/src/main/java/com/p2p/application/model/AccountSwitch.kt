package com.p2p.application.model

data class AccountSwitch(
    val name: String,
    val phone: String,
    val userType: String, // e.g., "User", "Merchant", etc.
    val isActive: Boolean,
    val title: String,
    val accountActive: Boolean
)
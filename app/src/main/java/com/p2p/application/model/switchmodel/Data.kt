package com.p2p.application.model.switchmodel

data class Data(
    val agent: User,
    val master_agent: User,
    val merchant: User,
    val user: User
)
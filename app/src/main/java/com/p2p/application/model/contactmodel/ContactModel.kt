package com.p2p.application.model.contactmodel

data class ContactModel(
    var id: String,
    var name: String,
    var phone: String?,
    var email: String? = null,
    var photoUri: String? = null
)

package com.p2p.application.model

data class UserInfo( val id: Int,
                     val first_name: String,
                     val last_name: String?,
                     val phone: String,
                     val business_logo: String?
)

package com.p2p.application.model

data class TransactionItem( val id: Int,
                            val amount: String,
                            val currency: String,
                            val status: String,
                            val date: String?,
                            val time:String?,
                            val transaction_type: String,
                            val transaction_mode:String?=null,
                            val user: UserInfo,
                            val transaction_category:String =""
    )

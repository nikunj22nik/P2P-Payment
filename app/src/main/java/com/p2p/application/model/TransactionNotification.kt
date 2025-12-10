package com.p2p.application.model

data class TransactionNotification(val id: Int? = null,
                                   val user_id: Int? = null,
                                   val title: String? = null,
                                   val message: String? = null,
                                   val amount: String? = null,
                                   val type: String? = null,
                                   val transaction_id: String? = null,
                                   val is_read: Int? = null,
                                   val created_at: String? = null,
                                   val updated_at: String? = null)

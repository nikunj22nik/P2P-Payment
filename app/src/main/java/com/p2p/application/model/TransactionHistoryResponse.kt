package com.p2p.application.model

data class TransactionHistoryResponse(
    val page: Int,
    val limit: Int,
    val total: Int,
    val total_page: Int,
    val data: List<TransactionItem>
)

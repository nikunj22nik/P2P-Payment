package com.p2p.application.model

sealed class HistoryItem {
    data class Header(val month: String) : HistoryItem()
    data class Transaction(
        val title: String,
        val phone: String,
        val date: String,
        val amount: Double,
        val profile:String?=null,
        val id :String ="",
        var currency :String ="FCFA",
        var rebalance:String ="normal"
    ) : HistoryItem()
}

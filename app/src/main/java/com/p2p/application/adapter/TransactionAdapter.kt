package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.R
import com.p2p.application.model.HistoryItem
import androidx.core.graphics.toColorInt

class TransactionAdapter(
    private val items: List<HistoryItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is HistoryItem.Header -> TYPE_HEADER
            is HistoryItem.Transaction -> TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false)
            TransactionViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HistoryItem.Header -> (holder as HeaderViewHolder).bind(item)
            is HistoryItem.Transaction -> (holder as TransactionViewHolder).bind(item)
        }
    }

    override fun getItemCount() = items.size

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val month: TextView = itemView.findViewById(R.id.tvMonth)
        fun bind(data: HistoryItem.Header) {
            month.text = data.month
        }
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvName)
        private val date: TextView = itemView.findViewById(R.id.tvDate)
        private val amount: TextView = itemView.findViewById(R.id.price)

        @SuppressLint("SetTextI18n")
        fun bind(data: HistoryItem.Transaction) {
            title.text = data.title
            date.text = data.date

            amount.text = "${data.amount} CFA"
            amount.setTextColor(
                if (data.amount > 0) "#2ecc71".toColorInt() else "#e74c3c".toColorInt()
            )
        }
    }
}

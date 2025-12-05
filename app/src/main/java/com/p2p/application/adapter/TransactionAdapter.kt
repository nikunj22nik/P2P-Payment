package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.R
import com.p2p.application.model.HistoryItem
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class TransactionAdapter(
    private var items: List<HistoryItem>,var type:String ="list",
    private val onTransactionClick: (userId: Int,userName :String, userNumber:String,userProfile:String?) -> Unit
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
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            val view = inflater.inflate(R.layout.item_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_transaction, parent, false)
            TransactionViewHolder(view, onTransactionClick,type)
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
            month.text = expandMonthYear(data.month)
        }

        private fun expandMonthYear(monthYear: String): String {
            val parts = monthYear.split(" ")
            if (parts.size >= 2) {
                val monthAbbr = parts[0]
                val year = parts[1]
                val fullMonth = when (monthAbbr) {
                    "Jan" -> "January"
                    "Feb" -> "February"
                    "Mar" -> "March"
                    "Apr" -> "April"
                    "May" -> "May"
                    "Jun" -> "June"
                    "Jul" -> "July"
                    "Aug" -> "August"
                    "Sep" -> "September"
                    "Oct" -> "October"
                    "Nov" -> "November"
                    "Dec" -> "December"
                    else -> monthAbbr
                }
                return "$fullMonth $year"
            }
            return ""
        }
    }

    class TransactionViewHolder(
        itemView: View,
        private val onTransactionClick: (userId: Int,userName :String, userNumber:String,userProfile:String?) -> Unit,val type:String
    ) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.tvName)
        private val date: TextView = itemView.findViewById(R.id.tvDate)
        private val amount: TextView = itemView.findViewById(R.id.price)
        private val image: CircleImageView = itemView.findViewById(R.id.imageProfile)
        private val lay: RelativeLayout = itemView.findViewById(R.id.trans_layout)

        @SuppressLint("SetTextI18n")
        fun bind(data: HistoryItem.Transaction) {
            if (data.amount < 0) {
                // Debit
                title.setTextColor(Color.parseColor("#0F0D1C"))
                amount.text = "${data.amount} CFA"
                title.text = if (data.phone.isNotEmpty()) "To ${data.title}" else data.title
            } else {
                // Credit
                title.setTextColor(Color.parseColor("#03B961"))
                amount.text = "+${data.amount} CFA"
                title.text = if (data.phone.isNotEmpty()) "From ${data.title}" else data.title
            }
            date.text = if (!isToday(data.date)) data.date else "Today"
            amount.setTextColor(
                if (data.amount > 0) Color.parseColor("#03B961")
                else Color.parseColor("#E74C3C")
            )
            val url = BuildConfig.MEDIA_URL + (data.profile ?: "")
            if(type.equals("list",true)){
                Glide.with(itemView.context)
                    .load(url.ifEmpty { null })
                    .placeholder(R.drawable.transfericon)
                    .error(R.drawable.transfericon)
                    .into(image)
            }else{
                if(data.amount < 0){
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_outgoing)
                        .placeholder(R.drawable.ic_outgoing)
                        .error(R.drawable.ic_outgoing)
                        .into(image)
                }else{
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_incoming)
                        .placeholder(R.drawable.ic_incoming)
                        .error(R.drawable.ic_incoming)
                        .into(image)
                }
            }
            lay.setOnClickListener {
                onTransactionClick(data.id.toInt(),data.title,data.phone,data.profile)
            }
        }

        private fun isToday(dateString: String): Boolean {
            val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            return try {
                val inputDate = format.parse(dateString) ?: return false
                val today = Calendar.getInstance()
                val cal = Calendar.getInstance()
                cal.time = inputDate
                cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                        && cal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                        && cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
            } catch (e: Exception) {
                false
            }
        }
    }

    fun updateAdapter(newItems: List<HistoryItem>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}

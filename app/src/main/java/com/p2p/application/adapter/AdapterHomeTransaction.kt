package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.databinding.ItemHomeTransactionBinding
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.homemodel.Transaction
import com.p2p.application.util.LoadingUtils.Companion.formatDateOnly
import com.p2p.application.util.MessageError


class AdapterHomeTransaction(
    private var requireActivity: Context,
    var itemClickListener: ItemClickListenerType,
    var transactionsList: MutableList<Transaction>,
    var selectedType: String
) :
    RecyclerView.Adapter<AdapterHomeTransaction.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemHomeTransactionBinding =
            ItemHomeTransactionBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }
    @SuppressLint("NotifyDataSetChanged", "DefaultLocale", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = transactionsList[position]
        val date = formatDateOnly(data.date ?: "")
        val time = data.time ?: ""
        val text = "$date · $time"
        holder.binding.tvDate.text = text
        data.transaction_mode?.let { type->
            if (type.equals("rebalancing",true)){
                data.transaction_type?.let { type->
                    if (type.equals("debit",true)){
                        if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)){
                            holder.binding.tvName.setTextColor("#FFFFFF".toColorInt())
                        }else{
                            holder.binding.tvName.setTextColor("#0F0D1C".toColorInt())
                        }
                        holder.binding.price.text = "-"+(String.format("%.2f", (data.amount ?: "0.0").toDouble()))+" "+ (data.currency?:"")
                        holder.binding.price.setTextColor("#F90B1B".toColorInt())
                    }else{
                        if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)){
                            holder.binding.tvName.setTextColor("#FFFFFF".toColorInt())
                        }else{
                            holder.binding.tvName.setTextColor("#0F0D1C".toColorInt())
                        }
                        holder.binding.price.setTextColor("#03B961".toColorInt())
                        holder.binding.price.text = "+"+(String.format("%.2f", (data.amount ?: "0.0").toDouble()))+" "+ (data.currency?:"")
                    }
                    holder.binding.tvName.text = "Rebalancing"
                    Glide.with(requireActivity)
                        .load(R.drawable.icon_rebalancing)
                        .error(R.drawable.icon_rebalancing)
                        .into(holder.binding.imageProfile)
                }
            }else{
                data.transaction_type?.let { type->
                    if (type.equals("debit",true)){
                        if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)){
                            holder.binding.tvName.setTextColor("#FFFFFF".toColorInt())
                        }else{
                            holder.binding.tvName.setTextColor("#0F0D1C".toColorInt())
                        }
                        holder.binding.price.text = "-"+(String.format("%.2f", (data.amount ?: "0.0").toDouble()))+" "+ (data.currency?:"")
                        holder.binding.price.setTextColor("#F90B1B".toColorInt())
                        data.user?.business_logo?.let { url->
                            holder.binding.tvName.text = (data.user.first_name ?:"") +" "+ (data.user.last_name ?:"")
                            Glide.with(requireActivity)
                                .load(BuildConfig.MEDIA_URL+url)
                                .placeholder(R.drawable.transfericon)
                                .error(R.drawable.transfericon)
                                .into(holder.binding.imageProfile)
                        }?:run {
                            holder.binding.tvName.text = "To "+ (data.user?.first_name ?:"") +" "+ (data.user?.last_name ?:"")
                            Glide.with(requireActivity)
                                .load(R.drawable.transfericon)
                                .placeholder(R.drawable.transfericon)
                                .error(R.drawable.transfericon)
                                .into(holder.binding.imageProfile)
                        }
                    }else{
                        if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)){
                            holder.binding.tvName.setTextColor("#FFFFFF".toColorInt())
                        }else{
                            holder.binding.tvName.setTextColor("#0F0D1C".toColorInt())
                        }
                        holder.binding.price.setTextColor("#03B961".toColorInt())
                        holder.binding.price.text = "+"+(String.format("%.2f", (data.amount ?: "0.0").toDouble()))+" "+ (data.currency?:"")
                        data.user?.business_logo?.let { url->
                            holder.binding.tvName.text = (data.user.first_name ?:"") +" "+ (data.user.last_name ?:"")
                            Glide.with(requireActivity)
                                .load(BuildConfig.MEDIA_URL+url)
                                .into(holder.binding.imageProfile)
                        }?:run {
                            holder.binding.tvName.text = "From "+ (data.user?.first_name ?:"") +" "+ (data.user?.last_name ?:"")
                            Glide.with(requireActivity)
                                .load(R.drawable.transfericon)
                                .into(holder.binding.imageProfile)
                        }
                    }
                }
            }
        }?:run {
            data.transaction_type?.let { type->
                if (type.equals("debit",true)){
                    if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)){
                        holder.binding.tvName.setTextColor("#FFFFFF".toColorInt())
                    }else{
                        holder.binding.tvName.setTextColor("#0F0D1C".toColorInt())
                    }
                    holder.binding.price.text = "-"+(String.format("%.2f", (data.amount ?: "0.0").toDouble()))+" "+ (data.currency?:"")
                    holder.binding.price.setTextColor("#F90B1B".toColorInt())
                    data.user?.business_logo?.let { url->
                        holder.binding.tvName.text = (data.user.first_name ?:"") +" "+ (data.user.last_name ?:"")
                        Glide.with(requireActivity)
                            .load(BuildConfig.MEDIA_URL+url)
                            .into(holder.binding.imageProfile)
                    }?:run {
                        holder.binding.tvName.text = "To "+ (data.user?.first_name ?:"") +" "+ (data.user?.last_name ?:"")
                        Glide.with(requireActivity)
                            .load(R.drawable.transfericon)
                            .into(holder.binding.imageProfile)
                    }
                }else{
                    if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)){
                        holder.binding.tvName.setTextColor("#FFFFFF".toColorInt())
                    }else{
                        holder.binding.tvName.setTextColor("#0F0D1C".toColorInt())
                    }
                    holder.binding.price.setTextColor("#03B961".toColorInt())
                    holder.binding.price.text = "+"+(String.format("%.2f", (data.amount ?: "0.0").toDouble()))+" "+ (data.currency?:"")
                    data.user?.business_logo?.let { url->
                        holder.binding.tvName.text = (data.user.first_name ?:"") +" "+ (data.user.last_name ?:"")
                        Glide.with(requireActivity)
                            .load(BuildConfig.MEDIA_URL+url)
                            .into(holder.binding.imageProfile)
                    }?:run {
                        holder.binding.tvName.text = "From "+ (data.user?.first_name ?:"") +" "+ (data.user?.last_name ?:"")
                        Glide.with(requireActivity)
                            .load(R.drawable.transfericon)
                            .into(holder.binding.imageProfile)
                    }
                }
            }
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(data.id.toString(),"receiptFragment")
        }
    }
    override fun getItemCount(): Int {
        return transactionsList.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<Transaction>) {
        transactionsList = list
        notifyDataSetChanged()
    }
    class ViewHolder(var binding: ItemHomeTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}
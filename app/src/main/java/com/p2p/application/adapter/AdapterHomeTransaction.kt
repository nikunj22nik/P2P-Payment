package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.databinding.ItemHomeTransactionBinding
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.homemodel.Transaction
import com.p2p.application.util.LoadingUtils.Companion.formatTime

class AdapterHomeTransaction(
    private var requireActivity: Context,
    var itemClickListener: ItemClickListener,
    var transactionsList: MutableList<Transaction>
) :
    RecyclerView.Adapter<AdapterHomeTransaction.ViewHolder>() {

        var color: String="#0F0D1C"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemHomeTransactionBinding =
            ItemHomeTransactionBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = transactionsList[position]
//        holder.binding.tvName.setTextColor(Color.parseColor(color))
        /*if (position==0){
            holder.binding.imageProfile.setBackgroundResource(R.drawable.transfericon)
        }else{
            holder.binding.imageProfile.setBackgroundResource(R.drawable.rebalancingicon)
        }*/
        holder.binding.tvDate.text = formatTime(data.created_at?:"")
        data.transaction_type?.let { type->
            if (type.equals("debit",true)){
                holder.binding.tvName.setTextColor(Color.parseColor("#0F0D1C"))
                holder.binding.price.text = "-"+(data.amount?:"")+" "+ (data.currency?:"")
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
                holder.binding.tvName.setTextColor(Color.parseColor("#03B961"))
                holder.binding.price.text = "-"+(data.amount?:"")+" "+ (data.currency?:"")
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
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick("")
        }
    }

    fun updateColor(colorType: String){
        color=colorType
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return transactionsList.size
    }
    fun updateList(list: MutableList<Transaction>) {
        transactionsList = list
        notifyDataSetChanged()
    }
    class ViewHolder(var binding: ItemHomeTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
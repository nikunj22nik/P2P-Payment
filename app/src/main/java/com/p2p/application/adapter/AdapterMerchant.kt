package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.databinding.ItemPaymentBinding
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.recentmerchant.Merchant

class AdapterMerchant(private var requireActivity: Context, var merchantList: MutableList<Merchant>, var itemClickListenerType: ItemClickListenerType) :
    RecyclerView.Adapter<AdapterMerchant.ViewHolder>() {

        var color: String="#0F0D1C"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPaymentBinding =
            ItemPaymentBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = merchantList[position]
        Log.d("image","****"+BuildConfig.MEDIA_URL+(data.business_logo?:""))
        Glide.with(requireActivity)
            .load(BuildConfig.MEDIA_URL+(data.business_logo?:""))
            .into(holder.binding.imageProfile)
        holder.binding.tvName.text = (data.first_name?:"") + "\n" + (data.last_name?:"")
        holder.itemView.setOnClickListener {
            itemClickListenerType.onItemClick(position.toString(),"merchant")
        }
    }


    override fun getItemCount(): Int {
        return merchantList.size

    }



    class ViewHolder(var binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
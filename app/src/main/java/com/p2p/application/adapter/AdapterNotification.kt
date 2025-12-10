package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.databinding.ItemContactBinding
import com.p2p.application.databinding.ItemNotificationBinding
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.TransactionNotification

class AdapterNotification(private var requireActivity: Context,private var list : MutableList<TransactionNotification> = mutableListOf<TransactionNotification>()) :
    RecyclerView.Adapter<AdapterNotification.ViewHolder>() {
        var color: String="#0F0D1C"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemNotificationBinding =
            ItemNotificationBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.title.text = list.get(position).message
        holder.binding.tvDate.text = list.get(position).created_at
    }

    override fun getItemCount(): Int {
        return list.size
    }



    fun updateAdapter(list : MutableList<TransactionNotification>){
        this.list = list
          notifyDataSetChanged()
    }


    class ViewHolder(var binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

    }

}
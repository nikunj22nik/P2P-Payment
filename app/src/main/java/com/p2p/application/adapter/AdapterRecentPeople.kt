package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.databinding.ItemNotificationBinding
import com.p2p.application.databinding.ItemPaymentBinding
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.recentpepole.Data

class AdapterRecentPeople(private var requireActivity: Context, var peopleList: MutableList<Data>, var itemClickListenerType: ItemClickListenerType) :
    RecyclerView.Adapter<AdapterRecentPeople.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPaymentBinding =
            ItemPaymentBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


    }


    fun updateData(list: MutableList<Data>){
        peopleList= list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return peopleList.size

    }



    class ViewHolder(var binding: ItemPaymentBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
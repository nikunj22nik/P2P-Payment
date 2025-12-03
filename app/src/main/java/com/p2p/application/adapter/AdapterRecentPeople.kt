package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.databinding.ItemNotificationBinding
import com.p2p.application.databinding.ItemPaymentBinding
import com.p2p.application.databinding.ItemPeopleBinding
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.recentpepole.RecentPeople
import com.p2p.application.model.recentpepole.Data

class AdapterRecentPeople(private var requireActivity: Context, var peopleList: MutableList<RecentPeople>, var itemClickListenerType: ItemClickListenerType) :
    RecyclerView.Adapter<AdapterRecentPeople.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemPeopleBinding =
            ItemPeopleBinding.inflate(inflater, parent, false);
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= peopleList[position]
        holder.binding.tvName.text = data.first_name + "\n"+data.last_name
        Glide.with(requireActivity)
            .load(BuildConfig.MEDIA_URL + (data.business_logo?:""))
            .placeholder(R.drawable.usericon)
            .error(R.drawable.usericon)
            .into(holder.binding.imageProfile)
        holder.itemView.setOnClickListener {
            itemClickListenerType.onItemClick(data.id.toString(),"")
        }
    }

    fun updateData(list: MutableList<RecentPeople>){
        peopleList= list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return peopleList.size

    }

    class ViewHolder(var binding: ItemPeopleBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
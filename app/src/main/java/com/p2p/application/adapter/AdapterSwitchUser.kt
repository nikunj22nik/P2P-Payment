package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.databinding.ItemCountryBinding
import com.p2p.application.databinding.ItemSwitchUserBinding
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.AccountSwitch
import com.p2p.application.model.contactmodel.ContactModel
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.model.switchmodel.User
import com.p2p.application.util.LoadingUtils.Companion.toInitials

class AdapterSwitchUser(var requireActivity: Context, var itemClickListener: ItemClickListener, var switchUserList: MutableList<User>) :
    RecyclerView.Adapter<AdapterSwitchUser.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemSwitchUserBinding =
            ItemSwitchUserBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= switchUserList[position]
        if (data.accountActive == true){
            holder.binding.layActive.visibility = View.VISIBLE
            holder.binding.layInActive.visibility = View.GONE
            val name = (data.first_name?:"") +" "+ (data.last_name?:"")
            val firstName = data.first_name?:""
            val lastName = data.last_name?:""
            val capitalName = listOfNotNull(firstName, lastName)
                .joinToString(" ")
                .trim()
                .split(" ")
                .filter { it.isNotEmpty() }
                .joinToString(" ") {
                    it.replaceFirstChar { ch -> ch.uppercase() }
                }
            holder.binding.tvName.text = capitalName
            holder.binding.tvShortName.text = toInitials(name)
            holder.binding.tvPhone.text = data.phone
            holder.binding.tvRole.text = data.type
            if (data.userActive == true){
                holder.binding.imgActive.setImageResource(R.drawable.icon_select)
            }else{
                holder.binding.imgActive.setImageResource(R.drawable.circleunselect)
            }
        }else{
            holder.binding.tvTitle.text = data.title
            holder.binding.tvRoleTYpe.text = data.type
            holder.binding.layActive.visibility = View.GONE
            holder.binding.layInActive.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position.toString())
        }
    }

    override fun getItemCount(): Int {
        return switchUserList.size

    }


    class ViewHolder(var binding: ItemSwitchUserBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
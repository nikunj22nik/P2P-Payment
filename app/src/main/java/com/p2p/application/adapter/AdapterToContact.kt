package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.databinding.ItemContactBinding
import com.p2p.application.databinding.ItemHomeTransactionBinding
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.contactmodel.ContactModel

class AdapterToContact(private var requireActivity: Context,var itemClickListener: ItemClickListenerType, var list: MutableList<ContactModel>) :
    RecyclerView.Adapter<AdapterToContact.ViewHolder>() {

        var color: String="#0F0D1C"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemContactBinding =
            ItemContactBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data=list[position]

        holder.binding.tvName.text= data.name

        holder.binding.tvNumber.text= data.phone

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(data.phone.toString(),"")
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(updateList: MutableList<ContactModel>){
        list=updateList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return list.size

    }



    class ViewHolder(var binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
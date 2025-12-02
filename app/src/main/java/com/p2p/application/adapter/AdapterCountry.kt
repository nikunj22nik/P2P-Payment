package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.databinding.ItemContactBinding
import com.p2p.application.databinding.ItemCountryBinding
import com.p2p.application.databinding.ItemHomeTransactionBinding
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.contactmodel.ContactModel

class AdapterCountry(private var requireActivity: Context, var itemClickListener: ItemClickListener ) :
    RecyclerView.Adapter<AdapterCountry.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemCountryBinding =
            ItemCountryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick("")
        }
    }

    fun updateList(updateList: MutableList<ContactModel>){
//        list=updateList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return 30

    }



    class ViewHolder(var binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
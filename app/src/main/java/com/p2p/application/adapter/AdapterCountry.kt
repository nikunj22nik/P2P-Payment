package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.databinding.ItemCountryBinding
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.contactmodel.ContactModel
import com.p2p.application.model.countrymodel.Country

class AdapterCountry(
    var requireActivity: Context,
    var itemClickListener: ItemClickListener,
    var countryList: MutableList<Country>
) :
    RecyclerView.Adapter<AdapterCountry.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemCountryBinding =
            ItemCountryBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data= countryList[position]

        data.country_code?.let {
            holder.binding.tvCode.text = it
        }

        data.icon?.let {
            Glide.with(requireActivity)
                .load(BuildConfig.MEDIA_URL+it)
                .into(holder.binding.imgIcon)
        }

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position.toString())
        }

    }

    fun updateList(updateList: MutableList<Country>){
        countryList=updateList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return countryList.size

    }



    class ViewHolder(var binding: ItemCountryBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
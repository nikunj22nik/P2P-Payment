package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.databinding.ItemHomeTransactionBinding
import com.p2p.application.listener.ItemClickListener

class AdapterHomeTransaction(private var requireActivity: Context,var itemClickListener: ItemClickListener) :
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

        holder.binding.tvName.setTextColor(Color.parseColor(color))

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick("")
        }
    }


    fun updateColor(colorType: String){
        color=colorType
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return 10

    }



    class ViewHolder(var binding: ItemHomeTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
    }

}
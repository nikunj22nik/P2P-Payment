package com.p2p.application.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.R
import com.p2p.application.databinding.ItemImageUploadBinding
import com.p2p.application.databinding.ItemNotificationBinding
import com.p2p.application.databinding.ItemPaymentBinding


class AdapterMerchantVerification(
    private val context: Context,
    // Data list 'var' rehne dein, taki external list ka reference rahe
    private var dataList: MutableList<Uri> = mutableListOf(),
    // Callback mein 'Unit' return hoga, kyunki ab yeh external modification trigger karega.
    private val onItemRemoved: ((position: Int, uri: Uri) -> Unit)? = null
) : RecyclerView.Adapter<AdapterMerchantVerification.ViewHolder>() {

    inner class ViewHolder(val binding: ItemImageUploadBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageUploadBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = dataList[position]
        val mime = getMimeType(holder.binding.root.context, uri)

        when {
            mime?.startsWith("image") == true -> {
                Log.d("TYPE1", "It's an image")
                holder.binding.mainImage.setImageURI(uri)
            }
            mime == "application/pdf" -> {
                Log.d("TYPE1", "It's a PDF")
                holder.binding.mainImage.setImageResource(R.drawable.pdf_img)
            }
            else -> {

            }
        }


        holder.binding.cutImg.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                // Yahan hum removeItem() ko call karenge.
                // Yeh ab internal list ko modify nahi karega.
                removeItem(pos)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    /**
     * **FIXED:** Yeh method ab list ko modify NAHI karta.
     * Yeh sirf external code ko batata hai ki kaun sa item remove karna hai.
     */
    private fun removeItem(position: Int) {
        if (position < 0 || position >= dataList.size) {
            Log.e("AdapterError", "Attempted to signal removal at invalid position: $position")
            return
        }

        val removedUri = dataList[position] // Sirf URI lene ke liye

        // 1. External Callback ko call karein.
        // **External code ab list ko modify karega aur notifyItemRemoved() bhi call karega.**
        onItemRemoved?.invoke(position, removedUri)

        // 🛑 NO notifyItemRemoved(position) or dataList.removeAt(position) here!
    }

    // updateAdapter method remains the same
    fun updateAdapter(newList: MutableList<Uri>) {
        dataList = newList
        Log.d("AdapterMerchantVerification", "List updated size: ${dataList.size}")
        notifyDataSetChanged()
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        val contentResolver = context.contentResolver
        return contentResolver.getType(uri)
    }
}
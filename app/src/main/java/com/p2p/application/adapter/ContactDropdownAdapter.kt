package com.p2p.application.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.p2p.application.R
import com.p2p.application.model.contactmodel.ContactModel

class ContactDropdownAdapter(
    context: Context,
    list: List<ContactModel>
) : ArrayAdapter<ContactModel>(context, 0, ArrayList(list)) {

    private val originalList = ArrayList<ContactModel>(list)
    private val filteredList = ArrayList<ContactModel>(list)

    override fun getCount(): Int = filteredList.size

    override fun getItem(position: Int): ContactModel = filteredList[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_contact, parent, false)

        val item = filteredList[position]

        view.findViewById<TextView>(R.id.tvName).text = item.name
        view.findViewById<TextView>(R.id.tvPhone).text = item.phone

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val query = constraint?.toString()?.lowercase()?.trim()

                val filtered = if (query.isNullOrEmpty()) {
                    originalList
                } else {
                    originalList.filter {
                        it.phone
                            ?.replace(" ", "")
                            ?.contains(query.replace(" ", "")) == true ||
                                it.name.lowercase().contains(query)
                    }
                }

                results.values = filtered
                results.count = filtered.size
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                filteredList.clear()

                val values = results.values
                if (values is List<*>) {
                    filteredList.addAll(values.filterIsInstance<ContactModel>())
                }

                notifyDataSetChanged()
            }
        }
    }

    // 🔥 REQUIRED for AutoCompleteTextView
    fun convertResultToString(resultValue: Any): CharSequence {
        return (resultValue as ContactModel).phone ?: ""
    }
}



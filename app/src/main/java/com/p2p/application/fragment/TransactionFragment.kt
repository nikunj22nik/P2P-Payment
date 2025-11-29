package com.p2p.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.adapter.TransactionAdapter
import com.p2p.application.databinding.FragmentTransactionBinding
import com.p2p.application.model.HistoryItem
import com.p2p.application.util.CommonFunction.Companion.SPLASH_DELAY
import com.p2p.application.util.MessageError
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {


    private lateinit var binding: FragmentTransactionBinding
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(layoutInflater, container, false)


        lifecycleScope.launch {
            delay(SPLASH_DELAY)
             binding.imgNoData.visibility = View.GONE
             binding.itemRcy.visibility = View.VISIBLE
        }


        val items = listOf(
            HistoryItem.Header("October"),
            HistoryItem.Transaction("To Beryl (01 33 23 43 44)", "", "Yesterday 19:12", -1400.0),
            HistoryItem.Transaction("Rebalancing", "", "June 29 19:12", 25000.0),
            HistoryItem.Transaction("To Amadou Bio", "", "June 28 2025", -6000.0),

            HistoryItem.Header("September"),
            HistoryItem.Transaction("To Amadou", "", "June 28 2025", -6000.0)
        )
        adapter= TransactionAdapter(items)

        adapter = TransactionAdapter(items)
        binding.itemRcy.adapter = adapter

         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}
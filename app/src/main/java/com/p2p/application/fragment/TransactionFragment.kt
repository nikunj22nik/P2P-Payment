package com.p2p.application.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.adapter.TransactionAdapter
import com.p2p.application.databinding.FragmentTransactionBinding
import com.p2p.application.model.HistoryItem
import com.p2p.application.util.AppConstant
import com.p2p.application.util.CommonFunction.Companion.SPLASH_DELAY
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var adapter: TransactionAdapter
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var popupWindow: PopupWindow?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        lifecycleScope.launch {
            delay(SPLASH_DELAY)
             binding.imgNoData.visibility = View.GONE
             binding.itemRcy.visibility = View.VISIBLE
        }
        if (selectedType.equals(AppConstant.MASTER_AGENT,true)){
            binding.layShow.visibility = View.GONE
            binding.imgQuestion.visibility = View.VISIBLE
            binding.layHide.visibility = View.VISIBLE
        }else{
            binding.layShow.visibility = View.VISIBLE
            binding.imgQuestion.visibility = View.GONE
            binding.layHide.visibility = View.GONE
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
        binding.layTransaction.setOnClickListener {
            alertView()
        }
    }

    private fun alertView(){
        val anchorView = binding.layTransaction
        anchorView.post {
            val inflater = LayoutInflater.from(requireContext())
            val popupView = inflater.inflate(R.layout.alert_transation, null)
            popupWindow = PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            val tvAll = popupView.findViewById<TextView>(R.id.tvAll)
            val tvFrom = popupView.findViewById<TextView>(R.id.tvFrom)
            tvAll.setOnClickListener {
                binding.tvName.text="All Transactions"
                popupWindow?.dismiss()
            }
            tvFrom.setOnClickListener {
                binding.tvName.text="From BBS"
                popupWindow?.dismiss()
            }
            popupWindow?.setBackgroundDrawable(null)
            popupWindow?.isOutsideTouchable = true
            popupWindow?.showAsDropDown(anchorView, 0, 20) // 20px margin from top
        }
    }

}
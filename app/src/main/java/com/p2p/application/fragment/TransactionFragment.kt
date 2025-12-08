package com.p2p.application.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.adapter.TransactionAdapter
import com.p2p.application.databinding.FragmentTransactionBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.HistoryItem
import com.p2p.application.model.TransactionItem
import com.p2p.application.util.AppConstant
import com.p2p.application.util.CommonFunction.Companion.SPLASH_DELAY
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var adapter: TransactionAdapter
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var popupWindow: PopupWindow?=null
    private lateinit var viewModel : TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        selectedType = sessionManager.getLoginType().orEmpty()

        lifecycleScope.launch {
             binding.itemRcy.visibility = View.VISIBLE
        }

        if (selectedType.equals(AppConstant.MASTER_AGENT,true)){
            binding.layShow.visibility = View.GONE
            binding.imgQuestion.visibility = View.VISIBLE
            binding.layHide.visibility = View.VISIBLE
        }

        else{
            binding.layShow.visibility = View.VISIBLE
            binding.imgQuestion.visibility = View.GONE
            binding.layHide.visibility = View.GONE
        }

        val items = mutableListOf<HistoryItem>()

        adapter = TransactionAdapter(items){  userId, userName, userNumber, userProfile->
            val bundle = Bundle()
            bundle.putInt("userId", userId)
            bundle.putString("userName", userName)
            bundle.putString("userNumber", userNumber)
            bundle.putString("userProfile", userProfile)
            findNavController().navigate(R.id.individualTransactionFragment,bundle)
        }
        binding.itemRcy.adapter = adapter
        callingRecyclerSetupPagination()
        callingTransactionHistoryApi()
        return binding.root
    }

    private fun callingRecyclerSetupPagination() {
        binding.itemRcy.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()

                if (!viewModel.isLoading && !viewModel.isLastPage) {
                    if (lastVisible == adapter.itemCount - 1) {
                        viewModel.nextPage()

                        lifecycleScope.launch {
                            viewModel.getTransactionHistory().collect { result ->
                                when (result) {

                                    is NetworkResult.Success -> {


                                        val response = result.data

                                        val list = withContext(Dispatchers.Default) {
                                            response?.data?.let { data ->
                                                buildHistoryList(data).toMutableList()
                                            } ?: mutableListOf()
                                        }



                                        adapter.updateAdapter(list)

                                        viewModel.isLoading = false
                                        binding.itemRcy.post {
                                          //  LoadingUtils.hide(requireActivity())
                                        }
                                        viewModel.isLastPage = viewModel.currentPage >= (response?.total_page ?: 1)
                                    }

                                    is NetworkResult.Error -> {
                                        LoadingUtils.hide(requireActivity())
                                        LoadingUtils.showErrorDialog(requireActivity(), result.message.toString())
                                        viewModel.isLoading = false
                                    }

                                    else -> Unit
                                }
                            }
                        }
                    }
                }
            }
        })
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

   private fun callingTransactionHistoryApi(){

       if (!isOnline(requireContext())) {
           LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
           return
       }


       lifecycleScope.launch {
           LoadingUtils.show(requireActivity())
           viewModel.getTransactionHistory().collect { result ->

                when (result) {

                    is NetworkResult.Success -> {

                        val response = result.data

                        val list = withContext(Dispatchers.Default) {
                            response?.data?.let { data ->
                                buildHistoryList(data).toMutableList()
                            } ?: mutableListOf()
                        }


                        adapter.updateAdapter(list)


                        Choreographer.getInstance().postFrameCallback{
                            LoadingUtils.hide(requireActivity())
                        }

                        viewModel.isLastPage = viewModel.currentPage >= (response?.total_page ?: 1)
                    }

                    is NetworkResult.Error -> {
                        LoadingUtils.hide(requireActivity())
                        LoadingUtils.showErrorDialog(requireActivity(), result.message.toString())
                        viewModel.isLoading = false
                    }

                    else -> Unit
                }
            }
        }
    }


    fun buildHistoryList(transactions: List<TransactionItem>): List<HistoryItem> {

            val historyList = mutableListOf<HistoryItem>()
            val grouped = transactions.groupBy { item ->
                val dateStr = item.date ?: "Unknown"  // Handle null
                val parts = dateStr.split(" ")
                if (parts.size >= 3) {
                val month = parts[1]
                val year = parts[2]
                "$month $year"
            } else {
                "Unknown"
            }
        }
            val sortedGroups = grouped.toSortedMap(compareByDescending { monthYear ->
            val (month, year) = monthYear.split(" ")
            val monthNum = monthToNumber(month)
            year.toInt() * 100 + monthNum     // For proper sorting
           })

        // Convert each group into header + transactions
        sortedGroups.forEach { (monthYear, list) ->

            // Add Header
            historyList.add(HistoryItem.Header(monthYear))

            // Add item rows
            list.forEach { item ->
                historyList.add(
                    HistoryItem.Transaction(
                        title = "${item.user.first_name} ${item.user.last_name}",
                        phone = item.user.phone,
                        date = "${item.date} ${item.time}",
                        amount = if(item.transaction_type.equals("debit",true)) -1*item.amount.toDouble() else item.amount.toDouble()
                        , profile = item.user.business_logo,
                        id = item.user.id.toString()

                    )
                )
            }
        }

        return historyList
    }

    fun expandMonthYear(monthYear: String): String {
        val parts = monthYear.split(" ")
        if (parts.size >= 2) {
            val monthAbbr = parts[0]
            val year = parts[1]

            val fullMonth = when (monthAbbr) {
                "Jan" -> "January"
                "Feb" -> "February"
                "Mar" -> "March"
                "Apr" -> "April"
                "May" -> "May"
                "Jun" -> "June"
                "Jul" -> "July"
                "Aug" -> "August"
                "Sep" -> "September"
                "Oct" -> "October"
                "Nov" -> "November"
                "Dec" -> "December"
                else -> monthAbbr
            }

            return "$fullMonth $year"
        }else{
            return ""
        }

    }
    fun monthToNumber(mon: String): Int {
        return when (mon) {
            "Jan" -> 1
            "Feb" -> 2
            "Mar" -> 3
            "Apr" -> 4
            "May" -> 5
            "Jun" -> 6
            "Jul" -> 7
            "Aug" -> 8
            "Sep" -> 9
            "Oct" -> 10
            "Nov" -> 11
            "Dec" -> 12
            else -> 0
        }
    }



}
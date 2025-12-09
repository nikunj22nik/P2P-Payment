package com.p2p.application.fragment

import android.os.Bundle
import android.view.Choreographer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.adapter.TransactionAdapter
import com.p2p.application.databinding.FragmentIndividualTransactionHistoryBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.HistoryItem
import com.p2p.application.model.TransactionItem
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class IndividualTransactionHistoryFragment : Fragment() {

    private lateinit  var binding : FragmentIndividualTransactionHistoryBinding

    private lateinit var adapter: TransactionAdapter
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var popupWindow: PopupWindow?=null
    private lateinit var viewModel : TransactionViewModel
    private  var userId :Int =0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         userId = requireArguments().getInt("userId")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentIndividualTransactionHistoryBinding.inflate(layoutInflater,container,false)
        sessionManager= SessionManager(requireContext())

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        val userName = requireArguments().getString("userName")
        val userNumber = requireArguments().getString("userNumber")
        val userProfile = requireArguments().getString("userProfile")
        selectedType = sessionManager.getLoginType().orEmpty()

        lifecycleScope.launch {
            binding.itemRcy.visibility = View.VISIBLE
        }

        val items = mutableListOf<HistoryItem>()
        adapter = TransactionAdapter(items,"individual"){ userId, userName, userNumber, userProfile, paymentId ->
            val bundle = Bundle()
            bundle.putString("receiptId", paymentId)
            findNavController().navigate(R.id.receiptFragment,bundle)
        }

        binding.itemRcy.adapter = adapter
        binding.tvName.setText(userName)
        binding.tvPhoneNumber.text = userNumber

        Glide.with(requireActivity())
            .load(BuildConfig.MEDIA_URL + userProfile)
            .placeholder(R.drawable.usernoimg)
            .error(R.drawable.usernoimg)         // shown if URL fails
            .into(binding.ivProfile)

        callingRecyclerSetupPagination()
        callingTransactionHistoryApi()
        binding.ivBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }

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

    private fun callingTransactionHistoryApi(){
        lifecycleScope.launch {

            LoadingUtils.show(requireActivity())

            viewModel.genOneToOneTransactionHistory(userId).collect { result ->

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
                        id = item.id.toString(),
                        currency = item.currency

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
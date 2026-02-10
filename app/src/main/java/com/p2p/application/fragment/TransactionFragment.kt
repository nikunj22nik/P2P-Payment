package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Choreographer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.R
import com.p2p.application.adapter.TransactionAdapter
import com.p2p.application.databinding.FragmentTransactionBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.HistoryItem
import com.p2p.application.model.TransactionItem
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var adapter: TransactionAdapter
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var popupWindow: PopupWindow? = null
    private lateinit var viewModel: TransactionViewModel
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


        binding = FragmentTransactionBinding.inflate(layoutInflater, container, false)

        sessionManager = SessionManager(requireContext())

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        selectedType = sessionManager.getLoginType().orEmpty()

        lifecycleScope.launch {
            binding.itemRcy.visibility = View.VISIBLE
        }

        if (selectedType.equals(AppConstant.MASTER_AGENT, true)) {
            binding.layShow.visibility = View.GONE
            binding.imgQuestion.visibility = View.GONE
            binding.layHide.visibility = View.VISIBLE
        } else {
            binding.layShow.visibility = View.VISIBLE
            binding.imgQuestion.visibility = View.GONE
            binding.layHide.visibility = View.VISIBLE
        }

        val items = mutableListOf<HistoryItem>()

        adapter = TransactionAdapter(items) {
            userId, userName, userNumber, userProfile, paymentId,type ->
            val bundle = Bundle()
            bundle.putInt("userId", userId)
            bundle.putString("userName", userName)
            bundle.putString("userNumber", userNumber)
            bundle.putString("userProfile", userProfile)
            bundle.putString("type",type)
            findNavController().navigate(R.id.individualTransactionFragment, bundle)
        }

        binding.itemRcy.adapter = adapter

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            private var searchJob: Job? = null
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString().orEmpty()
                  if(!viewModel.filter) {
                      searchJob?.cancel()
                      searchJob = lifecycleScope.launch {
                          delay(400) // debounce delay in ms
                          if (query.isEmpty()) {
                              viewModel.isSearching = false
                              adapter.updateAdapter(viewModel.mainList)
                          } else {
                              viewModel.isSearching = true
                              searchTransactions(query)
                          }
                      }
                  }else{
                      adapter.filter(query)
                  }
            }
        })

        callingRecyclerSetupPagination()

        callingTransactionHistoryApi()

        return binding.root
    }

    fun searchTransactions(query: String) {
        searchJob?.cancel()
        Log.d("TESTING_DATA",query.toString())
        searchJob = lifecycleScope.launch {
            delay(400)
          //  LoadingUtils.show(requireActivity(),false)
            viewModel.getTransactionHistory(query.trim()).collect { result ->
                 // LoadingUtils.hide(requireActivity())
                   when (result) {
                       is NetworkResult.Success -> {
                           val response = result.data
                           Log.d("TESTING_DATA"," "+response)

                           val list = withContext(Dispatchers.Default) {
                               response?.data?.let { data ->
                                   Log.d("TESTING_DATA"," "+data)
                                   buildHistoryList(data).toMutableList()
                               } ?: mutableListOf()
                           }

                           Log.d("TESTING_DATA",list.size.toString())

                           if (list.isNotEmpty()){
                               binding.itemRcy.visibility = View.VISIBLE
                               binding.layItem.visibility = View.VISIBLE
                               binding.imgNoData.visibility = View.GONE
                               adapter.updateAdapter(mutableListOf())
                               adapter.updateAdapter(list)
                               Choreographer.getInstance().postFrameCallback {
                                   LoadingUtils.hide(requireActivity())
                               }
                           }
                           else{
                               adapter.updateAdapter(list)
                           }

                       }
                       is NetworkResult.Error -> {
                       //    LoadingUtils.hide(requireActivity())
                           LoadingUtils.showErrorDialog(requireActivity(), result.message.toString())
                           viewModel.isLoading = false
                       }
                       else -> Unit
                   }
               }
        }
    }


    override fun onResume() {
        super.onResume()
    }

    private fun callingRecyclerSetupPagination() {

            binding.itemRcy.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                    Log.d("TESTING_TRANSACTION",
                        "TRUE WHEN CALLED" + lastVisible + " " + viewModel.isLastPage + " " +
                                viewModel.currentPage + " " + viewModel.isLoading
                    )

                    if (!viewModel.isLastPage && !viewModel.filter) {
                        if (lastVisible == adapter.itemCount - 1) {
                            viewModel.nextPage()
                            LoadingUtils.show(requireActivity())
                            lifecycleScope.launch {
                                viewModel.getTransactionHistory().collect { result ->
                                    when (result) {
                                        is NetworkResult.Success -> {
                                            LoadingUtils.hide(requireActivity())
                                            if (binding.edSearch.text.toString().trim().length == 0) {
                                                Log.d("TESTING_TRANSACTION", "TRUE WHEN CALLED")
                                                val response = result.data
                                                val list = withContext(Dispatchers.Default) {
                                                    response?.data?.let { data ->
                                                        mergeHistoryList(
                                                            viewModel.mainList,
                                                            data
                                                        ).toMutableList()
                                                    } ?: mutableListOf()

                                                }
                                                viewModel.mainList.clear()
                                                viewModel.mainList.addAll(list)
                                                adapter.updateAdapter(viewModel.mainList)
                                                viewModel.isLoading = false
                                                binding.itemRcy.post {
                                                    //  LoadingUtils.hide(requireActivity())
                                                }
                                                viewModel.isLastPage =
                                                    viewModel.currentPage >= (response?.total_page
                                                        ?: 1)
                                            }
                                        }

                                        is NetworkResult.Error -> {
                                            LoadingUtils.hide(requireActivity())
                                            LoadingUtils.showErrorDialog(
                                                requireActivity(),
                                                result.message.toString()
                                            )
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
            findNavController().navigate(R.id.userWelcomeFragment)
        }
        handleBackPress()
        binding.layTransaction.setOnClickListener {
            alertView()
        }

    }

    private fun handleBackPress() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
            }
        )

    }

    private fun callingTransactionFilter(){
        lifecycleScope.launch {
            Log.d("TESTING_CALLING_TRANSACTION_FILTER","here inside calling transaction filter")
            LoadingUtils.show(requireActivity())
            viewModel.getUserReceivedTransaction().collect {
                when(it){
                    is NetworkResult.Success ->{
                       LoadingUtils.hide(requireActivity())
                        val response = it.data
                        val list = withContext(Dispatchers.Default) {
                            response?.data?.let { data ->

                                buildHistoryList(data).toMutableList()
                            } ?: mutableListOf()
                        }
                        if (list.isNotEmpty()){
                            binding.itemRcy.visibility = View.VISIBLE
                            binding.layItem.visibility = View.VISIBLE
                            binding.imgNoData.visibility = View.GONE
                            adapter.updateAdapter(list)
                            Choreographer.getInstance().postFrameCallback {
                                LoadingUtils.hide(requireActivity())
                            }

                        }else{
                            binding.imgNoData.visibility = View.VISIBLE
                            binding.itemRcy.visibility = View.GONE
                            binding.layItem.visibility = View.GONE
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hide(requireActivity())
                        LoadingUtils.showErrorDialog(requireActivity(), it.message.toString())
                        viewModel.isLoading = false
                    }
                    else ->{

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun alertView() {

        val anchorView = binding.layTransaction

        anchorView.post {
            val inflater = LayoutInflater.from(requireContext())
            val popupView = inflater.inflate(R.layout.alert_transation, null)
            popupWindow = PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)

            val tvAll = popupView.findViewById<TextView>(R.id.tvAll)

            val tvFrom = popupView.findViewById<TextView>(R.id.tvFrom)

            tvAll.setOnClickListener {
                binding.tvName.text = "All Transactions"
                viewModel.filter = false
                viewModel.isSearching = false
                binding.edSearch.text.clear()
                adapter.updateAdapter(viewModel.mainList)
                popupWindow?.dismiss()
            }

            tvFrom.setOnClickListener {
                binding.tvName.text = "Received"
          //      adapter.filterReceived()
                popupWindow?.dismiss()
                viewModel.filter = true
                viewModel.isSearching = false
                binding.edSearch.text.clear()
                callingTransactionFilter()
            }

            popupWindow?.setBackgroundDrawable(null)
            popupWindow?.isOutsideTouchable = true
            popupWindow?.showAsDropDown(anchorView, 0, 20)
        }

    }

    private fun callingTransactionHistoryApi() {
        if (!isOnline(requireContext())) {
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }
        lifecycleScope.launch {
            LoadingUtils.show(requireActivity())
            viewModel.getTransactionHistory().collect { result ->
                LoadingUtils.hide(requireActivity())
                when (result) {
                    is NetworkResult.Success -> {
                        val response = result.data
                        val list = withContext(Dispatchers.Default) {
                            response?.data?.let { data ->
                                buildHistoryList(data).toMutableList()
                            } ?: mutableListOf()
                        }
                        viewModel.mainList.clear()
                        viewModel.mainList = list

                        if (list.isNotEmpty()){
                            binding.itemRcy.visibility = View.VISIBLE
                            binding.layItem.visibility = View.VISIBLE
                            binding.imgNoData.visibility = View.GONE
                            adapter.updateAdapter(list)
                            Choreographer.getInstance().postFrameCallback {
                                LoadingUtils.hide(requireActivity())
                            }
                            viewModel.isLastPage = viewModel.currentPage >= (response?.total_page ?: 1)
                        }else{
                            binding.imgNoData.visibility = View.VISIBLE
                            binding.itemRcy.visibility = View.GONE
                            binding.layItem.visibility = View.GONE
                        }
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

            historyList.add(HistoryItem.Header(monthYear))

            list.forEach { item ->
                Log.d("TESTING_CATEGORY",item.transaction_category)

                historyList.add(
                    HistoryItem.Transaction(
                        title = "${item.user.first_name} ${item.user.last_name}",
                        phone = item.user.phone,
                        date = "${item.date} ${item.time}",
                        amount = if (item.transaction_type.equals(
                                "debit",
                                true
                            )
                        ) -1 * item.amount.toDouble() else item.amount.toDouble(),
                        profile = item.user.business_logo,
                        id = item.user.id.toString(),
                        item.currency,
                        rebalance = if (item.transaction_mode == null) "normal"
                        else
                            if (item.transaction_mode.equals("rebalancing"))
                                "rebalancing"
                            else
                                item.transaction_mode
                        ,
                        transaction_category = item.transaction_category
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
        } else {
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

    fun mergeHistoryList(
        existingHistory: MutableList<HistoryItem>,
        newTransactions: List<TransactionItem>
    ): MutableList<HistoryItem> {

        val allTransactions = mutableListOf<HistoryItem.Transaction>()


        existingHistory.forEach {
            if (it is HistoryItem.Transaction) allTransactions.add(it)
        }


        val convertedNew = newTransactions.map {
            item ->
            Log.d("TESTING_CATEGORY",item.transaction_category)

            HistoryItem.Transaction(
                title = "${item.user.first_name} ${item.user.last_name}",
                phone = item.user.phone,
                date = "${item.date} ${item.time}", // can be Today HH:mm or dd MMM yyyy
                amount = if (item.transaction_type.equals("debit", true)) -item.amount.toDouble() else item.amount.toDouble(),
                profile = item.user.business_logo,
                id = item.user.id.toString(),
                currency = item.currency,
                rebalance = if (item.transaction_mode == null)
                    "normal"
                else
                    if (item.transaction_mode.equals("rebalancing",true))
                        "rebalancing"
                    else
                        item.transaction_mode,
                transaction_category = item.transaction_category
            )
        }

        allTransactions.addAll(convertedNew)


        val sortedTransactions = allTransactions.sortedByDescending {
            parseTransactionTime(it.date)
        }


        val result = mutableListOf<HistoryItem>()
        var currentHeader: String? = null

        sortedTransactions.forEach { txn ->
            val parts = txn.date.split(" ")
            val monthYear = if (parts[0].equals("Today", true)) {
                val cal = Calendar.getInstance()
                val month = SimpleDateFormat("MMM", Locale.ENGLISH).format(cal.time)
                val year = cal.get(Calendar.YEAR)
                "$month $year"
            } else {
                "${parts[1]} ${parts[2]}"
            }

            if (currentHeader != monthYear) {
                currentHeader = monthYear
                result.add(HistoryItem.Header(monthYear))
            }

            result.add(txn)
        }

        return result
    }


    fun parseTransactionTime(dateStr: String): Long {
        return try {
            if (dateStr.startsWith("Today", true)) {
                // Example: "Today 04:46 PM"
                val timePart = dateStr.replace("Today", "").trim()
                val timeFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                val time = timeFormat.parse(timePart)

                val cal = Calendar.getInstance()
                cal.time = time!!
                val today = Calendar.getInstance()
                today.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY))
                today.set(Calendar.MINUTE, cal.get(Calendar.MINUTE))
                today.set(Calendar.SECOND, 0)
                today.timeInMillis
            } else {
                val formatter = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH)
                formatter.parse(dateStr)?.time ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }


}
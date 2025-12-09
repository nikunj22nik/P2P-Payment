package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.adapter.AdapterRecentPeople
import com.p2p.application.adapter.AdapterUserPeople
import com.p2p.application.databinding.FragmentNewNumberBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.Receiver
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.model.recentpepole.RecentPeople
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.NumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewNumberFragment : Fragment(),ItemClickListener, ItemClickListenerType {

    private lateinit var binding: FragmentNewNumberBinding
    private lateinit var viewModel : NumberViewModel
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var popupWindow: PopupWindow?=null
    private lateinit var adapter: AdapterCountry
    private lateinit var adapterPeople: AdapterRecentPeople
    private lateinit var adapterUserPeople: AdapterUserPeople
    private var countryList: MutableList<Country> = mutableListOf()
    private var peopleList: MutableList<RecentPeople> = mutableListOf()
    private var userList: MutableList<com.p2p.application.model.newnumber.Data> = mutableListOf()
    private lateinit var textListener: TextWatcher
    private var textChangedJob: Job? = null
    private var searchFor = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewNumberBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[NumberViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        adapterPeople = AdapterRecentPeople(requireContext(),peopleList,this)
        adapterUserPeople = AdapterUserPeople(requireContext(),userList,this)
        binding.rcyRecentPeople.adapter = adapterPeople
        binding.rcyPeople.adapter = adapterUserPeople
        selectedType = sessionManager.getLoginType().orEmpty()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        loadRecentPeople()

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imgScan.setOnClickListener {
            findNavController().navigate(R.id.QRFragment)
        }

        binding.layTransaction.setOnClickListener {
            findNavController().navigate(R.id.transactionFragment)
        }

        binding.layCountry.setOnClickListener {
            if (isOnline(requireContext())){
                if (countryList.isNotEmpty()){
                    showCountry()
                }else{
                    countryListApi()
                }
            }else{
                LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            }

        }

        textListener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s?.toString() ?: ""
                if (searchText.isNotEmpty()){
                    if (searchText != searchFor) {
                        binding.layRecentPeople.visibility = View.GONE
                        binding.layPeople.visibility = View.GONE
                        binding.layInfo.visibility = View.VISIBLE
                        binding.layLoader.visibility = View.VISIBLE
                        searchFor = searchText
                        textChangedJob?.cancel()
                        // Only start debounce if text length >= 8
                        if (searchText.length >= 8) {
                            textChangedJob = lifecycleScope.launch {
                                delay(1000) // Debounce
                                if (searchText == searchFor) {
                                    if (isOnline(requireContext())) {
                                        searchNumber()
                                    } else {
                                        binding.layLoader.visibility = View.GONE
                                        LoadingUtils.showErrorDialog(
                                            requireContext(),
                                            MessageError.NETWORK_ERROR
                                        )
                                    }
                                }
                            }
                        }
                        else{
                            binding.layLoader.visibility = View.GONE
                           // LoadingUtils.hide(requireActivity())
                        }
                    }else{
                        binding.layRecentPeople.visibility = View.VISIBLE
                        binding.layPeople.visibility = View.GONE
                        binding.layInfo.visibility = View.GONE
                        binding.layLoader.visibility = View.GONE
                    }
                }else{

                    if(peopleList.size >0 )binding.layRecentPeople.visibility = View.VISIBLE else binding.layRecentPeople.visibility =View.GONE
                    binding.layPeople.visibility = View.GONE
                    binding.layInfo.visibility = View.GONE
                    binding.layLoader.visibility = View.GONE
                }

            }
        }

    }
    private fun searchNumber(){
        val type =AppConstant.mapperType( SessionManager(requireContext()).getLoginType())
        val countryCode  = binding.tvCountryCode.text.replace("[()]".toRegex(), "")
        lifecycleScope.launch {
            viewModel.searchNewNumberRequest(binding.edUser.text.toString(),countryCode,type).collect {
                binding.layLoader.visibility = View.GONE
                when(it){
                    is NetworkResult.Success ->{
                        userList.clear()
                        val data = it.data?.data
                        data?.let { list->
                            userList.addAll(list)
                        }
                        if (userList.isNotEmpty()){
                            adapterUserPeople.updateData(userList)
                            binding.layRecentPeople.visibility = View.GONE
                            binding.layInfo.visibility = View.GONE
                            binding.layPeople.visibility = View.VISIBLE
                        }else{
                            binding.layInfo.visibility = View.VISIBLE
                            binding.layRecentPeople.visibility = View.GONE
                            binding.layPeople.visibility = View.GONE
                        }
                    }
                    is NetworkResult.Error ->{
                        userList.clear()
                        binding.layInfo.visibility = View.VISIBLE
                        binding.layRecentPeople.visibility = View.GONE
                        binding.layPeople.visibility = View.GONE
                    }
                    is NetworkResult.Loading -> {
                        // optional: loading indicator dismayed
                    }
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun loadRecentPeople(){
        if (isOnline(requireContext())){
            show(requireActivity())
            lifecycleScope.launch {
                viewModel.recentPeopleRequest().collect { result ->
                    hide(requireActivity())
                    when (result) {
                        is NetworkResult.Success -> {
                            peopleList.clear()
                            result.data?.data?.recent_people?.let {
                                peopleList.addAll(it)
                            }
                            if (peopleList.isNotEmpty()){
                                adapterPeople.updateData(peopleList)
                                binding.layRecentPeople.visibility = View.VISIBLE
                            }else{
                                binding.layRecentPeople.visibility = View.GONE
                            }
                            binding.tvBalance.text = (result.data?.data?.wallet?.balance?:"")+" "+(result.data?.data?.wallet?.currency?:"")
                        }
                        is NetworkResult.Error -> {
                            binding.layRecentPeople.visibility = View.GONE
                        }
                        is NetworkResult.Loading -> {
                            // optional: loading indicator dismayed
                        }
                    }
                }
            }
        }else{
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
        }
    }

    private fun countryListApi() {
        show(requireActivity())
        lifecycleScope.launch {
            viewModel.countryRequest().collect { result ->
                hide(requireActivity())
                when (result) {
                    is NetworkResult.Success -> {
                        val countries = result.data?.data?.country ?: emptyList()
                        if (countries.isNotEmpty()){
                            countryList.clear()
                            countryList.addAll(countries)
                            showCountry()
                        }
                    }
                    is NetworkResult.Error -> {
                        LoadingUtils.showErrorDialog(requireContext(), result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        // optional: loading indicator dismayed
                    }
                }
            }
        }
    }
    @SuppressLint("InflateParams")
    fun showCountry() {
        val anchorView = binding.layCountry
        anchorView.post {
            val inflater = LayoutInflater.from(requireContext())
            val popupView = inflater.inflate(R.layout.alert_country, null)
            popupWindow =
                PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            val rcyCountry = popupView.findViewById<RecyclerView>(R.id.rcyCountry)
            adapter = AdapterCountry(requireContext(), this,countryList)
            rcyCountry.adapter = adapter
            popupWindow?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            popupWindow?.isOutsideTouchable = true
            popupWindow?.showAsDropDown(anchorView)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(data: String) {
        popupWindow?.dismiss()
        val item = countryList[data.toInt()]
        Glide.with(this)
            .load(BuildConfig.MEDIA_URL+item.icon)
            .into(binding.imgIcon)
        binding.tvCountryCode.text = "("+item.country_code+")"
        if (binding.edUser.text.toString().trim().length >= 8) {
            if (isOnline(requireContext())) {
                searchNumber()
            } else {
                binding.layLoader.visibility = View.GONE
                LoadingUtils.showErrorDialog(
                    requireContext(),
                    MessageError.NETWORK_ERROR
                )
            }
        }

    }

    override fun onItemClick(data: String, type: String) {
        val receiver =  if (type.equals("recentPeople",true)){
            val recentPeople = peopleList.find { it.id == data.toInt() }
            Receiver(((recentPeople?.first_name?:"")+" " + (recentPeople?.last_name?:"")),recentPeople?.id?:0,recentPeople?.phone,recentPeople?.role)
        }else{
            val userPeople = userList.find { it.id == data.toInt() }
            Receiver(((userPeople?.first_name?:"")+" " + (userPeople?.last_name?:"")),userPeople?.id?:0,userPeople?.phone,userPeople?.role)
        }
        val json = Gson().toJson(receiver)
        val bundle = Bundle()
        bundle.putString("receiver_json", json)
        bundle.putString("backType", "Number")
        bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.QR)
        findNavController().navigate(R.id.sendMoneyFragment, bundle)
    }

    override fun onResume() {
        super.onResume()
        binding.edUser.text.clear()
        binding.edUser.addTextChangedListener(textListener)
    }

    override fun onPause() {
        binding.edUser.removeTextChangedListener(textListener)
        super.onPause()
    }

}
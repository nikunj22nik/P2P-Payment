package com.p2p.application.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.adapter.AdapterRecentPeople
import com.p2p.application.databinding.FragmentNewNumberBinding
import com.p2p.application.databinding.FragmentUserWelcomeBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.model.recentpepole.Data
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.NumberViewModel
import com.p2p.application.viewModel.SecretCodeViewModel
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
    private var countryList: MutableList<Country> = mutableListOf()
    private var peopleList: MutableList<Data> = mutableListOf()
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
        binding.rcyPeople.adapter = adapterPeople
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
                if (searchText != searchFor) {
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
                                    LoadingUtils.showErrorDialog(
                                        requireContext(),
                                        MessageError.NETWORK_ERROR
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private fun searchNumber(){
        val type =AppConstant.mapperType( SessionManager(requireContext()).getLoginType())
        val countryCode  = binding.tvCountryCode.text.replace("[()]".toRegex(), "")
        lifecycleScope.launch {
            viewModel.searchNewNumberRequest(binding.edUser.text.toString(),countryCode,type).collect {
                when(it){
                    is NetworkResult.Success ->{

                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        // optional: loading indicator dismayed
                    }
                }
            }
        }
    }
    private fun loadRecentPeople(){
        if (isOnline(requireContext())){
            show(requireActivity())
            lifecycleScope.launch {
                viewModel.recentPeopleRequest().collect { result ->
                    hide(requireActivity())
                    when (result) {
                        is NetworkResult.Success -> {
                            result.data?.data?.let {
                                peopleList.addAll(it)
                            }
                            adapterPeople.updateData(peopleList)
                            if (peopleList.isNotEmpty()){
                                binding.layRecentPeople.visibility = View.VISIBLE
                            }else{
                                binding.layRecentPeople.visibility = View.GONE
                            }
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

    override fun onItemClick(data: String) {
        popupWindow?.dismiss()
        val item = countryList[data.toInt()]
        Glide.with(this)
            .load(BuildConfig.MEDIA_URL+item.icon)
            .into(binding.imgIcon)
        binding.tvCountryCode.text = "("+item.country_code+")"

    }

    override fun onItemClick(data: String, type: String) {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        super.onResume()
        binding.edUser.addTextChangedListener(textListener)
    }

    override fun onPause() {
        binding.edUser.removeTextChangedListener(textListener)
        super.onPause()
    }

}
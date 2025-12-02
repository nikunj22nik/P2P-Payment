package com.p2p.application.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.databinding.FragmentCreateAccountBinding

import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SendOtpRegisterViewModel

import com.p2p.application.listener.ItemClickListener

import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig

import com.p2p.application.di.NetworkResult
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CreateAccountFragment : Fragment(),ItemClickListener {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""

    private lateinit var viewModel : SendOtpRegisterViewModel


    private var popupWindow: PopupWindow?=null
    private lateinit var adapter: AdapterCountry
    private var countryList: MutableList<Country> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        handleBackPress()
       viewModel = ViewModelProvider(this)[SendOtpRegisterViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserRoleView()
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
            btncreate.setOnClickListener {
                createAccountApi()
            }
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

    private fun createAccountApi(){
        if(validation()){
           lifecycleScope.launch {
               val type =AppConstant.mapperType( SessionManager(requireContext()).getLoginType())
               show(requireActivity())
               viewModel.sendOtp(binding.etNumber.text.toString(),type , "+229","registration").collect {
                   hide(requireActivity())
                   when(it){
                       is NetworkResult.Success ->{
                           val otp = it.data
                           val bundle = bundleOf("screenType" to "Registration",
                               "firstName" to binding.etFirstName.text.toString(),
                               "lastName" to binding.etLastName.text.toString(),
                               "phone_number" to binding.etNumber.text.toString(),
                               "otp" to otp
                           )

                           findNavController().navigate(R.id.OTPFragment, bundle)
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
    }

    private fun validation() : Boolean{
        if(binding.etFirstName.text.toString().length <3){
            LoadingUtils.showErrorDialog(requireContext(),"The name must be at least 3 characters long.")
            return false
        }
        else if(binding.etNumber.text.toString().length <=8){
            LoadingUtils.showErrorDialog(requireContext(),"Please Enter a Valid Phone Number")
            return false
        }

        return true
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



    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        )
    }

    private fun setupUserRoleView() {
        val title = when (selectedType) {
            MessageError.USER -> "User Registration"
            MessageError.MERCHANT -> "Merchant Registration"
            MessageError.AGENT -> "Agent Registration"
            MessageError.MASTER_AGENT -> "Master Agent Registration"
            else -> "Login"
        }
        binding.tvText.text = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

    override fun onItemClick(data: String) {
        popupWindow?.dismiss()
        val item = countryList[data.toInt()]
        Glide.with(this)
            .load(BuildConfig.MEDIA_URL+item.icon)
            .into(binding.imgIcon)
        binding.tvCountryCode.text = "("+item.country_code+")"
    }
}
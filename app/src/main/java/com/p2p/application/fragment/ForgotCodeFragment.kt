package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.databinding.FragmentForgotCodeBinding
import com.p2p.application.databinding.FragmentSettingBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.MessageError
import com.p2p.application.util.MessageError.Companion.NUMBER_VALIDATION
import com.p2p.application.util.MessageError.Companion.PHONE_NUMBER
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SendOtpForgotSecretViewModel
import com.p2p.application.viewModel.SendOtpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotCodeFragment : Fragment() ,ItemClickListener{

    private lateinit var binding: FragmentForgotCodeBinding
    private lateinit var sessionManager: SessionManager
    private var screenType: String=""
    private lateinit var viewModel: SendOtpForgotSecretViewModel
    private var countryList: MutableList<Country> = mutableListOf()
    private var popupWindow: PopupWindow?=null
    private lateinit var adapter: AdapterCountry

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotCodeBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        screenType=arguments?.getString("screenType","")?:""
        viewModel = ViewModelProvider(requireActivity())[SendOtpForgotSecretViewModel::class.java]




        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (screenType.equals("settingCode",true)) {
            binding.header.text="Secret Code"
        }else{
            binding.header.text="Forgot Your Code"
        }

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
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

        binding.btnSend.setOnClickListener {
            if (isOnline(requireContext())){
                if (validation()){
                    sendOtp()
                }
            }else{
                LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            }
        }
    }

    private fun sendOtp(){
        lifecycleScope.launch {
            val type =AppConstant.mapperType( SessionManager(requireContext()).getLoginType())
            val countryCode  = binding.tvCountryCode.text.replace("[()]".toRegex(), "")
            show(requireActivity())
            viewModel.sendSecretCodeRequest( countryCode,binding.edPhone.text.toString() , type).collect {
                hide(requireActivity())
                when(it){
                    is NetworkResult.Success ->{
                        val otp = it.data
                        val bundle = bundleOf("screenType" to screenType,
                            "phone_number" to binding.edPhone.text.toString(),
                            "otp" to otp,
                            "country_code" to countryCode
                        )
                        findNavController().navigate(R.id.forgotCodeOtpVerifyFragment, bundle)
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

    private fun validation() : Boolean{
        if(binding.edPhone.text.trim().isEmpty()){
            LoadingUtils.showErrorDialog(requireContext(), PHONE_NUMBER)
            return false
        }else if(binding.edPhone.text.toString().length <=8){
            LoadingUtils.showErrorDialog(requireContext(),NUMBER_VALIDATION)
            return false
        }
        return true
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


}
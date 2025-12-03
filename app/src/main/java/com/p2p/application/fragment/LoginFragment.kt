package com.p2p.application.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.databinding.FragmentLoginBinding
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SendOtpLoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
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
import com.p2p.application.util.MessageError.Companion.NAME_ERROR
import com.p2p.application.util.MessageError.Companion.NUMBER_VALIDATION
import com.p2p.application.util.MessageError.Companion.PHONE_NUMBER
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(),ItemClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: SendOtpLoginViewModel
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var popupWindow: PopupWindow?=null
    private lateinit var adapter: AdapterCountry
    private var countryList: MutableList<Country> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        viewModel = ViewModelProvider(requireActivity())[SendOtpLoginViewModel::class.java]

        selectedType = sessionManager.getLoginType().orEmpty()
        handleBackPress()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserRoleView()

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.createAccountFragment)
        }

        binding.btnLogin.setOnClickListener {
            if (isOnline(requireContext())){
                if (validation()){
                    loginApi()
                }
            }else{
                LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
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

    private fun loginApi(){
        lifecycleScope.launch {
            val type =AppConstant.mapperType( SessionManager(requireContext()).getLoginType())
            val countryCode  = /*binding.tvCountryCode.text.replace("[()]".toRegex(), "")*/"+91"
            show(requireActivity())
            viewModel.sendOtp(binding.edPhone.text.toString(),type , countryCode,"login").collect {
                hide(requireActivity())
                when(it){
                    is NetworkResult.Success ->{
                        val otp = it.data
                        val bundle = bundleOf("screenType" to "Login",
                            "phone_number" to binding.edPhone.text.toString(),
                            "otp" to otp,
                            "country_code" to countryCode
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

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.accountTypeFragment)
                }
            }
        )
    }

    private fun setupUserRoleView() {
        val title = when (selectedType) {
            MessageError.USER -> "User Log In"
            MessageError.MERCHANT -> "Merchant Log In"
            MessageError.AGENT -> "Agent Log In"
            MessageError.MASTER_AGENT -> "Master Agent Log In"
            else -> "Login"
        }
        binding.tvText.text = title
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


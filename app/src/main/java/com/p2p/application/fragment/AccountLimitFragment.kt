package com.p2p.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentAccountLimitBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.LoadingUtils.Companion.showErrorDialog
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.AccountLimitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AccountLimitFragment : Fragment() {

    private lateinit var binding: FragmentAccountLimitBinding
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private lateinit var viewModel : AccountLimitViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountLimitBinding.inflate(layoutInflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        viewModel = ViewModelProvider(this)[AccountLimitViewModel::class.java]
        handleBackPress()
        loadApi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.imgBack.setOnClickListener {
            findNavController().navigate(R.id.settingFragment)
        }

        binding.btnVerify.setOnClickListener {
            findNavController().navigate(R.id.userIDUploadFragment)
        }

        binding.pullToRefresh.setOnRefreshListener {
            loadApi()
        }

    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.settingFragment)
                }
            }
        )
    }

    private fun loadApi(){
        if (isOnline(requireContext())){
            show(requireActivity())
            lifecycleScope.launch {
                viewModel.accountLimitRequest().collect { result ->
                    hide(requireActivity())
                    binding.pullToRefresh.isRefreshing = false
                    when (result) {
                        is NetworkResult.Success -> {
                            val dataUser=result.data?.data
                            dataUser?.let {
                                binding.layHide.visibility = View.VISIBLE
                                binding.tvMaxBalance.text = (it.monthly_limit?:"0") +" "+ it.currency
                                binding.tvMaxVolume.text = (it.wallet_limit?:"0") +" "+ it.currency
                                if (it.user_kyc_status == 0){
                                    binding.layPassport.visibility = View.VISIBLE
                                    binding.btnVerifyStatus.visibility = View.GONE
                                    binding.btnVerify.isClickable =  true
                                    binding.tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                                    binding.tvBtnName.setTextColor("#FFFFFF".toColorInt())
                                    binding.btnVerify.setBackgroundResource(R.drawable.button_custom)
                                }
                                if (it.user_kyc_status == 1){
                                    binding.layPassport.visibility = View.GONE
                                    binding.btnVerifyStatus.visibility = View.VISIBLE
                                    binding.btnVerify.isClickable =  false
                                    binding.tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                                    binding.tvSubHeader.text="Your ID document has been submitted.\nVerification is in progress."
                                    binding.tvBtnName.setTextColor("#696969".toColorInt())
                                    binding.btnVerify.setBackgroundResource(R.drawable.button_inactive)
                                }
                                if (it.user_kyc_status == 2){
                                    binding.layPassport.visibility = View.GONE
                                    binding.btnVerifyStatus.visibility = View.VISIBLE
                                    binding.btnVerify.isClickable =  false
                                    binding.tvSubHeader.text="Your identity has been verified.\nYour account limit has been increased."
                                    binding.tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.idright, 0)
                                    binding.tvBtnName.setTextColor("#696969".toColorInt())
                                    binding.btnVerify.setBackgroundResource(R.drawable.button_inactive)
                                }
                                if (it.user_kyc_status == 3){
                                    binding.layPassport.visibility = View.GONE
                                    binding.btnVerifyStatus.visibility = View.VISIBLE
                                    binding.tvBtnName.setTextColor("#FFFFFF".toColorInt())
                                    binding.tvBtnName.text = "Try again"
                                    binding.tvSubHeader.text="We weren’t able to confirm your details. Please try uploading your ID again."
                                    binding.tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.idcross, 0)
                                    binding.btnVerify.isClickable =  true
                                    binding.btnVerify.setBackgroundResource(R.drawable.button_custom)
                                }
                            }
                        }
                        is NetworkResult.Error -> {
                            binding.tvMaxBalance.text = "0"
                            binding.tvMaxVolume.text = "0"
                            binding.btnVerify.isClickable =  true
                            showErrorDialog(requireContext(), result.message.toString())
                        }
                        is NetworkResult.Loading -> {
                            // optional: loading indicator dismayed
                        }
                    }
                }
            }
        }else{
            binding.pullToRefresh.isRefreshing = false
            showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
        }

    }

}
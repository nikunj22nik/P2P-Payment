package com.p2p.application.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.activity.MainActivity
import com.p2p.application.databinding.FragmentAccountLimitBinding
import com.p2p.application.databinding.FragmentOTPBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.LoadingUtils.Companion.showErrorDialog
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.AccountLimitViewModel
import com.p2p.application.viewModel.SettingViewModel
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
                                binding.tvMaxBalance.text = (it.monthly_limit?:"0") +" "+ it.currency
                                binding.tvMaxVolume.text = (it.wallet_limit?:"0") +" "+ it.currency
                                if (it.user_kyc_status == 0){
                                    binding.layPassport.visibility = View.VISIBLE
                                }
                                if (it.user_kyc_status == 1){
                                    binding.layPassport.visibility = View.GONE
                                }
                                if (it.user_kyc_status == 2){
                                    binding.layPassport.visibility = View.GONE
                                }
                                if (it.user_kyc_status == 3){
                                    binding.layPassport.visibility = View.GONE
                                }
                            }
                        }
                        is NetworkResult.Error -> {
                            binding.tvMaxBalance.text = "0"
                            binding.tvMaxVolume.text = "0"
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
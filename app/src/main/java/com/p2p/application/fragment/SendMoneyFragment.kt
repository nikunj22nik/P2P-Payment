package com.p2p.application.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.compose.ui.platform.LocalAutofill
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.databinding.FragmentSendMoneyBinding
import com.p2p.application.databinding.FragmentSettingBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.Receiver
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SendMoneyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SendMoneyFragment : Fragment() {

    private lateinit var binding: FragmentSendMoneyBinding
    private lateinit var sessionManager: SessionManager
    private var previousScreenType: String = ""
    private lateinit var viewModel: SendMoneyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendMoneyBinding.inflate(layoutInflater, container, false)
        sessionManager = SessionManager(requireContext())
        viewModel = ViewModelProvider(this)[SendMoneyViewModel::class.java]
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBack.setOnClickListener {
            if (binding.layoutSendMoney.visibility == View.VISIBLE) {
                findNavController().navigate(R.id.userWelcomeFragment)
            }
            else {
                binding.layoutSecretCode.visibility = View.GONE
                binding.layoutSendMoney.visibility = View.VISIBLE
            }
        }
        binding.btnSend.setOnClickListener {
            // findNavController().navigate(R.id.enterSecretCodeFragment)
            if (binding.confirmAmount.length() > 0) {
                binding.layoutSecretCode.visibility = View.VISIBLE
                binding.layoutSendMoney.visibility = View.GONE
            } else {
                LoadingUtils.showErrorDialog(requireContext(), MessageError.INVALID_AMOUNT)
            }
        }


        if (requireArguments().containsKey(AppConstant.SCREEN_TYPE)) {
            previousScreenType = requireArguments().getString(AppConstant.SCREEN_TYPE, "")
        }

        val receiver = getReceiverArg()
        viewModel.receiver = receiver
        settingData(viewModel.receiver)

            callingGetAmountApi(viewModel.receiver)

        callingTextWatcher()
        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        binding.btnForgot.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("screenType", "loginCode")
            findNavController().navigate(R.id.forgotCodeFragment, bundle)
        }
        handleBackPress()

    }

    fun Fragment.getReceiverArg(): Receiver? {
        val json = arguments?.getString("receiver_json") ?: run {
            Log.w("ARG_WARNING", "receiver_json missing")
            return null
        }

        return try {
            Gson().fromJson(json, Receiver::class.java)
        } catch (e: Exception) {
            Log.e("ARG_ERROR", "Failed to parse receiver_json", e)
            null
        }
    }

    private fun callingTextWatcher() {
        binding.amnt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                val number = input.toDoubleOrNull()
                if (number != null) {
                    val result = number * 1.01
                    val finalValue = String.format("%.2f", result).toDouble()
                    if(SessionManager(requireContext()).getLoginType().equals(AppConstant.USER)) {
                        binding.confirmAmount.setText(finalValue.toString())
                    }else{
                        binding.confirmAmount.setText(number.toString())
                    }
                } else {
                    binding.confirmAmount.setText("")
                }
            }
        })
    }

    private fun settingData(receive: Receiver?) {
        receive?.let {
            binding.tvName.text = receive.name
            binding.tvNumber.text = receive.phone
        }
    }

    private fun callingGetAmountApi(receive: Receiver?) {
        receive?.let {
            lifecycleScope.launch {
                LoadingUtils.show(requireActivity())
                viewModel.receiverProfileImage(receive.user_id).collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            LoadingUtils.hide(requireActivity())
                            val data = it.data
                            data?.let {
                                it.receiver_profile_image?.let { img ->
                                    Glide.with(requireContext()).load(BuildConfig.MEDIA_URL + img)
                                        .into(binding.imageProfile)
                                }
                                if(SessionManager(requireContext()).getLoginType().equals(AppConstant.USER)) {
                                   binding.tv1.visibility =View.VISIBLE
                                    binding.l2.visibility =View.VISIBLE
                                    binding.fee.text = it.monthly_limit
                                }
                            }
                        }

                        is NetworkResult.Error -> {
                            LoadingUtils.hide(requireActivity())
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }


    private fun setupOtpFields(vararg fields: EditText) {
        fields.forEachIndexed { index, editText ->
            val next = fields.getOrNull(index + 1)
            val prev = fields.getOrNull(index - 1)

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    when {
                        s?.length == 1 -> {
                            next?.requestFocus()
                            if (index == fields.lastIndex) {
                                val otp = getOtp()
                                if (otp.length == fields.size) {
                                    callingCheckSecretCodeApi(getOtp())
                                }
                            }
                        }

                        s?.isEmpty() == true -> prev?.requestFocus()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun callingCheckSecretCodeApi(code: String) {
        lifecycleScope.launch {
            LoadingUtils.show(requireActivity())
            viewModel.checkSecretCode(code).collect {
                when (it) {
                    is NetworkResult.Success -> {
                        if(it.data == true) {
                            callingPaymentApi()
                        }
                        else{
                            LoadingUtils.hide(requireActivity())
                            LoadingUtils.showErrorDialog(requireContext(), MessageError.INVALID_SECRET)
                        }
                    }
                    is NetworkResult.Error -> {
                        LoadingUtils.hide(requireActivity())
                        LoadingUtils.showErrorDialog(requireContext(), it.message.toString())
                    }
                    else -> { }
                }
            }
        }
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.layoutSendMoney.visibility == View.VISIBLE) {
                        findNavController().navigate(R.id.userWelcomeFragment)
                    }
                    else {
                        binding.layoutSecretCode.visibility = View.GONE
                        binding.layoutSendMoney.visibility = View.VISIBLE
                    }
                }
            }
        )
    }

    private fun callingPaymentApi() {
        lifecycleScope.launch {
            val loginType = SessionManager(requireContext()).getLoginType()
            val type = AppConstant.mapperType(loginType)

            val receiver = viewModel.receiver
            val amount = binding.amnt.text?.toString()
            val confirmAccount = binding.confirmAmount.text?.toString()
            if (receiver != null && receiver.user_id != null && receiver.user_type != null && !amount.isNullOrBlank()) {
                viewModel.sendMoney(
                    senderType = type,
                    receiver_id = receiver.user_id,
                    receiverType = receiver.user_type,
                    amount = amount,
                    confirmAccount?:""
                ).collect { result ->
                    when (result) {
                        is NetworkResult.Success ->{
                            LoadingUtils.hide(requireActivity())
                            val data = result.data
                            val json = Gson().toJson(result.data)
                            val bundle = Bundle()
                            Log.d("TESTING_T_ID","Transaction id"+data?.transaction_id)
                            data?.id?.let {
                                bundle.putLong("transaction_id", data.id?.toLong()?:0)
                            }
                            bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.QR)
                            findNavController().navigate(R.id.transferStatusFragment, bundle)
                        }
                        is NetworkResult.Error -> {
                            LoadingUtils.hide(requireActivity())
                            LoadingUtils.showErrorDialog(requireContext(),result.message.toString())
                        }
                        else -> {

                        }
                    }
                }

                } else {

                    binding.layoutSendMoney.visibility = View.VISIBLE
                    binding.layoutSecretCode.visibility = View.GONE
                    LoadingUtils.showErrorDialog(requireContext(), MessageError.AMOUNT_NULL)
                }
            }
        }


    private fun getOtp(): String {
        return binding.etOtp1.text.toString() +
                binding.etOtp2.text.toString() +
                binding.etOtp3.text.toString() +
                binding.etOtp4.text.toString()
    }


}
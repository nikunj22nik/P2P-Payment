package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.databinding.FragmentSendMoneyBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.Receiver
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SendMoneyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.view.isVisible
import com.p2p.application.util.EditTextUtils
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.showErrorDialog

@AndroidEntryPoint
class SendMoneyFragment : Fragment() {

    private lateinit var binding: FragmentSendMoneyBinding
    private lateinit var sessionManager: SessionManager
    private var previousScreenType: String = ""
    private var backType: String = "Qr"
    private lateinit var viewModel: SendMoneyViewModel
    private var availableBalance: String = ""
    private var currency :String =""
    private var monthlyLimit :String =""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendMoneyBinding.inflate(layoutInflater, container, false)

        sessionManager = SessionManager(requireContext())

        viewModel = ViewModelProvider(this)[SendMoneyViewModel::class.java]

        backType = arguments?.getString("backType", "Qr") ?: "Qr"

        makeAstrict()

        callingBalanceApi()

        return binding.root
    }

    fun makeAstrict() {
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp1)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp2)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp3)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp4)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBack.setOnClickListener {

            if (binding.layoutSendMoney.isVisible) {
                if (backType.equals("Qr", true)) {
                    findNavController().navigate(R.id.userWelcomeFragment)
                } else {
                    findNavController().navigateUp()
                }
            }
            else {
                binding.layoutSecretCode.visibility = View.GONE
                binding.layoutSendMoney.visibility = View.VISIBLE
                binding.etOtp1.setText("")
                binding.etOtp2.setText("")
                binding.etOtp3.setText("")
                binding.etOtp4.setText("")
            }
        }

        binding.btnSend.setOnClickListener {
            if(availableBalance != null && !availableBalance.isEmpty() && binding.confirmAmount.length() > 0){
                val balance: Double = availableBalance.toDouble()
                val enteredAmount : Double = binding.confirmAmount.text.toString().toDouble()
                if(balance >= enteredAmount){
                    binding.layoutSecretCode.visibility = View.VISIBLE
                    binding.layoutSendMoney.visibility = View.GONE
                }
                else{
                    binding.insufficientTv.visibility = View.VISIBLE
                }

            }
            else if (binding.confirmAmount.length() > 0) {
                binding.layoutSecretCode.visibility = View.VISIBLE
                binding.layoutSendMoney.visibility = View.GONE
            }
            else {
                showErrorDialog(requireContext(), MessageError.INVALID_AMOUNT)
            }

        }

        if (requireArguments().containsKey(AppConstant.SCREEN_TYPE)) {
            previousScreenType = requireArguments().getString(AppConstant.SCREEN_TYPE, "")
        }

        val receiver = getReceiverArg()

        viewModel.receiver = receiver

        if (receiver?.amount != null) {
            binding.layoutSendMoney.visibility = View.GONE
            binding.layoutSecretCode.visibility = View.VISIBLE
            binding.amnt.setText(receiver.amount)
            val number = receiver.amount.toDoubleOrNull()
            if (number != null) {
                val result = number * 1.01
                val finalValue = String.format("%.2f", result).toDouble()
                binding.confirmAmount.setText(finalValue.toString())
            }
            else{
                binding.confirmAmount.setText(receiver.amount)
            }
        }

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

    fun callingBalanceApi() {
        if (!isOnline(requireContext())) {
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }

        lifecycleScope.launch {
            viewModel.getBalance().collect {
                when (it) {
                    is NetworkResult.Success -> {
                        availableBalance = it.data?.first.toString()
                        currency = it.data?.second.toString()
                        binding.fee.text = monthlyLimit+" "+currency
                    }
                    is NetworkResult.Error -> {
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else -> {
                    }
                }
            }

        }
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

            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString().trim()
                val number = input.toDoubleOrNull()
                if (number != null) {
                    val result = number * 1.01
                    val finalValue = String.format("%.2f", result).toDouble()
                    if (SessionManager(requireContext()).getLoginType().equals(AppConstant.USER)) {
                        if(viewModel.receiver?.user_type.equals(AppConstant.USER,true) ){
                            binding.confirmAmount.setText(finalValue.toString())
                        }
                        else{
                            binding.confirmAmount.setText(number.toString())
                        }
                    } else {
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
        if (!isOnline(requireContext())) {
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }
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
                                if (SessionManager(requireContext()).getLoginType()
                                        .equals(AppConstant.USER)
                                ) {
                                    binding.tv1.visibility = View.VISIBLE
                                    binding.l2.visibility = View.VISIBLE
                                    monthlyLimit= it.monthly_limit.toString()
                                    binding.fee.text = it.monthly_limit +" "+currency
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

            // Detect BACKSPACE (DEL key)
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty()) {
                        prev?.requestFocus()
                        prev?.setSelection(prev.text.length)
                    }
                }
                false
            }

            // Detect input change (typing)
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

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }


//    private fun setupOtpFields(vararg fields: EditText) {
//        fields.forEachIndexed { index, editText ->
//            val next = fields.getOrNull(index + 1)
//            val prev = fields.getOrNull(index - 1)
//
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    when {
//                        s?.length == 1 -> {
//                            next?.requestFocus()
//                            if (index == fields.lastIndex) {
//                                val otp = getOtp()
//                                if (otp.length == fields.size) {
//                                    callingCheckSecretCodeApi(getOtp())
//                                }
//                            }
//                        }
//
//                        s?.isEmpty() == true -> prev?.requestFocus()
//                    }
//                }
//
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            })
//        }
//    }

    private fun callingCheckSecretCodeApi(code: String) {
        if (!isOnline(requireContext())) {
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }

        lifecycleScope.launch {
            LoadingUtils.show(requireActivity())
            viewModel.checkSecretCode(code).collect {
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data == true) {
                            callingPaymentApi()
                        } else {
                            LoadingUtils.hide(requireActivity())
                            LoadingUtils.showErrorDialog(
                                requireContext(),
                                MessageError.INVALID_SECRET
                            )
                        }
                    }

                    is NetworkResult.Error -> {
                        LoadingUtils.hide(requireActivity())
                        LoadingUtils.showErrorDialog(requireContext(), it.message.toString())
                    }

                    else -> {}
                }
            }
        }
    }

    private fun handleBackPress() {

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.layoutSendMoney.isVisible) {
                        if (backType.equals("Qr", true)) {
                            findNavController().navigate(R.id.userWelcomeFragment)
                        } else {
                            findNavController().navigateUp()
                        }
                    } else {
                        binding.layoutSecretCode.visibility = View.GONE
                        binding.layoutSendMoney.visibility = View.VISIBLE
                        binding.etOtp1.setText("")
                        binding.etOtp2.setText("")
                        binding.etOtp3.setText("")
                        binding.etOtp4.setText("")
                    }
                }
            }
        )
    }

    private fun callingPaymentApi() {
        if (!isOnline(requireContext())) {
            showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }
        lifecycleScope.launch {
            val loginType = SessionManager(requireContext()).getLoginType()
            val type = AppConstant.mapperType(loginType)
            val receiver = viewModel.receiver
            val amount = binding.amnt.text?.toString()
            val confirmAccount = binding.confirmAmount.text?.toString()
            if (receiver != null && receiver.user_type != null && !amount.isNullOrBlank()) {
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                viewModel.sendMoney(
                    senderType = type,
                    receiver_id = receiver.user_id,
                    receiverType = receiver.user_type,
                    amount = amount,
                    confirmAccount ?: "",
                    currentTime,
                    currentDate
                ).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            LoadingUtils.hide(requireActivity())
                            val data = result.data
                            val json = Gson().toJson(result.data)
                            val bundle = Bundle()
                            Log.d("TESTING_T_ID", "Transaction id" + data?.transaction_id)
                            data?.id?.let {
                                bundle.putLong("transaction_id", data.id.toLong())
                            }
                            bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.QR)
                            findNavController().navigate(R.id.transferStatusFragment, bundle)
                        }

                        is NetworkResult.Error -> {
                            LoadingUtils.hide(requireActivity())
                            showErrorDialog(
                                requireContext(),
                                result.message.toString()
                            )
                        }

                        else -> {

                        }
                    }
                }
            } else {
                binding.layoutSendMoney.visibility = View.VISIBLE
                binding.layoutSecretCode.visibility = View.GONE
                showErrorDialog(requireContext(), MessageError.AMOUNT_NULL)
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
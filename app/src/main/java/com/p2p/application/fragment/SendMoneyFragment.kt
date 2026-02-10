package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.p2p.application.util.LoadingUtils.Companion.addThousandSeparator
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
    var loginType :String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendMoneyBinding.inflate(layoutInflater, container, false)
        sessionManager = SessionManager(requireContext())
        viewModel = ViewModelProvider(this)[SendMoneyViewModel::class.java]
        backType = arguments?.getString("backType", "Qr") ?: "Qr"

        callingBalanceApi()



        settingUpWithDrawDeposit()

        binding.etAmount.addThousandSeparator()
        binding.etConfirm.addThousandSeparator()
        binding.amnt.addThousandSeparator()
        binding.confirmAmount.addThousandSeparator()
         loginType  = SessionManager(requireContext()).getLoginType().toString()

        if(loginType.equals("Agent",true) || loginType.equals("Master Agent",true)) {
            Log.d("TESTING_MONEY","INSIDE LOGIN TYPE"+ loginType)

            binding.confirmAmount.apply {
                isEnabled = true
                isFocusable = true
                isFocusableInTouchMode = true
                isCursorVisible = true
            }
        }


        return binding.root
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


        }

        binding.btnSend.setOnClickListener {

            if(availableBalance != null && !availableBalance.isEmpty() && binding.confirmAmount.length() > 0){
                val balance: Double = LoadingUtils.getPlainNumber(availableBalance).toDouble()
                val enteredAmount : Double = LoadingUtils.getPlainNumber(binding.confirmAmount.text.toString()).toDouble()
                if(balance >= enteredAmount){
//                    binding.layoutSecretCode.visibility = View.VISIBLE
//                    binding.layoutSendMoney.visibility = View.GONE
                    callingPaymentApi()
                }
                else{
                    binding.insufficientTv.visibility = View.VISIBLE
                }
            }
            else if (binding.confirmAmount.length() > 0) {
//                binding.layoutSecretCode.visibility = View.VISIBLE
//                binding.layoutSendMoney.visibility = View.GONE
                callingPaymentApi()
            } else {
                showErrorDialog(requireContext(), MessageError.INVALID_AMOUNT)
            }
        }

        if (requireArguments().containsKey(AppConstant.SCREEN_TYPE)) {
            previousScreenType = requireArguments().getString(AppConstant.SCREEN_TYPE, "")
        }

        val receiver = getReceiverArg()

        viewModel.receiver = receiver

        val loggedInUserType = SessionManager(requireContext()).getLoginType()
        Log.d("TESTING_USER","Logged in user is"+loggedInUserType)

        if(loggedInUserType.equals(MessageError.MASTER_AGENT,true) || loggedInUserType.equals(MessageError.AGENT,true)){
            Log.d("TESTING_USER","Logged in user is"+receiver?.user_type)

            if(receiver?.user_type.equals(MessageError.USER,true) || receiver?.user_type.equals(MessageError.MERCHANT,true)){
                   binding.transferTypeContainer.visibility = View.VISIBLE
                   binding.layoutSendMoney.visibility= View.GONE
               }
        }

        if (receiver?.amount != null) {

            binding.layoutSendMoney.visibility = View.GONE

            binding.amnt.setText(receiver.amount)

            val number = receiver.amount.toDoubleOrNull()

            if (number != null) {
                var result = number * 1.01
                if(receiver?.user_type.equals(AppConstant.MERCHANT,true)){
                    result = number * 1
                }

                val finalValue = LoadingUtils.getPlainNumber(String.format("%.2f", result)).toDouble()

                val finalIntValue = AppConstant.roundHalfUp(finalValue)
                binding.confirmAmount.setText(finalIntValue.toString())

            }

            else{
                binding.confirmAmount.setText(AppConstant.roundHalfUpStr(receiver.amount))
            }

        }

        settingData(viewModel.receiver)
        callingGetAmountApi(viewModel.receiver)
        callingTextWatcher()
        handleBackPress()
    }

    private fun settingUpWithDrawDeposit(){

        val btnDepositLayout = binding.layoutDeposit
        val btnWithdrawLayout = binding.layoutWithdrawal
        val btnSubmit = binding.btnSubmit
        val lblAmount = binding.lblAmount

        val tvDepositIcon = binding.tvDepositIcon
        val tvDepositText = binding.tvDepositText
        val tvWithdrawIcon = binding.tvWithdrawIcon
        val tvWithdrawText = binding.tvWithdrawText

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.userWelcomeFragment)
        }

        btnDepositLayout.setOnClickListener {

            btnDepositLayout.setBackgroundResource(R.drawable.bg_purple_rounded)
            tvDepositIcon.setTextColor(Color.WHITE)
            tvDepositText.setTextColor(Color.WHITE)

            // Deselect Withdrawal
            btnWithdrawLayout.setBackgroundResource(R.drawable.bg_orange_outline)
            tvWithdrawIcon.setTextColor(Color.parseColor("#E67E22"))
            tvWithdrawText.setTextColor(Color.parseColor("#E67E22"))

            // Update Submit Button
            btnSubmit.text = "Deposit"
            btnSubmit.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#B03E7E"))
            lblAmount.text = "Amount deposit"
        }

        btnWithdrawLayout.setOnClickListener {
            // Select Withdrawal
            btnWithdrawLayout.setBackgroundResource(R.drawable.bg_orange_rounded)
            tvWithdrawIcon.setTextColor(Color.WHITE)
            tvWithdrawText.setTextColor(Color.WHITE)

            // Deselect Deposit
            btnDepositLayout.setBackgroundResource(R.drawable.bg_purple_outline)
            tvDepositIcon.setTextColor(Color.parseColor("#B03E7E"))
            tvDepositText.setTextColor(Color.parseColor("#B03E7E"))

            // Update Submit Button
            btnSubmit.text = "Withdrawal"
            btnSubmit.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E67E22"))
            lblAmount.text = "Amount withdrawal"
        }
        btnSubmit.setOnClickListener {
            if(btnSubmit.text.equals("Deposit")){
                callingPaymentApiAgentMasterAgent()
            }else{
                withDrawPaymentApi()
            }
        }


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
                val input = LoadingUtils.getPlainNumber(s.toString().trim())
                val number = input.toDoubleOrNull()
                if (number != null) {
                    val result = number * 1.01
                    val finalValue = LoadingUtils.getPlainNumber(String.format("%.2f", result)).toDouble()
                    if (SessionManager(requireContext()).getLoginType().equals(AppConstant.USER)) {
                        if(viewModel.receiver?.user_type.equals(AppConstant.USER,true) ){
                            binding.confirmAmount.setText(AppConstant.roundHalfUp(finalValue).toString())
                        }
                        else{
                            val percentage = 1
                            val result = number * percentage
                            binding.confirmAmount.setText(AppConstant.roundHalfUp(result).toString())
                        }
                    } else {
                     //   binding.confirmAmount.setText(AppConstant.roundHalfUp(number).toString())
                    }
                } else {
                    binding.confirmAmount.setText("")
                }
            }
        })

        binding.etAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val input = p0.toString().trim()
                val number = input.toDoubleOrNull()
                if (number != null) {
                  //  binding.etConfirm.setText(AppConstant.roundHalfUp(number).toString())
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
                                    && viewModel.receiver?.user_type.equals(AppConstant.USER,true)
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
                   LoadingUtils.showErrorDialog(requireContext(), MessageError.INVALID_SECRET)
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

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.userWelcomeFragment)
                    if (binding.layoutSendMoney.isVisible) {
                     //   findNavController().navigateUp()
//
//                        if (backType.equals("Qr", true)) {
//                            findNavController().navigate(R.id.userWelcomeFragment)
//                        } else {
//                            findNavController().navigateUp()
//                        }
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

        val amount = binding.amnt.text?.toString()
        val confirmAccount = binding.confirmAmount.text?.toString()
        val loginType = SessionManager(requireContext()).getLoginType()

        if(loginType.equals("Agent",true) || loginType.equals("Master Agent",true)) {
            if(!amount.equals(confirmAccount)){
                showErrorDialog(requireContext(), "The amount and the confirmation amount should be the same.")
                return
            }
        }

        lifecycleScope.launch {
            val type = AppConstant.mapperType(loginType)
            val receiver = viewModel.receiver

            if (receiver != null && receiver.user_type != null && !amount.isNullOrBlank()) {
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                LoadingUtils.show(requireActivity())
                viewModel.sendMoney(
                    senderType = type,
                    receiver_id = receiver.user_id,
                    receiverType = receiver.user_type,
                    amount = LoadingUtils.getPlainNumber(amount),
                    LoadingUtils.getPlainNumber(confirmAccount?:"0") ?: "",
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
                            showErrorDialog(requireContext(), result.message.toString())
                        }
                        else -> {

                        }
                    }
                }
            } else {
                binding.layoutSendMoney.visibility = View.VISIBLE
            //    binding.layoutSecretCode.visibility = View.GONE

                showErrorDialog(requireContext(), MessageError.AMOUNT_NULL)

            }

        }

    }


    private fun callingPaymentApiAgentMasterAgent() {
        if (!isOnline(requireContext())) {
            showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }

        val amount = binding.etAmount.text?.toString()
        val confirmAccount = binding.etConfirm.text?.toString()
         val loginType = sessionManager.getLoginType()
        Log.d("TESTING_TYPE_USER",loginType.toString())



        if(!confirmAccount.equals(amount)){
            showErrorDialog(requireContext(), "The amount and the confirmation amount should be the same.")

            return
        }

        lifecycleScope.launch {
            val loginType = SessionManager(requireContext()).getLoginType()
            val type = AppConstant.mapperType(loginType)
            val receiver = viewModel.receiver




            if (receiver != null && receiver.user_type != null && !amount.isNullOrBlank()) {
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                LoadingUtils.show(requireActivity())
                viewModel.sendMoney(
                    senderType = type,
                    receiver_id = receiver.user_id,
                    receiverType = receiver.user_type,
                    amount = LoadingUtils.getPlainNumber(amount),
                    LoadingUtils.getPlainNumber(confirmAccount?:"0") ?: "",
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
                            showErrorDialog(requireContext(), result.message.toString())
                        }
                        else -> {

                        }
                    }
                }
            } else {
                binding.layoutSendMoney.visibility = View.VISIBLE
                //    binding.layoutSecretCode.visibility = View.GONE
                showErrorDialog(requireContext(), MessageError.AMOUNT_NULL)
            }
        }
    }

    private fun withDrawPaymentApi(){
        if (!isOnline(requireContext())) {
            showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }
        val amount = binding.etAmount.text?.toString()
        val confirmAccount = binding.etConfirm.text?.toString()

        if(!confirmAccount.equals(amount)){
            showErrorDialog(requireContext(), "The amount and the confirmation amount should be the same.")
            return
        }

        lifecycleScope.launch {
            val loginType = SessionManager(requireContext()).getLoginType()
            Log.d("TESTING_TYPE_USER",loginType.toString())

            val type = AppConstant.mapperType(loginType)
            val receiver = viewModel.receiver


            if (receiver != null && receiver.user_type != null && !amount.isNullOrBlank()) {
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                LoadingUtils.show(requireActivity())
                viewModel.withDraw(
                    senderId = receiver.user_id.toString(),
                    senderType = receiver.user_type,
                    amount = LoadingUtils.getPlainNumber(amount),
                    currentTime,
                    currentDate
                ).collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            LoadingUtils.hide(requireActivity())
                            Toast.makeText(requireActivity(), "Amount withdrawn successfully", Toast.LENGTH_LONG).show();

                            val bundle = Bundle()
                            Log.d("TESTING_T_ID", "Transaction id" + result.data)
                            bundle.putLong("transaction_id", result.data?.toLong() ?: 0)
                            bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.QR)

                            findNavController().navigate(R.id.transferStatusFragment, bundle)

                        }
                        is NetworkResult.Error -> {

                            LoadingUtils.hide(requireActivity())
                            showErrorDialog(requireContext(), result.message.toString())

                        }
                        else -> {

                        }
                    }
                }
            } else {
                binding.layoutSendMoney.visibility = View.VISIBLE
                //    binding.layoutSecretCode.visibility = View.GONE
                showErrorDialog(requireContext(), MessageError.AMOUNT_NULL)
            }
        }

    }


}
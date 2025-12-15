package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.p2p.application.R
import com.p2p.application.databinding.FragmentOTPBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.LoginModel
import com.p2p.application.util.AppConstant
import com.p2p.application.util.EditTextUtils
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.OtpViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding
    private var screenType: String = ""
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var firstName :String=""
    private var lastName :String=""
    private var phoneNumber :String=""
    private var countryCode :String =""
    private var otp :String =""
    private val startTimeInMillis: Long = 60000
    private var mTimeLeftInMillis = startTimeInMillis
    private var countDownTimer: CountDownTimer? = null
    private var fcmToken: String = ""
    private lateinit var viewModel : OtpViewModel
    private var showDialogToHome :Boolean  = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPBinding.inflate(layoutInflater, container, false)
        screenType = arguments?.getString("screenType") ?: ""
        startTime()
        sessionManager = SessionManager(requireContext())
        viewModel = ViewModelProvider(this)[OtpViewModel::class.java]
        selectedType = sessionManager.getLoginType().orEmpty()
        viewModel.screenType = screenType
         extractingParameter()
        callingResendTask()
       // makeAstrict()
        return binding.root
    }

    fun callingResendTask(){
        binding.btnRegister.setOnClickListener {
            otp =""
            viewModel.otp =""
            callingResendOtp()

        }
    }

    fun makeAstrict(){
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp1)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp2)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp3)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp4)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)
        binding.btnVerify.setOnClickListener {
            if (getOtp().equals( viewModel.otp,true)) {
                if (viewModel.screenType.equals("Registration", true))  callingCreateAccount()
                else callingLoginApi()
            } else {
                LoadingUtils.showErrorDialog(requireContext(), MessageError.OTP_NOT_MATCH)
            }
        }
    }

    private fun callingResendOtp(){
        lifecycleScope.launch {
            val type = AppConstant.mapperType(SessionManager(requireContext()).getLoginType())
            val screenType = if(viewModel.screenType.equals("Registration")) "registration" else "login"
            LoadingUtils.show(requireActivity())
            viewModel.
            resendOtp( viewModel.phoneNumber,type, viewModel.countryCode,screenType).
            collect {
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hide(requireActivity())
                        startTime()
                      val currentOtp = it.data
                        if (currentOtp != null) {
                            otp= currentOtp
                            viewModel.otp = currentOtp
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hide(requireActivity())
                        LoadingUtils.showErrorDialog(requireActivity(),it.message.toString())
                    }
                    else->{
                    }
                }
            }
        }
    }

    private fun extractingParameter(){
        if(viewModel.screenType.equals("Registration",true)){
            countryCode = arguments?.getString("country_code")?:""
            firstName = arguments?.getString("firstName")?:""
            lastName = arguments?.getString("lastName")?:""
            phoneNumber = arguments?.getString("phone_number")?:""
            otp = arguments?.getString("otp")?:""
            viewModel.countryCode =countryCode
            viewModel.firstName = firstName
            viewModel.lastName = lastName
            viewModel.otp = otp
            viewModel.phoneNumber = phoneNumber
        }else{
            countryCode = arguments?.getString("country_code")?:""
            phoneNumber = arguments?.getString("phone_number")?:""
            otp = arguments?.getString("otp")?:""
            viewModel.countryCode =countryCode
            viewModel.otp = otp
            viewModel.phoneNumber = phoneNumber
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAlertDialog(header:String, subheader:String,
                                content:String,
                                buttonContent:String,
                                iconRes: Int) {
        val dialog = context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.account_create_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = layoutParams
        val btnContinue: LinearLayout = dialog.findViewById(R.id.btnContinue)
        val tvSubHeader: TextView = dialog.findViewById(R.id.tvSubHeader)

        val tvContent: TextView = dialog.findViewById<TextView>(R.id.tvContent)
        val tvHeader: TextView = dialog.findViewById<TextView>(R.id.tvHeader)
        val btnTv: TextView = dialog.findViewById<TextView>(R.id.tvBtn)
        val logo: ImageView = dialog.findViewById<ImageView>(R.id.logo)
        logo.setImageDrawable(null)
        logo.setImageResource(iconRes)
        tvContent.text = content
        tvHeader.text = header
        if (subheader.isEmpty()) tvSubHeader.visibility = View.GONE
        tvContent.text = content
        tvHeader.text = header
        btnTv.text = buttonContent
        tvSubHeader.text = subheader
        btnContinue.setOnClickListener {
            dialog.dismiss()
            if (viewModel.screenType.equals("Registration", true)) {
                if (selectedType.equals(AppConstant.USER,true)) {
                        sessionManager.setIsLogin(true)
                        findNavController().navigate(R.id.secretCodeFragment)
                } else {
                        findNavController().navigate(R.id.loginFragment)
                }
            } else {
                if (buttonContent.equals(AppConstant.BACK_TO_HOME,true)) {
                    Log.d("typeSelect","****"+sessionManager.getScreenType())
//                    if (selectedType.equals(AppConstant.AGENT,true) || selectedType.equals(AppConstant.MASTER_AGENT,true)){
                        if (sessionManager.getIsPin()){
                            findNavController().navigate(R.id.userWelcomeFragment)
                        }else{
                            findNavController().navigate(R.id.secretCodeFragment)
                        }
//                    }else{
//                        findNavController().navigate(R.id.userWelcomeFragment)
//                    }
                } else if (buttonContent.equals(AppConstant.BACK_TO_LOGIN,true)) {
                        findNavController().navigate(R.id.loginFragment)
                } else if (buttonContent.equals(AppConstant.TRY_AGAIN,true)) {
                    if(selectedType.equals(AppConstant.MERCHANT,true)){
                        findNavController().navigate(R.id.merchantVerificationFragment)
                    }else {
                        findNavController().navigate(R.id.createAccountFragment)
                    }
                }
            }
        }
        dialog.show()
    }

    private fun callingCreateAccount(){

        if (isOnline(requireContext())) {
            lifecycleScope.launch {
                val type = AppConstant.mapperType(SessionManager(requireContext()).getLoginType())
                LoadingUtils.show(requireActivity())
                viewModel.register(
                    viewModel.firstName,
                    viewModel.lastName,
                    viewModel.countryCode,
                    viewModel.phoneNumber,
                    viewModel.otp,
                    type,
                    fcmToken
                ).collect {
                    when (it) {
                        is NetworkResult.Success -> {
                            LoadingUtils.hide(requireActivity())
                            val obj = it.data
                            SessionManager(requireContext()).setIsWelcome(true)
                            it.data?.let {
                                if (selectedType.equals(AppConstant.USER,true)) {
                                    SessionManager(requireContext()).setAuthToken(it.token ?: "")
                                    SessionManager(requireContext()).setFirstName(
                                        it.user?.first_name ?: ""
                                    )
                                    SessionManager(requireContext()).setLastName(
                                        it.user?.last_name ?: ""
                                    )
                                    SessionManager(requireContext()).setPhoneNumber(
                                        it.user?.phone ?: ""
                                    )
                                    SessionManager(requireContext()).setIsLogin(true)
                                    showAlertDialog(
                                        "Account Created !",
                                        "",
                                        "Your user account has been created successfully.",
                                        "Continue",
                                        R.drawable.icon_park_outline_check_one
                                    )
                                } else if (selectedType.equals(AppConstant.MERCHANT,true)) {
                                    SessionManager(requireContext()).setAuthToken(it.token ?: "")
                                    findNavController().navigate(R.id.notificationFragment)
                                } else if (selectedType.equals(AppConstant.AGENT,true)) {
                                    showAlertDialog(
                                        "Registration Successful!",
                                        "Please wait while we verify your details.",
                                        "You will be notified once your agent account is approved by Many Mobile Money.",
                                        "Go to Login",
                                        R.drawable.icon_park_outline_check_one
                                    )
                                } else if (selectedType.equals(AppConstant.MASTER_AGENT,true)) {
                                    showAlertDialog(
                                        "Registration Successful!",
                                        "Please wait while we verify your details.",
                                        "You will be notified once your\n" +
                                                "Master Agent account is approved by Many Mobile Money.",
                                        "Go to Login", R.drawable.icon_park_outline_check_one
                                    )
                                }
                            }
                        }
                        is NetworkResult.Error -> {
                            LoadingUtils.hide(requireActivity())
                            LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                        }
                        else -> {

                        }
                    }

                }
            }
        }else{
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
        }
    }

    private fun  callingLoginApi() {
        if (!isOnline(requireContext())) {
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }

        lifecycleScope.launch {
            LoadingUtils.show(requireActivity())

            val loginType = AppConstant.mapperType(SessionManager(requireContext()).getLoginType())

            viewModel.login( viewModel.phoneNumber,  viewModel.otp,  viewModel.countryCode, loginType, fcmToken)
                .collect { result ->
                    when (result) {
                        is NetworkResult.Success -> {
                            LoadingUtils.hide(requireActivity())

                            val response = result.data ?: return@collect
                            val user = response.user

                            when (selectedType) {
                                AppConstant.USER -> handleUserLogin(response)
                                AppConstant.MERCHANT -> {
                                    sessionManager.setAuthToken(response.token?:"")
                                    if(response.user.verification_status ==1){
                                    SessionManager(requireContext()).apply {
                                        setAuthToken(response.token?:"")
                                        setFirstName(user.first_name ?: "")
                                        setLastName(user.last_name ?: "")
                                        setPhoneNumber(user.phone ?: "")
                                        setIsLogin(true)
                                        if (response.user.mpin!=null){
                                            setIsPin(true)
                                        }
                                     }
                                    }
                                    showDialogToHome = response.first_login_status


                                    if(response.user.verification_docs_upload_status ==1 ){
                                         findNavController().navigate(R.id.merchantVerificationFragment)
                                    }else {

                                            handleVerificationStatus(
                                                status = user.verification_status,
                                                role = AppConstant.MERCHANT
                                            )


                                    }
                                }
                                AppConstant.AGENT -> {
                                    if(response.user.verification_status ==1){
                                        SessionManager(requireContext()).apply {
                                            setAuthToken(response.token?:"")
                                            setFirstName(user.first_name ?: "")
                                            setLastName(user.last_name ?: "")
                                            setPhoneNumber(user.phone ?: "")
                                            setIsLogin(true)
                                            if (response.user.mpin!=null){
                                                setIsPin(true)
                                            }
                                        }
                                    }
                                    showDialogToHome = response.first_login_status


                                    handleVerificationStatus(status = user.verification_status, role = AppConstant.AGENT)
                                }
                                AppConstant.MASTER_AGENT -> {
                                    if(response.user.verification_status ==1){
                                        SessionManager(requireContext()).apply {
                                            setAuthToken(response.token?:"")
                                            setFirstName(user.first_name ?: "")
                                            setLastName(user.last_name ?: "")
                                            setPhoneNumber(user.phone ?: "")
                                             setIsLogin(true)
                                            if (response.user.mpin!=null){
                                                setIsPin(true)
                                            }
                                        }
                                    }
                                    showDialogToHome = response.first_login_status


                                    handleVerificationStatus(status = user.verification_status, role = AppConstant.MASTER_AGENT)
                                }
                                else -> {
                                    LoadingUtils.showErrorDialog(requireContext(), "Unknown user type")
                                }
                            }
                        }

                        is NetworkResult.Error -> {
                            LoadingUtils.hide(requireActivity())
                            LoadingUtils.showErrorDialog(requireContext(), result.message.toString())
                        }

                        is NetworkResult.Loading -> {
                            // optionally handle
                        }
                    }
                }
        }
    }

    // helper to persist user and navigate for normal user
    private fun handleUserLogin(response: LoginModel) {
        val user = response.user
        SessionManager(requireContext()).apply {
            setAuthToken(response.token?:"")
            setFirstName(user.first_name ?: "")
            setLastName(user.last_name ?: "")
            setPhoneNumber(user.phone ?: "")
            setIsLogin(true)
            if (response.user.mpin!=null){
                setIsPin(true)
            }
        }
        if (sessionManager.getIsPin()){
            findNavController().navigate(R.id.userWelcomeFragment)
        }else{
            findNavController().navigate(R.id.secretCodeFragment)
        }
    }


    private fun handleVerificationStatus(status: Int, role: String) {
        when (role) {
            AppConstant.MERCHANT -> {
                when (status) {
                    0 -> showAlertDialog(
                        header = "Verification In Progress",
                        subheader = "",
                        content = "Your account is under review, and we’ll get back to you within 24–48 hours once verification is complete.",
                        buttonContent = AppConstant.BACK_TO_LOGIN,
                        iconRes = R.drawable.ic_verfication_merchant
                    )
                    1 -> {

                        if(!showDialogToHome){
                            if (sessionManager.getIsPin()){
                                findNavController().navigate(R.id.userWelcomeFragment)
                            }else{
                                findNavController().navigate(R.id.secretCodeFragment)
                            }
                        }else {
                            showAlertDialog(
                                header = "Documents Approved",
                                subheader = "",
                                content = "Your merchant account has been verified successfully. You can now use all features.",
                                buttonContent = AppConstant.BACK_TO_HOME,
                                iconRes = R.drawable.ic_document_approve
                            )
                        }
                    }
                    2 -> showAlertDialog(
                        header = "Documents Rejected",
                        subheader = "",
                        content = "Verification failed. Please re-upload valid documents.",
                        buttonContent = AppConstant.TRY_AGAIN,
                        iconRes = R.drawable.ic_document_rejected
                    )
                }
            }
            AppConstant.AGENT, AppConstant.MASTER_AGENT -> {
                when (status) {
                    0 -> showAlertDialog(
                        header = "Verification In Progress",
                        subheader = "",
                        content = "Your account is under review, and we’ll get back to you within 24–48 hours once verification is complete.",
                        buttonContent = AppConstant.BACK_TO_LOGIN,
                        iconRes = R.drawable.ic_verification_progress
                    )
                    1 -> {
                        if(!showDialogToHome){
                            if (sessionManager.getIsPin()){
                                findNavController().navigate(R.id.userWelcomeFragment)
                            }else{
                                findNavController().navigate(R.id.secretCodeFragment)
                            }
                        }else {
                            showAlertDialog(
                                header = "Verification Approved",
                                subheader = "",
                                content = "Your agent account has been verified successfully. You can now use all features.",
                                buttonContent = AppConstant.BACK_TO_HOME,
                                iconRes = R.drawable.ic_document_approve
                            )
                        }
                    }
                    2 -> showAlertDialog(
                        header = "Verification Rejected",
                        subheader = "",
                        content = "Verification failed. Please re-upload valid documents.",
                        buttonContent = AppConstant.TRY_AGAIN,
                        iconRes = R.drawable.ic_document_rejected
                    )
                }
            }
            else -> {
                when (status) {
                    0 -> showAlertDialog(
                        header = "Verification In Progress",
                        subheader = "",
                        content = "Your account is under review.",
                        buttonContent = "Back",
                        iconRes = R.drawable.ic_verification_progress
                    )
                    1 -> {
                        if(!showDialogToHome){
                            if (sessionManager.getIsPin()){
                                findNavController().navigate(R.id.userWelcomeFragment)
                            }else{
                                findNavController().navigate(R.id.secretCodeFragment)
                            }
                        }else {
                            showAlertDialog(
                                header = "Verification Approved",
                                subheader = "",
                                content = "Verification completed.",
                                buttonContent = AppConstant.BACK_TO_HOME,
                                iconRes = R.drawable.ic_document_approve
                            )
                        }
                    }
                    2 -> showAlertDialog(
                        header = "Verification Rejected",
                        subheader = "",
                        content = "Verification failed.",
                        buttonContent = AppConstant.TRY_AGAIN,
                        iconRes = R.drawable.ic_document_rejected
                    )
                }
            }
        }
    }



    private fun getOtp(): String {
        return binding.etOtp1.text.toString() +
                binding.etOtp2.text.toString() +
                binding.etOtp3.text.toString() +
                binding.etOtp4.text.toString()
    }

//    private fun setupOtpFields(vararg fields: EditText) {
//
//        fields.forEachIndexed { index, editText ->
//
//            val next = fields.getOrNull(index + 1)
//
//            val prev = fields.getOrNull(index - 1)
//
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    when {
//                        s?.length == 1 -> next?.requestFocus()
//                        s?.isEmpty() == true -> prev?.requestFocus()
//                    }
//                }
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            })
//        }
//    }

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
                    // Move forward on typing 1 digit
                    if (s?.length == 1) {
                        next?.requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun startTime() {
        countDownTimer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished
                binding.showView.visibility = View.GONE
                binding.tvResend.visibility = View.VISIBLE
                updateCountDownText()
            }
            override fun onFinish() {
                mTimeLeftInMillis = 60000
                binding.showView.visibility = View.VISIBLE
                binding.tvResend.visibility = View.GONE
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountDownText() {
        val seconds = mTimeLeftInMillis.toInt() / 1000 % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds)
        binding.tvResend.text = "Resend in $timeLeftFormatted sec"
    }

    private fun fetchToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fcmToken = task.result
                    Log.d("FCM", "FCM Token: ${task.result}")
                } else {
                    fcmToken = "Fetching FCM token failed"
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        fetchToken()
    }

}
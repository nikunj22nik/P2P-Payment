package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.p2p.application.Error.ErrorHandler
import com.p2p.application.R
import com.p2p.application.databinding.FragmentOTPBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPBinding.inflate(layoutInflater, container, false)
        screenType = arguments?.getString("screenType") ?: ""
        sessionManager = SessionManager(requireContext())
        viewModel = ViewModelProvider(this)[OtpViewModel::class.java]
        selectedType = sessionManager.getLoginType().orEmpty()
         extractingParameter()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)
        startTime()
        binding.btnVerify.setOnClickListener {

            if(getOtp().equals(otp,true)){
                callingCreateAccount()
            }else{
                LoadingUtils.showErrorDialog(requireContext(), MessageError.OTP_NOT_MATCH)
            }

//            if (screenType.equals("Registration",true)){
//                showAlertDialog()
//            }
//            if (screenType.equals("Login",true)){
//                sessionManager.setIsLogin(true)
//                findNavController().navigate(R.id.userWelcomeFragment)
//            }
        }
    }

    private fun extractingParameter(){
        if(screenType.equals("Registration",true)){
            countryCode = arguments?.getString("country_code")?:""
            firstName = arguments?.getString("firstName")?:""
            lastName = arguments?.getString("lastName")?:""
            phoneNumber = arguments?.getString("phone_number")?:""
            otp = arguments?.getString("otp")?:""
        }else{
            countryCode = arguments?.getString("country_code")?:""
            phoneNumber = arguments?.getString("phone_number")?:""
            otp = arguments?.getString("otp")?:""
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAlertDialog(header:String, subheader:String,
                                content:String,
                                buttonContent:String){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.account_create_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = layoutParams
        val btnContinue: LinearLayout =dialog.findViewById(R.id.btnContinue)
        val tvSubHeader: TextView =dialog.findViewById(R.id.tvSubHeader)
        val tvContent : TextView = dialog.findViewById(R.id.tvContent)
        val tvHeader : TextView = dialog.findViewById(R.id.tvHeader)
        val btnTv : TextView = dialog.findViewById(R.id.tvBtn)
        val logo : ImageView = dialog.findViewById(R.id.logo)
        tvContent.text =content
        tvHeader.text = header
        btnTv.text = buttonContent
        if (subheader.equals("",true)){
            tvSubHeader.visibility = View.GONE
        }else{
            tvSubHeader.visibility = View.VISIBLE
        }
        tvSubHeader.text= subheader
        btnContinue.setOnClickListener {
            dialog.dismiss()
            if(selectedType.equals(AppConstant.USER,true)){
                sessionManager.setIsLogin(true)
                findNavController().navigate(R.id.secretCodeFragment)
            }else{
                 findNavController().navigate(R.id.loginFragment)
            }
        }
        dialog.show()
    }


    private fun callingCreateAccount(){
        lifecycleScope.launch {
            val type = AppConstant.mapperType(SessionManager(requireContext()).getLoginType())
            LoadingUtils.show(requireActivity())
            viewModel.register(firstName,lastName,countryCode,phoneNumber,otp,type,fcmToken).collect {
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hide(requireActivity())
                        it.data?.let { userData->
                            if(selectedType.equals(AppConstant.USER,true)) {
                                SessionManager(requireContext()).setAuthToken(userData.token ?: "")
                                SessionManager(requireContext()).setFirstName(userData.user?.first_name?:"")
                                SessionManager(requireContext()).setLastName(userData.user?.last_name?:"")
                                SessionManager(requireContext()).setPhoneNumber(userData.user?.phone?:"")
                                showAlertDialog("Account Created !","","Your user account has been created successfully.","Continue")
                            }
                            else if(selectedType.equals(AppConstant.MERCHANT,true)){
                                findNavController().navigate(R.id.notificationFragment)
                            }
                            else if(selectedType.equals(AppConstant.AGENT,true)){
                                showAlertDialog("Registration Successful!","Please wait while we verify your details.",
                                    "You will be notified once your agent account is approved by Many Mobile Money.","Go to Login")
                            }
                            else if(selectedType.equals(AppConstant.MASTER_AGENT,true)){
                                showAlertDialog("Registration Successful!","Please wait while we verify your details.","You will be notified once your\n" +
                                        "Master Agent account is approved by Many Mobile Money.","Go to Login")
                            }
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hide(requireActivity())
                    }
                    else ->{

                    }
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

    private fun setupOtpFields(vararg fields: EditText) {
        fields.forEachIndexed { index, editText ->

            val next = fields.getOrNull(index + 1)
            val prev = fields.getOrNull(index - 1)

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    when {
                        s?.length == 1 -> next?.requestFocus()             // move next
                        s?.isEmpty() == true -> prev?.requestFocus()        // move back
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
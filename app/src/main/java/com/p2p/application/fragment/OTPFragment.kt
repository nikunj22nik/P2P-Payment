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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.p2p.application.R
import com.p2p.application.databinding.FragmentOTPBinding
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import java.util.Locale


class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding
    private var screenType: String = ""
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private val startTimeInMillis: Long = 60000
    private var mTimeLeftInMillis = startTimeInMillis
    private var countDownTimer: CountDownTimer? = null
    private var fcmToken: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOTPBinding.inflate(layoutInflater, container, false)
        screenType = arguments?.getString("screenType") ?: ""
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)
        startTime()
        binding.btnVerify.setOnClickListener {
            if (screenType.equals("Registration",true)){
                showAlertDialog()
            }
            if (screenType.equals("Login",true)){
                sessionManager.setIsLogin(true)
                findNavController().navigate(R.id.userWelcomeFragment)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun showAlertDialog(){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.account_create_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams
        val btnContinue: LinearLayout =dialog.findViewById(R.id.btnContinue)
        val tvSubHeader: TextView =dialog.findViewById(R.id.tvSubHeader)

        tvSubHeader.text="Your ${selectedType.lowercase()} account has been created \nsuccessfully."

        btnContinue.setOnClickListener {
            dialog.dismiss()
            if (selectedType.equals(MessageError.MERCHANT,true)){
                findNavController().navigate(R.id.notificationFragment)
            }else{
                sessionManager.setIsLogin(true)
                findNavController().navigate(R.id.secretCodeFragment)
            }
        }
        dialog.show()
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
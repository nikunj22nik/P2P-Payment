package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentForgotCodeBinding
import com.p2p.application.databinding.FragmentForgotCodeOtpVerifyBinding
import com.p2p.application.util.SessionManager
import java.util.Locale

class ForgotCodeOtpVerifyFragment : Fragment() {

    private lateinit var binding: FragmentForgotCodeOtpVerifyBinding
    private lateinit var sessionManager: SessionManager
    private var screenType: String=""

    private val startTimeInMillis: Long = 60000
    private var mTimeLeftInMillis = startTimeInMillis
    private var countDownTimer: CountDownTimer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotCodeOtpVerifyBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        screenType=arguments?.getString("screenType","")?:""

        startTime()

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


        binding.btnVerify.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("screenType", screenType)
            findNavController().navigate(R.id.editSecretCodeFragment,bundle)
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

}
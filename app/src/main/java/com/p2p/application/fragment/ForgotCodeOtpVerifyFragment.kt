package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentForgotCodeOtpVerifyBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.util.AppConstant
import com.p2p.application.util.EditTextUtils
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SendOtpForgotSecretViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class ForgotCodeOtpVerifyFragment : Fragment() {

    private lateinit var binding: FragmentForgotCodeOtpVerifyBinding
    private lateinit var sessionManager: SessionManager
    private var screenType: String=""
    private val startTimeInMillis: Long = 60000
    private var mTimeLeftInMillis = startTimeInMillis
    private var countDownTimer: CountDownTimer? = null
    private var phoneNumber :String=""
    private var countryCode :String =""
    private var otp :String =""
    private lateinit var viewModel: SendOtpForgotSecretViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotCodeOtpVerifyBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        viewModel = ViewModelProvider(requireActivity())[SendOtpForgotSecretViewModel::class.java]
        extractingParameter()
        makeAstrict()
        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        startTime()
        return binding.root
    }
    private fun extractingParameter(){

        screenType=arguments?.getString("screenType","")?:""
        countryCode = arguments?.getString("country_code")?:""
        phoneNumber = arguments?.getString("phone_number")?:""
        otp = arguments?.getString("otp")?:""

        binding.tvNumber.text = "+($countryCode) $phoneNumber"

    }

    fun makeAstrict(){
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp1)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp2)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp3)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp4)
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
            if (isOnline(requireContext())){
                if (isValidation()){
                    otpVerify()
                }
            }else{
                LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            }
        }
        binding.btnResend.setOnClickListener {
            if (isOnline(requireContext())){
                sendOtp()
            }else{
                LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            }
        }
    }
    private fun sendOtp(){
        lifecycleScope.launch {
            val type =AppConstant.mapperType( SessionManager(requireContext()).getLoginType())
            show(requireActivity())
            viewModel.sendSecretCodeRequest( countryCode,phoneNumber , type).collect {
                hide(requireActivity())
                when(it){
                    is NetworkResult.Success ->{
                        otp = it.data.toString()
                        startTime()
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
    private fun otpVerify(){
        lifecycleScope.launch {
            val type = AppConstant.mapperType(SessionManager(requireContext()).getLoginType())
            show(requireActivity())
            viewModel.otpVerifySecretCodeRequest(countryCode,phoneNumber,otp,type).collect {
                hide(requireActivity())
                when(it){
                    is NetworkResult.Success ->{
                        val bundle = Bundle()
                        bundle.putString("screenType", screenType)
                        findNavController().navigate(R.id.editSecretCodeFragment,bundle)
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else ->{

                    }
                }

            }
        }
    }
    private fun isValidation(): Boolean{
        if(getOtp().isEmpty()){
            LoadingUtils.showErrorDialog(requireContext(), MessageError.SECRET_CODE)
            return false
        }else if(!getOtp().equals(otp,true)){
            LoadingUtils.showErrorDialog(requireContext(), MessageError.CODE_NOT_MATCH)
            return false
        }
        return true
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
    private fun getOtp(): String {
        return binding.etOtp1.text.toString() +
                binding.etOtp2.text.toString() +
                binding.etOtp3.text.toString() +
                binding.etOtp4.text.toString()
    }

}
package com.p2p.application.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.activity.MainActivity
import com.p2p.application.databinding.FragmentOTPBinding
import com.p2p.application.databinding.FragmentSecretCodeBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.util.AppConstant
import com.p2p.application.util.EditTextUtils
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.OtpViewModel
import com.p2p.application.viewModel.SecretCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class  SecretCodeFragment : Fragment() {

    private lateinit var binding: FragmentSecretCodeBinding
    private lateinit var viewModel : SecretCodeViewModel
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecretCodeBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[SecretCodeViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        handleBackPress()
        makeAstrict()
        return binding.root
    }
    fun makeAstrict(){
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp1)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp2)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp3)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp4)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp11)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp22)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp33)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp44)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4,binding.etOtp11)
        setupOtpFields(binding.etOtp11, binding.etOtp22, binding.etOtp33, binding.etOtp44)

        binding.btnOkay.setOnClickListener {
            if (isOnline(requireContext())){
                if (isValidation()){
                    setCode()
                }
            }else{
                LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            }
        }
    }


    private fun setCode(){
        lifecycleScope.launch {
            val type =AppConstant.mapperType( SessionManager(requireContext()).getLoginType())
            show(requireActivity())
            viewModel.setSecretCodeRequest(getOtp(),type).collect {
                hide(requireActivity())
                when(it){
                    is NetworkResult.Success ->{
                        sessionManager.setIsPin(true)
                        showAlert()
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

    private fun isValidation(): Boolean{
        if(getOtp().isEmpty()){
            LoadingUtils.showErrorDialog(requireContext(), MessageError.SECRET_CODE)
            return false
        }else if(getOtpRe().isEmpty()){
            LoadingUtils.showErrorDialog(requireContext(), MessageError.SECRET_CODE)
            return false
        }else if(!getOtp().equals(getOtpRe(),true)){
            LoadingUtils.showErrorDialog(requireContext(), MessageError.CODE_NOT_MATCH)
            return false
        }
        return true
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        )
    }

    fun showAlert(){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.successfuly_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams
        val btnOk: LinearLayout =dialog.findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.notificationFragment)
        }
        dialog.show()
    }

//    private fun setupOtpFields(vararg fields: EditText) {
//        fields.forEachIndexed { index, editText ->
//
//            val next = fields.getOrNull(index + 1)
//            val prev = fields.getOrNull(index - 1)
//
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    when {
//                        s?.length == 1 -> next?.requestFocus()             // move next
//                        s?.isEmpty() == true -> prev?.requestFocus()        // move back
//                    }
//                }
//
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            })
//        }
//    }

//    private fun setupOtpFields(vararg fields: EditText) {
//
//        fields.forEachIndexed { index, editText ->
//
//            val next = fields.getOrNull(index + 1)
//            val prev = fields.getOrNull(index - 1)
//
//            // prevent NEXT from jumping outside OTP
//            editText.imeOptions =
//                if (index == fields.lastIndex) EditorInfo.IME_ACTION_DONE
//                else EditorInfo.IME_ACTION_NEXT
//
//            // Detect BACKSPACE
//            editText.setOnKeyListener { _, keyCode, event ->
//                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
//                    if (editText.text.isEmpty()) {
//                        prev?.requestFocus()
//                        prev?.setSelection(prev.text.length)
//                    }
//                }
//                false
//            }
//
//            // Detect input (move forward)
//            editText.addTextChangedListener(object : TextWatcher {
//                override fun afterTextChanged(s: Editable?) {
//                    if (s?.length == 1) {
//                        next?.requestFocus()
//                    }
//                }
//                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            })
//        }
//    }
//


    private fun setupOtpFields(vararg fields: EditText, nextGroupFirst: EditText? = null) {

        fields.forEachIndexed { index, editText ->

            val next = fields.getOrNull(index + 1)
            val prev = fields.getOrNull(index - 1)

            editText.imeOptions =
                if (index == fields.lastIndex) EditorInfo.IME_ACTION_DONE
                else EditorInfo.IME_ACTION_NEXT

            // Handle backspace navigation
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty()) {
                        prev?.requestFocus()
                        prev?.setSelection(prev.text.length)
                    }
                }
                false
            }

            // Handle typing navigation
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    // Normal next
                    if (s?.length == 1) {

                        // If last box and next group exists → move to next group
                        if (index == fields.lastIndex && nextGroupFirst != null) {
                            nextGroupFirst.requestFocus()
                            return
                        }

                        // otherwise normal next
                        next?.requestFocus()
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

    private fun getOtpRe(): String {
        return binding.etOtp11.text.toString() +
                binding.etOtp22.text.toString() +
                binding.etOtp33.text.toString() +
                binding.etOtp44.text.toString()
    }

}
package com.p2p.application.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentEnterSecretCodeBinding
import com.p2p.application.databinding.FragmentOTPBinding
import com.p2p.application.util.SessionManager


class EnterSecretCodeFragment : Fragment() {


    private lateinit var binding: FragmentEnterSecretCodeBinding
    private var screenType: String = ""
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEnterSecretCodeBinding.inflate(layoutInflater, container, false)
        screenType = arguments?.getString("screenType") ?: ""
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        handleBackPress()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        binding.btnForgot.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("screenType", "loginCode")
            findNavController().navigate(R.id.forgotCodeFragment,bundle)
        }
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }


    private fun getOtp(): String {
        return binding.etOtp1.text.toString() +
                binding.etOtp2.text.toString() +
                binding.etOtp3.text.toString() +
                binding.etOtp4.text.toString()
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

    private fun setupOtpFields(vararg fields: EditText) {
        fields.forEachIndexed { index, editText ->

            val next = fields.getOrNull(index + 1)
            val prev = fields.getOrNull(index - 1)

            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    when {
                        s?.length == 1 -> {
                            next?.requestFocus()

                            // If last digit entered
                            if (index == fields.lastIndex) {
                                val otp = getOtp()

                                if (otp.length == fields.size) {
                                    // Delay 2 seconds then navigate
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        findNavController().navigate(R.id.userWelcomeFragment)
                                    }, 2000)
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


}
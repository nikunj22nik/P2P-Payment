package com.p2p.application.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentEnterSecretCodeBinding
import com.p2p.application.databinding.FragmentEnterSecretLoginBinding
import com.p2p.application.util.EditTextUtils


class EnterSecretLoginFragment : Fragment() {

    private lateinit var binding: FragmentEnterSecretLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEnterSecretLoginBinding.inflate(LayoutInflater.from(requireContext()))

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
        makeAstrict()
    }

    fun makeAstrict(){
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp1)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp2)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp3)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp4)
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.userWelcomeFragment)
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
}
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentCheckSecretCodeBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.util.EditTextUtils
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.MessageError
import com.p2p.application.viewModel.SendMoneyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckSecretCodeFragment : Fragment() {

    private lateinit var binding: FragmentCheckSecretCodeBinding

    private lateinit var viewModel: SendMoneyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[SendMoneyViewModel::class.java]
        binding = FragmentCheckSecretCodeBinding.inflate(LayoutInflater.from(requireContext()))
        binding.btnForgot.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("screenType", "loginCode")
            findNavController().navigate(R.id.forgotCodeFragment, bundle)
        }

        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        makeAstrict()

        return binding.root
    }


    fun makeAstrict() {
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp1)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp2)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp3)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp4)
    }


    private fun setupOtpFields(vararg fields: EditText) {

        fields.forEachIndexed { index, editText ->

            val next = fields.getOrNull(index + 1)
            val prev = fields.getOrNull(index - 1)

            // Handle BACKSPACE
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty()) {
                        prev?.requestFocus()
                        prev?.setSelection(prev.text.length)
                        return@setOnKeyListener true
                    }
                }
                false
            }

            editText.addTextChangedListener(object : TextWatcher {

                override fun onTextChanged(
                    s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Move forward ONLY when user types a digit
                    if (count == 1 && s?.length == 1) {
                        next?.requestFocus()

                        if (index == fields.lastIndex) {
                            val otp = getOtp()
                            if (otp.length == fields.size) {
                                callingCheckSecretCodeApi(otp)
                            }
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }

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
                            findNavController().navigate(R.id.userWelcomeFragment)
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


    private fun getOtp(): String {
        return binding.etOtp1.text.toString() +
                binding.etOtp2.text.toString() +
                binding.etOtp3.text.toString() +
                binding.etOtp4.text.toString()
    }


}
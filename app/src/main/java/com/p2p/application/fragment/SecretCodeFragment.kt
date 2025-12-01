package com.p2p.application.fragment

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.activity.MainActivity
import com.p2p.application.databinding.FragmentOTPBinding
import com.p2p.application.databinding.FragmentSecretCodeBinding


class  SecretCodeFragment : Fragment() {

    private lateinit var binding: FragmentSecretCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSecretCodeBinding.inflate(layoutInflater, container, false)
        handleBackPress()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)
        setupOtpFieldsRe(binding.etOtp11, binding.etOtp22, binding.etOtp33, binding.etOtp44)
        binding.btnOkay.setOnClickListener {
            showAlert()
        }
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
//            findNavController().navigate(R.id.enterSecretCodeFragment)
            findNavController().navigate(R.id.notificationFragment)
        }

        dialog.show()
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

    private fun setupOtpFieldsRe(vararg fields: EditText) {
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

    private fun getOtpRe(): String {
        return binding.etOtp11.text.toString() +
                binding.etOtp22.text.toString() +
                binding.etOtp33.text.toString() +
                binding.etOtp44.text.toString()
    }

}
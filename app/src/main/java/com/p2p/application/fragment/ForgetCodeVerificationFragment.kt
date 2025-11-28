package com.p2p.application.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.p2p.application.R
import com.p2p.application.databinding.FragmentAccountTypeBinding
import com.p2p.application.databinding.FragmentForgetCodeVerificationBinding


class ForgetCodeVerificationFragment : Fragment() {
  private lateinit var binding: FragmentForgetCodeVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentForgetCodeVerificationBinding.inflate(layoutInflater, container, false)

        setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        binding.btnProceed.setOnClickListener {
          getOtp()

        }

        return binding.root
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



}
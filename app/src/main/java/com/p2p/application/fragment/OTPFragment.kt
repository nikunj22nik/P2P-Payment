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
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentAccountTypeBinding
import com.p2p.application.databinding.FragmentCreateAccountBinding
import com.p2p.application.databinding.FragmentOTPBinding
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import kotlin.toString


class OTPFragment : Fragment() {

    private lateinit var binding: FragmentOTPBinding
    private var screenType: String = ""
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""

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



        binding.btnVerify.setOnClickListener {
            if (screenType.equals("Registration",true)){

                    showAlertDialog()

            }
            if (screenType.equals("Login",true)){
                findNavController().navigate(R.id.userWelcomeFragment)
                /*if (selectedType.equals(MessageError.USER,true)){
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
                if (selectedType.equals(MessageError.MERCHANT,true)){
                    findNavController().navigate(R.id.merchantFragment)
                }
                if (selectedType.equals(MessageError.AGENT,true)){
                    findNavController().navigate(R.id.agentWelcomeFragment)
                }
                if (selectedType.equals(MessageError.MASTER_AGENT,true)){
                    findNavController().navigate(R.id.masterAgentFragment)
                }*/

            }
        }
    }

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

        btnContinue.setOnClickListener {
            dialog.dismiss()
            if (selectedType.equals(MessageError.MERCHANT,true)){
                findNavController().navigate(R.id.merchantVerificationFragment)
            }else{
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

}
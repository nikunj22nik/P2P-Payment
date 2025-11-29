package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.activity.MainActivity
import com.p2p.application.databinding.FragmentEditSecretCodeBinding
import com.p2p.application.databinding.FragmentForgotCodeOtpVerifyBinding
import com.p2p.application.util.SessionManager


class EditSecretCodeFragment : Fragment() {

    private lateinit var binding: FragmentEditSecretCodeBinding
    private lateinit var sessionManager: SessionManager
    private var screenType: String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditSecretCodeBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        screenType=arguments?.getString("screenType","")?:""
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
            showAlert()
        }


    }

    @SuppressLint("SetTextI18n")
    private fun showAlert(){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.successfuly_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams

        val tvTitle: TextView =dialog.findViewById(R.id.tvTitle)
        val tvbBn: TextView =dialog.findViewById(R.id.tvbBn)
        val btnOk: LinearLayout =dialog.findViewById(R.id.btnOk)

        tvTitle.text="Updated Successfully"
        tvbBn.text="Back to Settings"

        btnOk.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.settingFragment)
        }

        dialog.show()
    }

}
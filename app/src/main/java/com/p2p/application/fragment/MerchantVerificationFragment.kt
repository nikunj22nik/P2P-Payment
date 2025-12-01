package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.adapter.AdapterMerchantVerification
import com.p2p.application.databinding.FragmentMerchantVerificationBinding
import com.p2p.application.databinding.FragmentSettingBinding
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import org.w3c.dom.Text


class MerchantVerificationFragment : Fragment() {


    private lateinit var binding: FragmentMerchantVerificationBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: AdapterMerchantVerification

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMerchantVerificationBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        adapter= AdapterMerchantVerification(requireContext())
        binding.rcyID.adapter = adapter
        binding.rcyNumber.adapter = adapter
        binding.rcyTaxId.adapter = adapter
        handleBackPress()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVerify.setOnClickListener {
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


    @SuppressLint("SetTextI18n")
    fun showAlert(){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.id_submit_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams
        val btnContinue: LinearLayout =dialog.findViewById(R.id.btnContinue)
        val tvHeader: TextView =dialog.findViewById(R.id.tvHeader)
        val tvSubHeader: TextView =dialog.findViewById(R.id.tvSubHeader)
        val tvBtn: TextView =dialog.findViewById(R.id.tvBtn)
        val logo: ImageView =dialog.findViewById(R.id.logo)
        tvHeader.text="Verification Submitted!"
        tvBtn.text="Back to Login"
        tvSubHeader.text="Your documents have been submitted successfully. We'll review them and notify you within 24-48 hours."
        logo.setBackgroundResource(R.drawable.material_symbols_fact_check_outline_rounded)
        btnContinue.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.loginFragment)
        }
        dialog.show()
    }



}
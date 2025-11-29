package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentForgotCodeBinding
import com.p2p.application.databinding.FragmentSettingBinding
import com.p2p.application.util.SessionManager


class ForgotCodeFragment : Fragment() {

    private lateinit var binding: FragmentForgotCodeBinding
    private lateinit var sessionManager: SessionManager
    private var screenType: String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotCodeBinding.inflate(layoutInflater, container, false)
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

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSend.setOnClickListener {
            val bundle = Bundle();
            bundle.putString("screenType", screenType)
            findNavController().navigate(R.id.forgotCodeOtpVerifyFragment,bundle)
        }

    }


}
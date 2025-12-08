package com.p2p.application.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentDeveloperBinding
import com.p2p.application.databinding.FragmentQRBinding
import com.p2p.application.util.AppConstant
import com.p2p.application.util.SessionManager

class DeveloperFragment : Fragment() {
    private lateinit var binding: FragmentDeveloperBinding
    private var checkStatus: Boolean = false
    private lateinit var sessionManager: SessionManager
    private var selectType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeveloperBinding.inflate(layoutInflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectType = sessionManager.getLoginType() ?: ""
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnOk.setOnClickListener {
            checkStatus=true
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            startActivity(intent)
        }
        binding.btnNo.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkStatus){
            checkStatus =false
            if (sessionManager.getIsLogin()?:false){
                if (selectType.equals(AppConstant.USER,true) || selectType.equals(AppConstant.AGENT,true)) {
                    if (sessionManager.getIsPin()){
                        findNavController().navigate(R.id.userWelcomeFragment)
                    }else{
                        findNavController().navigate(R.id.secretCodeFragment)
                    }
                }else{
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
            }else{
                findNavController().navigate(R.id.accountTypeFragment)
            }
        }
    }

}
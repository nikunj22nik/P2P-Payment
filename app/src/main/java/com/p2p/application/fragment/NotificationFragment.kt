package com.p2p.application.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentNotificationBinding
import com.p2p.application.databinding.FragmentSecretCodeBinding
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager


class NotificationFragment : Fragment() {


    private lateinit var binding: FragmentNotificationBinding
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()

        handleBackPress()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOn.setOnClickListener {
            if (selectedType.equals(MessageError.MERCHANT,true)){
                findNavController().navigate(R.id.merchantVerificationFragment)
            }else{
                findNavController().navigate(R.id.userWelcomeFragment)
            }

        }
        binding.btnOff.setOnClickListener {
            if (selectedType.equals(MessageError.MERCHANT,true)){
                findNavController().navigate(R.id.merchantVerificationFragment)
            }else{
                findNavController().navigate(R.id.userWelcomeFragment)
            }
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

}
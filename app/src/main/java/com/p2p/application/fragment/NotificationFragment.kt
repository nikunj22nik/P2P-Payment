package com.p2p.application.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
            askNotificationPermission()


        }
        binding.btnOff.setOnClickListener {
            if (selectedType.equals(MessageError.MERCHANT,true)){
                findNavController().navigate(R.id.merchantVerificationFragment)
            }else{
                findNavController().navigate(R.id.userWelcomeFragment)
            }
        }

    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                if (selectedType.equals(MessageError.MERCHANT,true)){
                    findNavController().navigate(R.id.merchantVerificationFragment)
                }else{
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
            } else {
                if (selectedType.equals(MessageError.MERCHANT,true)){
                    findNavController().navigate(R.id.merchantVerificationFragment)
                }else{
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
            }
        }

    fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
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
package com.p2p.application.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
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

        startRippleAnimation()

    }


    private fun startRippleAnimation() {
        val circles = listOf(
            binding.circle1,
            binding.circle2,
            binding.circle3
        )

        circles.forEachIndexed { index, circle ->
            circle.scaleX = 0f
            circle.scaleY = 0f
            circle.alpha = 1f

            circle.animate()
                .scaleX(2.5f)
                .scaleY(2.5f)
                .alpha(0f)
                .setStartDelay((index * 400).toLong())
                .setDuration(5000)
                .withEndAction { startRippleAnimation() }
                .start()
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
            else{
                if (selectedType.equals(MessageError.MERCHANT,true)){
                    findNavController().navigate(R.id.merchantVerificationFragment)
                }else{
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
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
package com.p2p.application.fragment

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.p2p.application.R

import com.p2p.application.databinding.FragmentAccountTypeBinding
import com.p2p.application.util.MessageError
import com.p2p.application.util.MessageError.Companion.SELECT_TYPE
import com.p2p.application.util.SessionManager
import androidx.core.graphics.toColorInt
import com.p2p.application.util.AppConstant


class AccountTypeFragment : Fragment() {

    private lateinit var binding: FragmentAccountTypeBinding
    private lateinit var sessionManager: SessionManager
    private var selectType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountTypeBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectType = sessionManager.getLoginType() ?: MessageError.USER
        handleBackPress()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProceed.setOnClickListener {
            if (selectType.isEmpty()) {
                Toast.makeText(requireContext(), SELECT_TYPE, Toast.LENGTH_SHORT).show()
            } else {
                sessionManager.setLoginType(selectType)
                findNavController().navigate(R.id.loginFragment)
            }
        }
        updateSelection(selectType)

        binding.user.setOnClickListener { updateSelection(AppConstant.USER) }
        binding.merchant.setOnClickListener { updateSelection(AppConstant.MERCHANT) }
        binding.agent.setOnClickListener { updateSelection(AppConstant.AGENT) }
        binding.masterAgent.setOnClickListener { updateSelection(AppConstant.MASTER_AGENT) }

        // Restore previous selection if exist
        if (selectType.isNotEmpty()) updateSelection(selectType)

    }

    private fun updateSelection(type: String) {
        selectType = type

        val activeColor = "#B13A7E".toColorInt()
        val inactiveColor = "#FFFFFF".toColorInt()

        // Reset all items to inactive
        listOf(
            Triple(binding.user, binding.imgUser, binding.tvUser),
            Triple(binding.merchant, binding.imgMerchant, binding.tvmerchant), // fixed tvMerchant
            Triple(binding.agent, binding.imgAgent, binding.tvAgent),
            Triple(binding.masterAgent, binding.imgMasterAgent, binding.tvMasterAgent)
        ).forEach { (layout, img, tv) ->
            layout.setBackgroundResource(R.drawable.user_select_inactive)
            img.setColorFilter(inactiveColor)
            tv.setTextColor(inactiveColor)
        }

        // Apply active UI to the selected type
        when (type) {
            MessageError.USER -> updateItem(binding.user, binding.tvUser, binding.imgUser, activeColor)
            MessageError.MERCHANT -> updateItem(binding.merchant, binding.tvmerchant, binding.imgMerchant, activeColor)
            MessageError.AGENT -> updateItem(binding.agent, binding.tvAgent, binding.imgAgent, activeColor)
            MessageError.MASTER_AGENT -> updateItem(binding.masterAgent, binding.tvMasterAgent, binding.imgMasterAgent, activeColor)
        }

    }


    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }


    private fun updateItem(
        layout: View,
        textView: View,
        imageView: android.widget.ImageView,
        color: Int
    ) {
        layout.setBackgroundResource(R.drawable.user_select_active)

        if (textView is android.widget.TextView)
            textView.setTextColor(color)

        imageView.setColorFilter(color)
    }
}
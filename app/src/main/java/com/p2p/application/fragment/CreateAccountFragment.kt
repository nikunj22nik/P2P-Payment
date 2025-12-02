package com.p2p.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentCreateAccountBinding
import com.p2p.application.util.AppConstant
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SendOtpRegisterViewModel

class CreateAccountFragment : Fragment() {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private lateinit var viewModel : SendOtpRegisterViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        handleBackPress()
       viewModel = ViewModelProvider(this)[SendOtpRegisterViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserRoleView()
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
            btncreate.setOnClickListener {
                val bundle = bundleOf("screenType" to "Registration")

                findNavController().navigate(R.id.OTPFragment, bundle)
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

    private fun setupUserRoleView() {
        val title = when (selectedType) {
            AppConstant.USER -> "User Registration"
            AppConstant.MERCHANT -> "Merchant Registration"
            MessageError.AGENT -> "Agent Registration"
            MessageError.MASTER_AGENT -> "Master Agent Registration"
            else -> "Login"
        }
        binding.tvText.text = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }
}
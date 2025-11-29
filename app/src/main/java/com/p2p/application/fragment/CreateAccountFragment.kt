package com.p2p.application.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentCreateAccountBinding
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager

class CreateAccountFragment : Fragment() {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()

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

    private fun setupUserRoleView() {
        val title = when (selectedType) {
            MessageError.USER -> "User Registration"
            MessageError.MERCHANT -> "Merchant Registration"
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
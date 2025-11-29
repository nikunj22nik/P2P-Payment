package com.p2p.application.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentLoginBinding
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserRoleView()

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.createAccountFragment)
        }

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(
                R.id.OTPFragment,
                Bundle().apply { putString("screenType", "Login") }
            )
        }
    }

    private fun setupUserRoleView() {
        val title = when (selectedType) {
            MessageError.USER -> "User Log In"
            MessageError.MERCHANT -> "Merchant Log In"
            MessageError.AGENT -> "Agent Log In"
            MessageError.MASTER_AGENT -> "Master Agent Log In"
            else -> "Login"
        }
        binding.tvText.text = title
    }
}
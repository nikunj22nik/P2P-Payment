package com.p2p.application.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.messaging.FirebaseMessaging
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.databinding.FragmentLoginBinding
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager

class LoginFragment : Fragment(),ItemClickListener {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""
    private var popupWindow: PopupWindow?=null
    private var fcmToken: String = ""

    private lateinit var adapter: AdapterCountry


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        handleBackPress()

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

        binding.layCountry.setOnClickListener {
            showCountry()
        }

    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.accountTypeFragment)
                }
            }
        )
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


    private fun fetchToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fcmToken = task.result
                    Log.d("FCM", "FCM Token: ${task.result}")
                } else {
                    fcmToken = "Fetching FCM token failed"
                    Log.e("FCM", "Fetching FCM token failed", task.exception)
                }
            }
    }

    fun showCountry() {
        val anchorView = binding.layCountry
        anchorView.post {
            val inflater = LayoutInflater.from(requireContext())
            val popupView = inflater.inflate(R.layout.alert_country, null)
            popupWindow =
                PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            val rcyCountry = popupView.findViewById<RecyclerView>(R.id.rcyCountry)
            adapter = AdapterCountry(requireContext(), this)
            rcyCountry.adapter = adapter
            popupWindow?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popupWindow?.isOutsideTouchable = true
            // 🟢 popup exactly anchor ke niche
            popupWindow?.showAsDropDown(anchorView)
        }
    }

    override fun onItemClick(data: String) {
        popupWindow?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        fetchToken()
    }
}


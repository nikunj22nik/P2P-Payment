package com.p2p.application.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.p2p.application.R
import com.p2p.application.databinding.FragmentSendMoneyBinding
import com.p2p.application.databinding.FragmentSettingBinding
import com.p2p.application.model.Receiver
import com.p2p.application.util.AppConstant
import com.p2p.application.util.SessionManager


class SendMoneyFragment : Fragment() {

    private lateinit var binding: FragmentSendMoneyBinding
    private lateinit var sessionManager: SessionManager
    private var previousScreenType:String =""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendMoneyBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnSend.setOnClickListener {
            findNavController().navigate(R.id.enterSecretCodeFragment)
        }

        if(requireArguments().containsKey(AppConstant.SCREEN_TYPE)){
            previousScreenType = requireArguments().getString(AppConstant.SCREEN_TYPE,"")
        }

        val receiver = getReceiverArg()

    }

    fun Fragment.getReceiverArg(): Receiver? {
        val json = arguments?.getString("receiver_json") ?: run {
            Log.w("ARG_WARNING", "receiver_json missing")
            return null
        }

        return try {
            Gson().fromJson(json, Receiver::class.java)
        } catch (e: Exception) {
            Log.e("ARG_ERROR", "Failed to parse receiver_json", e)
            null
        }
    }

}
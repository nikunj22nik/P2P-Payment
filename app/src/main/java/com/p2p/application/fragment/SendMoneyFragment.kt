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
import com.p2p.application.util.SessionManager


class SendMoneyFragment : Fragment() {


    private lateinit var binding: FragmentSendMoneyBinding
    private lateinit var sessionManager: SessionManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendMoneyBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())

  /*      val json = arguments?.getString("receiver_json")
        val receiver = Gson().fromJson(json, Receiver::class.java)
        Log.d("INSIDE_TESTING",receiver.name.toString())
*/
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

    }

}
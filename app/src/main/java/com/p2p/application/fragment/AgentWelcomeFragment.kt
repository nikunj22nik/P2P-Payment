package com.p2p.application.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentAgentWelcomeBinding
import com.p2p.application.databinding.FragmentUserWelcomeBinding


class AgentWelcomeFragment : Fragment() {

    private lateinit var binding: FragmentAgentWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAgentWelcomeBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSetting.setOnClickListener {
            findNavController().navigate(R.id.settingFragment)
        }
        binding.btnNotification.setOnClickListener {
            findNavController().navigate(R.id.notificationListFragment)
        }

    }


}
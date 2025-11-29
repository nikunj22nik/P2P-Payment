package com.p2p.application.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.adapter.AdapterNotification
import com.p2p.application.databinding.FragmentNotificationListBinding
import com.p2p.application.databinding.FragmentSendMoneyBinding
import com.p2p.application.util.SessionManager


class NotificationListFragment : Fragment() {

    private lateinit var binding: FragmentNotificationListBinding
    private lateinit var adapter: AdapterNotification
    private lateinit var sessionManager: SessionManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationListBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        adapter= AdapterNotification(requireContext())
        binding.itemRcy.adapter=adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

    }

}
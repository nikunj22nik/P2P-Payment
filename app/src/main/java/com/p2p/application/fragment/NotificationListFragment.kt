package com.p2p.application.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.datatransport.runtime.scheduling.persistence.EventStoreModule_PackageNameFactory.packageName
import com.p2p.application.R
import com.p2p.application.adapter.AdapterNotification
import com.p2p.application.databinding.FragmentNotificationListBinding
import com.p2p.application.databinding.FragmentSendMoneyBinding
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager


class NotificationListFragment : Fragment() {

    private lateinit var binding: FragmentNotificationListBinding
    private lateinit var adapter: AdapterNotification
    private lateinit var sessionManager: SessionManager

    private var statusClick = true

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

        binding.imgToggle.setOnClickListener {
            if (checkNotificationPermission()){
                openNotificationSettings()
            }else{
                askNotificationPermission()
            }
        }

    }


    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            loadPermission()
        }

    fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else{
                loadPermission()
            }
        }
    }
    fun openNotificationSettings() {
        val intent = Intent().apply {
            action = "android.settings.APP_NOTIFICATION_SETTINGS"
            putExtra("android.provider.extra.APP_PACKAGE", requireContext().packageName)
        }
        startActivity(intent)
    }



    fun checkNotificationPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 and below: permission is always granted
            true
        }
    }

    private fun loadPermission(){
        if (checkNotificationPermission()){
            binding.imgToggle.setImageResource(R.drawable.toggle_on)
        }else{
            binding.imgToggle.setImageResource(R.drawable.toggle_off)
        }
    }


    override fun onResume() {
        super.onResume()
        loadPermission()
    }

}
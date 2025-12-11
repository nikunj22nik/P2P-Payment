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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.adapter.AdapterNotification
import com.p2p.application.databinding.FragmentNotificationListBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.TransactionNotification
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class NotificationListFragment : Fragment() {

    private lateinit var binding: FragmentNotificationListBinding
    private lateinit var adapter: AdapterNotification
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel : NotificationViewModel

    private var statusClick = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationListBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        adapter= AdapterNotification(requireContext())
        binding.itemRcy.adapter=adapter
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
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

        callingNotificationApi()
    }

    private fun callingNotificationApi(){
        lifecycleScope.launch {
            LoadingUtils.show(requireActivity())
            viewModel.getAllNotification().collect {
                when(it){
                    is NetworkResult.Success ->{
                        val list = it.data
                        val updated = updateListInBackground(list)

                        if(list?.size ==0){
                            binding.noDataView.visibility =View.VISIBLE
                        }
                        LoadingUtils.hide(requireActivity())
                        adapter.updateAdapter(updated)
                    }
                    is NetworkResult.Error ->{
                        binding.noDataView.visibility =View.VISIBLE
                        LoadingUtils.hide(requireActivity())
                        LoadingUtils.showErrorDialog(requireActivity(),it.message.toString())
                    }
                    else ->{

                    }
                }
            }
        }
    }

    suspend fun updateListInBackground(
        list: MutableList<TransactionNotification>?
    ): MutableList<TransactionNotification> {

        return withContext(Dispatchers.IO) {
            list?.map { item ->
                item.copy(
                    created_at = formatDate(item.created_at ?: "")
                )
            }?.toMutableList()?:mutableListOf()
        }
    }
    fun formatDate(input: String): String {
        val zonedDateTime = ZonedDateTime.parse(input)
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return zonedDateTime.format(outputFormatter)
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
package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.p2p.application.R
import com.p2p.application.adapter.AdapterHomeTransaction
import com.p2p.application.adapter.AdapterMerchant
import com.p2p.application.databinding.FragmentUserWelcomeBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.homemodel.Data
import com.p2p.application.model.homemodel.Transaction
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeFragment : Fragment(), ItemClickListener {

    private lateinit var binding: FragmentUserWelcomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: AdapterHomeTransaction
    private lateinit var adapterMerchant: AdapterMerchant
    private lateinit var sessionManager: SessionManager
    private lateinit var dialogSend : BottomSheetDialog
    private var selectedType: String=""
    private val readContactsPermission = 100
    private var openedSettings = false
    private var dataHome: Data? = null
    private var transactionsList: MutableList<Transaction> = mutableListOf()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserWelcomeBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        selectedType=sessionManager.getLoginType()?:""
        viewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        adapter=AdapterHomeTransaction(requireContext(),this,transactionsList)
        adapterMerchant=AdapterMerchant(requireContext())
        binding.itemRcy.adapter=adapter
        handleBackPress()
        homeApi()
        handleWelComeScreen()
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
        binding.imgPay.setOnClickListener {
            showAlertPay()
        }
        binding.imgscan.setOnClickListener {
            findNavController().navigate(R.id.QRFragment)
        }
        binding.imgSend.setOnClickListener {
            showAlertSend()
        }
        binding.btnRebalancing.setOnClickListener {
            findNavController().navigate(R.id.rebalancingFragment)
        }
        binding.btnScanCode.setOnClickListener {
            findNavController().navigate(R.id.QRFragment)
        }
        binding.btnSeeAll.setOnClickListener {
            findNavController().navigate(R.id.transactionFragment)
        }

        binding.swipeRefresh.setOnRefreshListener {
            homeApi()
        }
    }


    private fun handleWelComeScreen(){
        if (sessionManager.getIsWelcome()){
            if (selectedType.equals(MessageError.USER,true)){
                binding.layTitle.visibility = View.VISIBLE
                binding.layBalance.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
                binding.layTransaction.visibility = View.GONE
                binding.viewPayOr.visibility = View.VISIBLE
                binding.viewPay.visibility = View.GONE
                binding.imgPay.visibility = View.VISIBLE
                binding.imgSend.visibility = View.VISIBLE
                binding.imgText.visibility = View.INVISIBLE
                binding.tvHeader.text = "Welcome to \nMany Mobile Money"
                binding.tvHSubHeader.text = "Send and receive money instantly with just a few \ntaps."
            }
            if (selectedType.equals(MessageError.MERCHANT,true)){
                binding.layTitle.visibility = View.VISIBLE
                binding.layBalance.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
                binding.layTransaction.visibility = View.GONE
                binding.viewPayOr.visibility = View.VISIBLE
                binding.viewPay.visibility = View.GONE
                binding.tvHeader.text = "Welcome Merchant"
                binding.tvHSubHeader.text = "Let customers pay you via QR and keep full visibility \nof your transfer records."
                binding.imgPay.visibility = View.GONE
                binding.imgSend.visibility = View.GONE
                binding.imgText.visibility = View.VISIBLE
            }
            if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)) {
                if (selectedType.equals(MessageError.AGENT,true)){
                    binding.tvHeader.text = "Welcome Agent"
                    binding.tvHSubHeader.text = "Manage Transfer & Rebalancing Tasks Easily."
                }
                if (selectedType.equals(MessageError.MASTER_AGENT,true)){
                    binding.tvHeader.text = "Welcome Master Agent"
                    binding.tvHSubHeader.text = "Handle transfers and Rebalancing from BBS \nefficiently."
                }
                binding.btnSetting.setColorFilter("#FFFFFF".toColorInt())
                binding.imgHide.setColorFilter("#FFFFFF".toColorInt())
                binding.btnNotification.setColorFilter("#FFFFFF".toColorInt())
                binding.tvHeader.setTextColor("#FFFFFF".toColorInt())
                binding.tvHSubHeader.setTextColor("#FFFFFF".toColorInt())
                binding.tvBalance.setTextColor("#FFFFFF".toColorInt())
                binding.tvBalanceTitle.setTextColor("#FFFFFF".toColorInt())
                binding.recentTitle.setTextColor("#000000".toColorInt())
                binding.viewPayOr.visibility = View.GONE
                binding.viewPay.visibility = View.VISIBLE
                binding.noData.setBackgroundResource(R.drawable.rafikiicon)
                binding.main.setBackgroundResource(R.drawable.bgimg)
                binding.view.setBackgroundResource(R.drawable.circletopcurve)
                binding.tvRebalancing.setCompoundDrawablesWithIntrinsicBounds(R.drawable.maximize, 0, 0, 0)
                binding.tvRebalancing.setTextColor("#444444".toColorInt())
                binding.btnRebalancing.setBackgroundResource(R.drawable.button_custom_border)
                binding.imgLogo.setBackgroundResource(R.drawable.bbs_logo)
            }
        } else{
            if (selectedType.equals(MessageError.USER,true)){
                binding.layTitle.visibility = View.GONE
                binding.layBalance.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                binding.layTransaction.visibility = View.VISIBLE
                binding.viewPayOr.visibility = View.VISIBLE
                binding.viewPay.visibility = View.GONE
                binding.imgPay.visibility = View.VISIBLE
                binding.imgSend.visibility = View.VISIBLE
                binding.imgText.visibility = View.INVISIBLE
            }
            if (selectedType.equals(MessageError.MERCHANT,true)){
                binding.layTitle.visibility = View.GONE
                binding.layBalance.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                binding.layTransaction.visibility = View.VISIBLE
                binding.viewPayOr.visibility = View.VISIBLE
                binding.viewPay.visibility = View.GONE
                binding.tvHeader.text = "Welcome Merchant"
                binding.tvHSubHeader.text = "Let customers pay you via QR and keep full visibility \nof your transfer records."
                binding.imgPay.visibility = View.GONE
                binding.imgSend.visibility = View.GONE
                binding.imgText.visibility = View.VISIBLE
            }
            if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)) {
                binding.layTitle.visibility = View.GONE
                binding.layBalance.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
                binding.layTransaction.visibility = View.VISIBLE
                binding.main.setBackgroundResource(R.drawable.circletopcurve)
                binding.view.setBackgroundResource(R.drawable.circletopcurveblack)
                binding.btnSetting.setColorFilter("#000000".toColorInt())
                binding.imgHide.setColorFilter("#000000".toColorInt())
                binding.btnNotification.setColorFilter("#000000".toColorInt())
                binding.tvHeader.setTextColor("#000000".toColorInt())
                binding.tvHSubHeader.setTextColor("#000000".toColorInt())
                binding.tvBalance.setTextColor("#000000".toColorInt())
                binding.tvBalanceTitle.setTextColor("#000000".toColorInt())
                binding.recentTitle.setTextColor("#FFFFFF".toColorInt())
                binding.tvRebalancing.setCompoundDrawablesWithIntrinsicBounds(R.drawable.maximize_white, 0, 0, 0)
                binding.tvRebalancing.setTextColor("#FFFFFF".toColorInt())
                adapter.updateColor("#FFFFFF")
                binding.btnRebalancing.setBackgroundResource(R.drawable.user_select_inactive)
                binding.imgLogo.setBackgroundResource(R.drawable.agentlogo)
            }
        }
    }

    private fun homeApi(){
        if (isOnline(requireContext())) {
            show(requireActivity())
            lifecycleScope.launch {
                viewModel.homeRequest().collect {
                    hide(requireActivity())
                    binding.swipeRefresh.isRefreshing = false
                    when(it){
                        is NetworkResult.Success ->{
                            it.data?.data?.let { data->
                                dataHome=data
                            }
                            showUIData()
                        }
                        is NetworkResult.Error ->{
                            LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                        }
                        is NetworkResult.Loading -> {
                            // optional: loading indicator dismayed
                        }
                    }
                }
            }
        } else {
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
        }
    }

    private fun showUIData(){
        dataHome?.let { data ->
            binding.tvBalance.text = (data.wallet?.balance?:"0") +" "+ (data.wallet?.currency?:"")
            transactionsList.clear()
            data.transactions?.let { list->
                transactionsList.addAll(list)
            }
            if (transactionsList.isNotEmpty()){
                adapter.updateList(transactionsList)
                binding.layTransaction.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
            }else{
                showNoData()
            }
           /*if (selectedType.equals(MessageError.USER,true) || selectedType.equals(MessageError.AGENT,true)) {
                if(data.secret_code_status){
                    findNavController().navigate(R.id.secretCodeFragment)
                }
            }*/
        }
    }

    private fun showNoData(){
        if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)) {
            binding.noData.setBackgroundResource(R.drawable.rafikiicon)
        }else{
            binding.noData.setBackgroundResource(R.drawable.userhomeicon)
        }
        binding.layTransaction.visibility = View.GONE
        binding.noData.visibility = View.VISIBLE
    }
    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                   requireActivity().finish()
                }
            }
        )
    }

    fun showAlertPay(){
        val dialogWeight = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialogWeight.setContentView(R.layout.choosemerchent_alert)
        dialogWeight.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialogWeight.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogWeight.window?.setGravity(Gravity.BOTTOM)
        val bottomSheet = dialogWeight.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val btnTransfer = dialogWeight.findViewById<LinearLayout>(R.id.btnTransfer)
        val itemRcy = dialogWeight.findViewById<RecyclerView>(R.id.itemRcy)
        itemRcy?.adapter=adapterMerchant
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isHideable = true // Prevent swipe down to hide
            behavior.state = BottomSheetBehavior.STATE_EXPANDED // Fully expand
            behavior.skipCollapsed = true
        }
        btnTransfer?.setOnClickListener {
            dialogWeight.dismiss()
            showAlertPayMerchant()
        }
        dialogWeight.show()
    }

    fun showAlertSend(){
        dialogSend = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialogSend.setContentView(R.layout.send_alert)
        dialogSend.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialogSend.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogSend.window?.setGravity(Gravity.BOTTOM)
        val bottomSheet = dialogSend.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val layNumber = dialogSend.findViewById<LinearLayout>(R.id.layNumber)
        val layContact = dialogSend.findViewById<LinearLayout>(R.id.layContact)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isHideable = true
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
        layContact?.setOnClickListener {
            askContactPermission()
        }
        layNumber?.setOnClickListener {
            dialogSend.dismiss()
            findNavController().navigate(R.id.newNumberFragment)
        }
        dialogSend.show()
    }

    private fun askContactPermission() {
        if (checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                readContactsPermission
            )
        } else {
            dialogSend.dismiss()
            findNavController().navigate(R.id.toContactFragment)
        }
    }


    fun showAlertPayMerchant(){
        val dialogWeight = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialogWeight.setContentView(R.layout.paymerchent_alert)
        dialogWeight.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialogWeight.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogWeight.window?.setGravity(Gravity.BOTTOM)
        val bottomSheet = dialogWeight.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val btnContinue = dialogWeight.findViewById<LinearLayout>(R.id.btnContinue)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isHideable = true // Prevent swipe down to hide
            behavior.state = BottomSheetBehavior.STATE_EXPANDED // Fully expand
            behavior.skipCollapsed = true
        }
        btnContinue?.setOnClickListener {
            dialogWeight.dismiss()
        }
        dialogWeight.show()
    }

    override fun onItemClick(data: String) {
        findNavController().navigate(R.id.receiptFragment)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == readContactsPermission) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                dialogSend.dismiss()
                findNavController().navigate(R.id.toContactFragment)
            } else {
                showAlertContact()
            }
        }

    }
    private fun showAlertContact() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.contact_sms_alert)
        dialog.setCancelable(true)
        val btnContinue = dialog.findViewById<LinearLayout>(R.id.btnContinue)
        btnContinue?.setOnClickListener {
            dialog.dismiss()
            openedSettings = true
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", requireContext().packageName, null)
            startActivity(intent)
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
    }
    override fun onResume() {
        super.onResume()
        if (openedSettings){
            openedSettings = false
            askContactPermission()
        }
        sessionManager.setIsWelcome(false)
    }
}
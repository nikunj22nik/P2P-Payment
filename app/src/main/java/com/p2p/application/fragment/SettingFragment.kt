package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import com.p2p.application.R
import com.p2p.application.activity.MainActivity
import com.p2p.application.adapter.AdapterSwitchUser
import com.p2p.application.databinding.FragmentSettingBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.LoginModel
import com.p2p.application.model.switchmodel.User
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.LoadingUtils.Companion.showErrorDialog
import com.p2p.application.util.LoadingUtils.Companion.toInitials
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SettingFragment : Fragment(),ItemClickListener {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel : SettingViewModel
    private var selectedType: String = ""
    private var fcmToken: String = ""
    private lateinit var dialogUser : BottomSheetDialog
    private var switchUserList: MutableList<User> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        viewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        selectedType = sessionManager.getLoginType().orEmpty()
        Log.d("selectedType", "*****$selectedType")
        handleBackPress()
        setValueFromSession()
        return binding.root
    }

    @SuppressLint("UseKtx")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvUserType.text = selectedType
        binding.layswitch.setOnClickListener {
            if (isOnline(requireContext())){
                switchUserListApi()
            }else{
                showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            }
        }
        binding.imgBack.setOnClickListener {
            findNavController().navigate(R.id.userWelcomeFragment)
        }
        binding.btnDelete.setOnClickListener {
            alertSessionClear("delete")
        }
        binding.btnInvitation.setOnClickListener {
            findNavController().navigate(R.id.inviteFriendFragment)
        }
        binding.btnAccountLimit.setOnClickListener {
            findNavController().navigate(R.id.accountLimitFragment)
        }
        binding.btnEditCode.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("screenType", "settingCode")
            findNavController().navigate(R.id.forgotCodeFragment,bundle)
        }
        binding.btnLogout.setOnClickListener {
            alertSessionClear("logout")
        }
        binding.btnTransaction.setOnClickListener {
            findNavController().navigate(R.id.transactionFragment)
        }
        binding.btnService.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = "tel:${binding.tvNumbar.text}".toUri()
            startActivity(intent)
        }
        if (selectedType.equals(AppConstant.MERCHANT,true)){
            binding.btnAccountLimit.visibility = View.GONE
            binding.btnEditCode.visibility = View.GONE
        }
        if (selectedType.equals(AppConstant.AGENT,true) || selectedType.equals(AppConstant.MASTER_AGENT,true)){
            binding.btnAccountLimit.visibility = View.GONE
        }
    }

    private fun switchUserListApi(){
        show(requireActivity())
        lifecycleScope.launch {
            viewModel.userAccountList().collect { result ->
                hide(requireActivity())
                when (result) {
                    is NetworkResult.Success -> {
                        val dataUser=result.data?.data
                        switchUserList.clear()
                        dataUser?.user?.let {
                            switchUserList.add(User("", it.first_name, it.id, it.last_name, it.phone, it.role, AppConstant.USER, "Ready to grow your business reach with a", userActive = (AppConstant.USER == selectedType), accountActive = true))
                        }?:run {
                            switchUserList.add(User("", "", 0, "", "", "", AppConstant.USER, "Ready to grow your business reach with a", userActive = false, accountActive = false))
                        }
                        dataUser?.merchant?.let {
                            switchUserList.add(User("", it.first_name, it.id, it.last_name, it.phone, it.role, AppConstant.MERCHANT, "Ready to grow your business reach with a", userActive = (AppConstant.MERCHANT == selectedType), accountActive = true))
                        }?:run {
                            switchUserList.add(User("", "", 0, "", "", "", AppConstant.MERCHANT, "Ready to grow your business reach with a", userActive = false, accountActive = false))
                        }
                        dataUser?.agent?.let {
                            switchUserList.add(User("", it.first_name, it.id, it.last_name, it.phone, it.role, AppConstant.AGENT, "Want to boost your earning potential with an", userActive = (AppConstant.AGENT == selectedType), accountActive = true))
                        }?:run {
                            switchUserList.add(User("", "", 0, "", "", "", AppConstant.AGENT, "Want to boost your earning potential with an", userActive = false, accountActive = false))
                        }
                        dataUser?.master_agent?.let {
                            switchUserList.add(User("", it.first_name, it.id, it.last_name, it.phone, it.role, AppConstant.MASTER_AGENT, "Want to lead and manage at scale with a", userActive = (AppConstant.MASTER_AGENT == selectedType), accountActive = true))
                        }?:run {
                            switchUserList.add(User("", "", 0, "", "", "", AppConstant.MASTER_AGENT, "Want to lead and manage at scale with a", userActive = false, accountActive = false))
                        }
                        switchUserList = switchUserList.sortedByDescending { it.accountActive }.toMutableList()
                        showAlertSwitch()
                    }
                    is NetworkResult.Error -> {
                        showErrorDialog(requireContext(), result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        // optional: loading indicator dismayed
                    }
                }
            }
        }
    }
    private fun setValueFromSession(){
        val name = sessionManager.getFirstName() +" "+ sessionManager.getLastName()
        binding.tvName.text = name
        binding.tvNumbar.text = sessionManager.getPhoneNumber()
        binding.tvShortName.text = toInitials(name)
    }
    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
            }
        )
    }
    fun showAlertSwitch(){
        dialogUser = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialogUser.setContentView(R.layout.switch_alert)
        dialogUser.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialogUser.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogUser.window?.setGravity(Gravity.BOTTOM)
        val bottomSheet = dialogUser.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val rcyUser = dialogUser.findViewById<RecyclerView>(R.id.rcyUser)
        val adapter = AdapterSwitchUser(requireContext(), this,switchUserList)
        rcyUser?.adapter = adapter
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isHideable = true // Prevent swipe down to hide
            behavior.state = BottomSheetBehavior.STATE_EXPANDED // Fully expand
            behavior.skipCollapsed = true
        }
        dialogUser.show()
    }
    fun alertSessionClear(viewType: String){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        val layout = if (viewType.equals("delete", true)) { R.layout.delete_alert } else { R.layout.logout_alert }
        dialog?.setContentView(layout)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams
        val imgCross: ImageView =dialog.findViewById(R.id.imgcross)
        val btnOk: LinearLayout =dialog.findViewById(R.id.btnOk)
        val btnNo: LinearLayout =dialog.findViewById(R.id.btnNo)
        imgCross.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            if (isOnline(requireContext())){
                dialog.dismiss()
                show(requireActivity())
                apiCall(viewType,dialog)
            }else{
                showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            }
        }
        btnNo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun apiCall(viewType: String, dialog: Dialog) {
        lifecycleScope.launch {
            viewModel.apiCallLogOutAndDelete(viewType).collect { result ->
                hide(requireActivity())
                when (result) {
                    is NetworkResult.Success -> {
                        dialog.dismiss()
                        sessionManager.sessionClear()
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    is NetworkResult.Error -> {
                        showErrorDialog(requireContext(), result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        // optional: loading indicator dismayed
                    }
                }
            }
        }
    }

    override fun onItemClick(data: String) {
        val selectData= switchUserList[data.toInt()]
        if (selectData.userActive == true){
            dialogUser.dismiss()
        }else{
             if (selectData.accountActive == true){
                 if (isOnline(requireContext())){
                     switchUserApi(selectData.id.toString(),selectData.phone,selectData.type)
                 }else{
                     showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
                 }
             }else{
                 dialogUser.dismiss()
                 sessionManager.clearSession()
                 sessionManager.setLoginType(selectData.type)
                 findNavController().navigate(R.id.createAccountFragment)
             }
        }
    }
    private fun switchUserApi(id: String, phone: String, role: String?) {
        show(requireActivity())
        val loginType = AppConstant.mapperType(role)
        lifecycleScope.launch {
            viewModel.switchUserApiRequest(id,phone,loginType,fcmToken).collect { result ->
                hide(requireActivity())
                when (result) {
                    is NetworkResult.Success -> {
                        dialogUser.dismiss()
                        val response = result.data ?: return@collect
                        val user = response.user
                        when (role) {
                            AppConstant.USER -> handleUserLogin(response,role)
                            AppConstant.MERCHANT -> {
                                sessionManager.clearSession()
                                sessionManager.setLoginType(role)
                                sessionManager.setAuthToken(response.token?:"")
                                if(response.user.verification_status ==1){
                                    sessionManager.clearSession()
                                    sessionManager.setLoginType(role)
                                    SessionManager(requireContext()).apply {
                                        setAuthToken(response.token?:"")
                                        setFirstName(user.first_name ?: "")
                                        setLastName(user.last_name ?: "")
                                        setPhoneNumber(user.phone ?: "")
                                        setIsLogin(true)
                                        if (response.user.mpin!=null){
                                            setIsPin(true)
                                        }
                                    }
                                }
                                if(response.user.verification_docs_upload_status ==1 ){
                                    findNavController().navigate(R.id.merchantVerificationFragment)
                                }else {
                                    handleVerificationStatus(status = user.verification_status, userRole = AppConstant.MERCHANT)
                                }
                            }
                            AppConstant.AGENT -> {
                                sessionManager.clearSession()
                                sessionManager.setLoginType(role)
                                sessionManager.setAuthToken(response.token?:"")
                                if(response.user.verification_status ==1){
                                    SessionManager(requireContext()).apply {
                                        setAuthToken(response.token?:"")
                                        setFirstName(user.first_name ?: "")
                                        setLastName(user.last_name ?: "")
                                        setPhoneNumber(user.phone ?: "")
                                        setIsLogin(true)
                                        if (response.user.mpin!=null){
                                            setIsPin(true)
                                        }
                                    }
                                }
                                handleVerificationStatus(status = user.verification_status, userRole = AppConstant.AGENT)
                            }
                            AppConstant.MASTER_AGENT -> {
                                sessionManager.clearSession()
                                sessionManager.setLoginType(role)
                                sessionManager.setAuthToken(response.token?:"")
                                if(response.user.verification_status ==1){
                                    SessionManager(requireContext()).apply {
                                        setAuthToken(response.token?:"")
                                        setFirstName(user.first_name ?: "")
                                        setLastName(user.last_name ?: "")
                                        setPhoneNumber(user.phone ?: "")
                                        setIsLogin(true)
                                        if (response.user.mpin!=null){
                                            setIsPin(true)
                                        }
                                    }
                                }
                                handleVerificationStatus(status = user.verification_status, userRole = AppConstant.MASTER_AGENT)
                            }
                            else -> {
                                showErrorDialog(requireContext(), "Unknown user type")
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        showErrorDialog(requireContext(), result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        // optional: loading indicator dismayed
                    }
                }
            }
        }
    }
    private fun handleUserLogin(response: LoginModel, role: String) {
        sessionManager.clearSession()
        sessionManager.setLoginType(role)
        val user = response.user
        SessionManager(requireContext()).apply {
            setAuthToken(response.token?:"")
            setFirstName(user.first_name ?: "")
            setLastName(user.last_name ?: "")
            setPhoneNumber(user.phone ?: "")
            setIsLogin(true)
            if (response.user.mpin!=null){
                setIsPin(true)
            }
        }
        if (sessionManager.getIsPin()){
            findNavController().navigate(R.id.userWelcomeFragment)
        }else{
            findNavController().navigate(R.id.secretCodeFragment)
        }
    }
    private fun handleVerificationStatus(status: Int, userRole: String) {
        when (userRole) {
            AppConstant.MERCHANT -> {
                when (status) {
                    0 -> showAlertDialog(
                        header = "Verification In Progress",
                        subHeader = "",
                        content = "Your account is under review, and we’ll get back to you within 24–48 hours once verification is complete.",
                        buttonContent = AppConstant.BACK_TO_LOGIN,
                        iconRes = R.drawable.ic_verification_progress
                    )
                    1 -> showAlertDialog(
                        header = "Documents Approved",
                        subHeader = "",
                        content = "Your agent account has been verified successfully. You can now use all features.",
                        buttonContent = AppConstant.BACK_TO_HOME,
                        iconRes = R.drawable.ic_document_approve
                    )
                    2 -> showAlertDialog(
                        header = "Documents Rejected",
                        subHeader = "",
                        content = "Verification failed. Please re-upload valid documents.",
                        buttonContent = AppConstant.TRY_AGAIN,
                        iconRes = R.drawable.ic_document_rejected
                    )
                }
            }
            AppConstant.AGENT, AppConstant.MASTER_AGENT -> {
                when (status) {
                    0 -> showAlertDialog(
                        header = "Verification In Progress",
                        subHeader = "",
                        content = "Your account is under review, and we’ll get back to you within 24–48 hours once verification is complete.",
                        buttonContent = AppConstant.BACK_TO_LOGIN,
                        iconRes = R.drawable.ic_verification_progress
                    )
                    1 -> showAlertDialog(
                        header = "Verification Approved",
                        subHeader = "",
                        content = "Your agent account has been verified successfully. You can now use all features.",
                        buttonContent = AppConstant.BACK_TO_HOME,
                        iconRes = R.drawable.ic_document_approve
                    )
                    2 -> showAlertDialog(
                        header = "Verification Rejected",
                        subHeader = "",
                        content = "Verification failed. Please re-upload valid documents.",
                        buttonContent = AppConstant.TRY_AGAIN,
                        iconRes = R.drawable.ic_document_rejected
                    )
                }
            }
            else -> {
                when (status) {
                    0 -> showAlertDialog(
                        header = "Verification In Progress",
                        subHeader = "",
                        content = "Your account is under review.",
                        buttonContent = "Back",
                        iconRes = R.drawable.ic_verification_progress
                    )
                    1 -> showAlertDialog(
                        header = "Verification Approved",
                        subHeader = "",
                        content = "Verification completed.",
                        buttonContent = AppConstant.BACK_TO_HOME,
                        iconRes = R.drawable.ic_document_approve
                    )
                    2 -> showAlertDialog(
                        header = "Verification Rejected",
                        subHeader = "",
                        content = "Verification failed.",
                        buttonContent = AppConstant.TRY_AGAIN,
                        iconRes = R.drawable.ic_document_rejected
                    )
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun showAlertDialog(header:String, subHeader:String,
                                content:String,
                                buttonContent:String,
                                iconRes: Int) {
        val dialog = context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.account_create_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = layoutParams
        val btnContinue: LinearLayout = dialog.findViewById(R.id.btnContinue)
        val tvSubHeader: TextView = dialog.findViewById(R.id.tvSubHeader)

        val tvContent: TextView = dialog.findViewById(R.id.tvContent)
        val tvHeader: TextView = dialog.findViewById(R.id.tvHeader)
        val btnTv: TextView = dialog.findViewById(R.id.tvBtn)
        val logo: ImageView = dialog.findViewById(R.id.logo)
        logo.setImageResource(iconRes)
        tvContent.text = content
        tvHeader.text = header

        if (subHeader.isEmpty()) tvSubHeader.visibility = View.GONE
        tvContent.text = content
        tvHeader.text = header
        btnTv.text = buttonContent
        tvSubHeader.text = subHeader
        btnContinue.setOnClickListener {
            dialog.dismiss()
            if (buttonContent.equals(AppConstant.BACK_TO_HOME,true)) {
                if (selectedType.equals(AppConstant.AGENT,true) || selectedType.equals(AppConstant.MASTER_AGENT,true)){
                    if (sessionManager.getIsPin()){
                        findNavController().navigate(R.id.userWelcomeFragment)
                    }else{
                        findNavController().navigate(R.id.secretCodeFragment)
                    }
                }else{
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
            } else if (buttonContent.equals(AppConstant.BACK_TO_LOGIN,true)) {
                findNavController().navigate(R.id.loginFragment)
            } else if (buttonContent.equals(AppConstant.TRY_AGAIN,true)) {
                if(selectedType.equals(AppConstant.MERCHANT,true)){
                    findNavController().navigate(R.id.merchantVerificationFragment)
                }else {
                    findNavController().navigate(R.id.createAccountFragment)
                }
            }
        }
        dialog.show()

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
    override fun onResume() {
        super.onResume()
        fetchToken()
    }
}
package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils.normalizeNumber
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.adapter.ContactDropdownAdapter
import com.p2p.application.databinding.FragmentRebalancingBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.contactmodel.ContactModel
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.util.AppConstant
import com.p2p.application.util.EditTextUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.LoadingUtils.Companion.showErrorDialog
import com.p2p.application.util.MessageError
import com.p2p.application.util.MessageError.Companion.AMOUNT_CNF_ERROR
import com.p2p.application.util.MessageError.Companion.AMOUNT_MATCH_ERROR
import com.p2p.application.util.MessageError.Companion.AMOUNT__ERROR
import com.p2p.application.util.MessageError.Companion.NUMBER_VALIDATION
import com.p2p.application.util.MessageError.Companion.PHONE_NUMBER
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.NumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class RebalancingFragment : Fragment(),ItemClickListener {

    private lateinit var binding: FragmentRebalancingBinding
    private lateinit var viewModel : NumberViewModel
    private var countryList: MutableList<Country> = mutableListOf()
    private var popupWindow: PopupWindow?=null
    private lateinit var sessionManager: SessionManager
    private lateinit var adapterCountry: AdapterCountry
    private var contactsList : MutableList<ContactModel> = mutableListOf()
    private val readContactsPermission = 100
    private lateinit var contactAdapter: ContactDropdownAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRebalancingBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[NumberViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        contactsList.clear()
        makeAstrict()
        askContactPermission()
        return binding.root
    }

    fun makeAstrict() {
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp1)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp2)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp3)
        EditTextUtils.setNumericAsteriskPassword(binding.etOtp4)
    }

        @SuppressLint("SetTextI18n")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            handleBackPress()

            binding.layDeposit.setOnClickListener {
                binding.layDeposit.setBackgroundResource(R.drawable.button_custom)
                binding.layDone.setBackgroundResource(R.drawable.button_custom)
                binding.layWithdrawal.setBackgroundResource(R.drawable.bg_withdrawal)
                binding.tvSubmit.text = "Deposit"
                binding.tvDeposit.setTextColor("#FFFFFF".toColorInt())
                binding.imgAdd.setColorFilter("#FFFFFF".toColorInt())
                binding.tvWithdrawal.setTextColor("#E2692B".toColorInt())
                binding.imgMinus.setColorFilter("#E2692B".toColorInt())
            }

            binding.layWithdrawal.setOnClickListener {
                binding.layDeposit.setBackgroundResource(R.drawable.button_custom_border)
                binding.layDone.setBackgroundResource(R.drawable.bg_withdrawal_fill)
                binding.layWithdrawal.setBackgroundResource(R.drawable.bg_withdrawal_fill)
                binding.tvSubmit.text = "Withdrawal"
                binding.tvWithdrawal.setTextColor("#FFFFFF".toColorInt())
                binding.imgMinus.setColorFilter("#FFFFFF".toColorInt())
                binding.tvDeposit.setTextColor("#B13A7E".toColorInt())
                binding.imgAdd.setColorFilter("#B13A7E".toColorInt())

            }

            binding.imgBack.setOnClickListener {
                findNavController().navigateUp()
            }

            binding.layCountry.setOnClickListener {
                if (isOnline(requireContext())){
                    if (countryList.isNotEmpty()){
                        showCountry()
                    }else{
                        countryListApi()
                    }
                }else{
                    showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
                }
            }

            binding.edSearchAuto.setOnItemClickListener { parent, _, position, _ ->
                val item = parent.getItemAtPosition(position) as ContactModel
                binding.edSearchAuto.setText(item.phone)
                binding.edSearchAuto.setSelection(item.phone?.length ?: 0)
            }

            binding.layDone.setOnClickListener {
                if (isOnline(requireContext())){
                    if (isValidation()){
                        binding.layoutSecretCode.visibility = View.VISIBLE
                        binding.layoutSendMoney.visibility = View.GONE
                    }
                }else{
                    showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
                }
            }

            setupOtpFields(binding.etOtp1, binding.etOtp2, binding.etOtp3, binding.etOtp4)

        }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.layoutSendMoney.isVisible) {
                        findNavController().navigateUp()
                    } else {
                        binding.layoutSecretCode.visibility = View.GONE
                        binding.layoutSendMoney.visibility = View.VISIBLE
                        binding.etOtp1.setText("")
                        binding.etOtp2.setText("")
                        binding.etOtp3.setText("")
                        binding.etOtp4.setText("")
                    }
                }
            }
        )
    }

    private fun setupOtpFields(vararg fields: EditText) {
        fields.forEachIndexed { index, editText ->
            val next = fields.getOrNull(index + 1)
            val prev = fields.getOrNull(index - 1)
            // Detect BACKSPACE (DEL key)
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty()) {
                        prev?.requestFocus()
                        prev?.setSelection(prev.text.length)
                    }
                }
                false
            }
            // Detect input change (typing)
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    when {
                        s?.length == 1 -> {
                            next?.requestFocus()
                            if (index == fields.lastIndex) {
                                val otp = getOtp()
                                if (otp.length == fields.size) {
                                    callingCheckSecretCodeApi(getOtp())
                                }
                            }
                        }
                        s?.isEmpty() == true -> prev?.requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }


    private fun callingCheckSecretCodeApi(code: String) {
        if (!isOnline(requireContext())) {
            showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
            return
        }
        lifecycleScope.launch {
            show(requireActivity())
            viewModel.checkSecretCode(code).collect {
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data == true) {
                            rebalancingRequest()
                        } else {
                            hide(requireActivity())
                            showErrorDialog(requireContext(), MessageError.INVALID_SECRET)
                        }
                    }
                    is NetworkResult.Error -> {
                        hide(requireActivity())
                        showErrorDialog(requireContext(), it.message.toString())
                    }else -> {}
                }
            }
        }
    }


    private fun getOtp(): String {
        return binding.etOtp1.text.toString() +
                binding.etOtp2.text.toString() +
                binding.etOtp3.text.toString() +
                binding.etOtp4.text.toString()
    }

       private fun askContactPermission() {
        if (checkSelfPermission(requireContext(),android.Manifest.permission.READ_CONTACTS) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), readContactsPermission)
        } else {
            loadContacts()
        }
    }


    private fun rebalancingRequest(){
        show(requireActivity())
        lifecycleScope.launch {
            val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
            val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            val countryCode  = binding.tvCountryCode.text.replace("[()]".toRegex(), "")
            viewModel.rebalancingRequest(binding.edCnfAmount.text.toString(),
                countryCode,
                binding.edSearchAuto.text.toString(),currentTime,currentDate).collect { result ->
                hide(requireActivity())
                when (result) {
                    is NetworkResult.Success -> {
                        val data = result.data
                        val json = Gson().toJson(result.data)
                        val bundle = Bundle()
                        Log.d("TESTING_T_ID", "Transaction id" + data?.transaction_id)
                        data?.id?.let {
                            bundle.putLong("transaction_id", data.id.toLong())
                        }
                        bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.QR)
                        findNavController().navigate(R.id.transferStatusFragment, bundle)
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

    private fun isValidation(): Boolean{
        if(binding.edSearchAuto.text.trim().isEmpty()){
            showErrorDialog(requireContext(), PHONE_NUMBER)
            return false
        }else if(binding.edSearchAuto.text.toString().length <=8){
            showErrorDialog(requireContext(),NUMBER_VALIDATION)
            return false
        }else if(binding.edAmount.text.toString().trim().isEmpty()){
            showErrorDialog(requireContext(),AMOUNT__ERROR)
            return false
        }else if(binding.edCnfAmount.text.toString().trim().isEmpty()){
            showErrorDialog(requireContext(),AMOUNT_CNF_ERROR)
            return false
        }else if(!binding.edAmount.text.toString().trim().equals(binding.edCnfAmount.text.toString().trim(),true)){
            showErrorDialog(requireContext(),AMOUNT_MATCH_ERROR)
            return false
        }

        return true
    }

    private fun loadContacts() {
        val tempList = mutableListOf<ContactModel>()

        val cursor = requireContext().contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val idIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                tempList.add(
                    ContactModel(
                        id = it.getString(idIdx),
                        name = it.getString(nameIdx),
                        phone = normalizeNumber(it.getString(phoneIdx))
                    )
                )
            }
        }

        contactsList.clear()
        contactsList.addAll(tempList.distinctBy { it.phone })

        contactAdapter = ContactDropdownAdapter(requireContext(), contactsList)
        binding.edSearchAuto.setAdapter(contactAdapter)
        binding.edSearchAuto.threshold = 1
    }

    private fun countryListApi() {
            show(requireActivity())
            lifecycleScope.launch {
                viewModel.countryRequest().collect { result ->
                    hide(requireActivity())
                    when (result) {
                        is NetworkResult.Success -> {
                            val countries = result.data?.data?.country ?: emptyList()
                            if (countries.isNotEmpty()) {
                                countryList.clear()
                                countryList.addAll(countries)
                                showCountry()
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

    @SuppressLint("InflateParams")
    fun showCountry() {
            val anchorView = binding.layCountry
            anchorView.post {
                val inflater = LayoutInflater.from(requireContext())
                val popupView = inflater.inflate(R.layout.alert_country, null)
                popupWindow = PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
                val rcyCountry = popupView.findViewById<RecyclerView>(R.id.rcyCountry)
                adapterCountry = AdapterCountry(requireContext(), this, countryList)
                rcyCountry.adapter = adapterCountry
                popupWindow?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                popupWindow?.isOutsideTouchable = true
                popupWindow?.showAsDropDown(anchorView)
            }
        }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(data: String) {
            popupWindow?.dismiss()
            val item = countryList[data.toInt()]
            Glide.with(this)
                .load(BuildConfig.MEDIA_URL + item.icon)
                .into(binding.imgIcon)
            binding.tvCountryCode.text = "(" + item.country_code + ")"
        }


}


package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils.normalizeNumber
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.adapter.AdapterToContact
import com.p2p.application.adapter.ContactDropdownAdapter
import com.p2p.application.databinding.FragmentRebalancingBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.contactmodel.ContactModel
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.LoadingUtils.Companion.showErrorDialog
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.NumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class RebalancingFragment : Fragment(),ItemClickListener,ItemClickListenerType {

    private lateinit var binding: FragmentRebalancingBinding
    private lateinit var viewModel : NumberViewModel
    private var countryList: MutableList<Country> = mutableListOf()
    private var popupWindow: PopupWindow?=null
    private var popupWindowContact: PopupWindow?=null
    private lateinit var sessionManager: SessionManager
    private lateinit var adapterCountry: AdapterCountry
    private lateinit var adapter: AdapterToContact
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
        adapter=AdapterToContact(requireContext(),this,contactsList)
        askContactPermission()

        return binding.root
    }

        @SuppressLint("SetTextI18n")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

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

            binding.edSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {

                }

                override fun onTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {

                }

                override fun afterTextChanged(value: Editable) {
                    val searchText = value.toString().trim()

                    if (searchText.isNotEmpty()) {
                        val filtered = contactsList.filter {
                            it.phone?.contains(searchText) == true
                        }.toMutableList()

                        if (filtered.isNotEmpty()) {
                            adapter.updateList(filtered)
                            showContactList()
                        } else {
                            hideContactPopup()
                        }
                    } else {
                        hideContactPopup()
                    }
                }
            })

            binding.edSearchAuto.setOnItemClickListener { parent, _, position, _ ->
                val selected = parent.getItemAtPosition(position).toString()
                binding.edSearch.setText(selected)
                binding.edSearch.setSelection(selected.length)
            }

        }

       private fun askContactPermission() {
        if (checkSelfPermission(requireContext(),android.Manifest.permission.READ_CONTACTS) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), readContactsPermission)
        } else {
            loadContacts()
        }
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
        private fun showContactList() {
            if (popupWindowContact != null && popupWindowContact!!.isShowing) return

            val popupView = LayoutInflater.from(requireContext())
                .inflate(R.layout.alert_country, null)

            popupWindowContact = PopupWindow(
                popupView,
                binding.layEdit.width,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            val rcy = popupView.findViewById<RecyclerView>(R.id.rcyCountry)
            rcy.adapter = adapter

            popupWindowContact!!.apply {
                setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                isOutsideTouchable = true
                showAsDropDown(binding.layEdit)
            }
        }



    private fun hideContactPopup() {
        popupWindowContact?.dismiss()
        popupWindowContact = null
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

    override fun onItemClick(data: String, type: String) {
        hideContactPopup()
        binding.edSearch.setText(data)
        binding.edSearch.setSelection(data.length)
    }

}


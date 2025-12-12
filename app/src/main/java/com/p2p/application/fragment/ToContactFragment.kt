package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.graphics.drawable.toDrawable
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
import com.p2p.application.adapter.AdapterToContact
import com.p2p.application.databinding.FragmentToContactBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.listener.ItemClickListenerType
import com.p2p.application.model.Receiver
import com.p2p.application.model.contactmodel.ContactModel
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.util.AppConstant
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
class ToContactFragment : Fragment(), ItemClickListener,ItemClickListenerType {
    private lateinit var adapter: AdapterToContact
    private lateinit var binding: FragmentToContactBinding
    private lateinit var viewModel : NumberViewModel
    private val readContactsPermission = 100
    private var contactsList : MutableList<ContactModel> = mutableListOf()
    private lateinit var sessionManager: SessionManager
    private var countryList: MutableList<Country> = mutableListOf()
    private var popupWindow: PopupWindow?=null
    private lateinit var adapterCountry: AdapterCountry
    private var userInputNumber=""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentToContactBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[NumberViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        contactsList.clear()
        adapter=AdapterToContact(requireContext(),this,contactsList)
        binding.itemRcy.adapter=adapter
        askContactPermission()
        loadBalance()
        return binding.root
    }
0    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
            override fun afterTextChanged(value: Editable) {
                val searchText = value.toString().trim()
                if (searchText.isNotEmpty()) {
                    val filtered = contactsList.filter {
                        it.phone?.contains(searchText, ignoreCase = true) == true
                    }.toMutableList()
                    if (filtered.isNotEmpty()){
                        adapter.updateList(filtered)
                        binding.itemRcy.visibility = View.VISIBLE
                        binding.layGift.visibility = View.GONE
                        binding.layInfo.visibility = View.GONE
                        binding.layTv.visibility = View.VISIBLE
                    }else{
                        binding.itemRcy.visibility = View.GONE
                        binding.layGift.visibility = View.VISIBLE
                        binding.layTv.visibility = View.GONE
                        binding.layInfo.visibility = View.VISIBLE
                    }
                } else {
                    adapter.updateList(contactsList)
                    binding.itemRcy.visibility = View.VISIBLE
                    binding.layTv.visibility = View.VISIBLE
                    binding.layGift.visibility = View.GONE
                    binding.layInfo.visibility = View.GONE
                }
            }
        })
        binding.imgScan.setOnClickListener {
            findNavController().navigate(R.id.QRFragment)
        }
        binding.imgInvite.setOnClickListener {
            findNavController().navigate(R.id.inviteContactFragment)
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

    }
    @SuppressLint("SetTextI18n")
    override fun onItemClick(data: String) {
        popupWindow?.dismiss()
        val item = countryList[data.toInt()]
        Glide.with(this)
            .load(BuildConfig.MEDIA_URL+item.icon)
            .into(binding.imgIcon)
        binding.tvCountryCode.text = "("+item.country_code+")"
        if (binding.edSearch.text.toString().trim().isNotEmpty()){
            if (userInputNumber.trim().length >= 8) {
                val amountText = binding.tvBalance.text.toString()
                val cleanAmount = amountText.replace(Regex("[^0-9.]"), "")
                val amount = cleanAmount.toDoubleOrNull() ?: 0.0
                if (amount == 0.0) {
                    showErrorDialog(requireContext(), MessageError.AMOUNT_ERROR)
                    return
                }
                if (isOnline(requireContext())) {
                    searchNumber()
                } else {
                    showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
                }
            }
        }
    }
    private fun countryListApi() {
        show(requireActivity())
        lifecycleScope.launch {
            viewModel.countryRequest().collect { result ->
                hide(requireActivity())
                when (result) {
                    is NetworkResult.Success -> {
                        val countries = result.data?.data?.country ?: emptyList()
                        if (countries.isNotEmpty()){
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
            adapterCountry = AdapterCountry(requireContext(), this,countryList)
            rcyCountry.adapter = adapterCountry
            popupWindow?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            popupWindow?.isOutsideTouchable = true
            popupWindow?.showAsDropDown(anchorView)
        }
    }
    private fun loadBalance(){
        if (isOnline(requireContext())){
            show(requireActivity())
            lifecycleScope.launch {
                viewModel.balanceRequest().collect { result ->
                    hide(requireActivity())
                    when (result) {
                        is NetworkResult.Success -> {
                            binding.tvBalance.text = result.data.toString()
                        }
                        is NetworkResult.Error -> {
                            binding.tvBalance.text = "0"
                        }
                        is NetworkResult.Loading -> {
                            // optional: loading indicator dismayed
                        }
                    }
                }
            }
        }else{
           showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
        }
    }
    private fun askContactPermission() {
        if (checkSelfPermission(requireContext(),android.Manifest.permission.READ_CONTACTS) != PermissionChecker.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), readContactsPermission)
        } else {
            loadContacts()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == readContactsPermission &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts()
        }else{
            Toast.makeText(requireContext(), "Permission required to load contacts", Toast.LENGTH_SHORT).show()
        }
    }
    private fun loadContacts() {
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
                val id = it.getString(idIdx)
                val name = it.getString(nameIdx)
                val phone = normalizeNumber(it.getString(phoneIdx))
                contactsList.add(ContactModel(id = id, name = name, phone = phone))
            }
        }
        contactsList = contactsList
            .distinctBy { it.phone }
            .toMutableList()
        // Example: print to console or update UI
        contactsList.forEach { println(it) }
        if (contactsList.isNotEmpty()){
            adapter.updateList(contactsList)
        }
    }

    override fun onItemClick(data: String, type: String) {
        val number = removeCountryCode(data)
//        binding.edSearch.setText(number)
        Log.d("numberUser", "*******$number")
        val amountText = binding.tvBalance.text.toString()
        userInputNumber = number
        val cleanAmount = amountText.replace(Regex("[^0-9.]"), "")
        val amount = cleanAmount.toDoubleOrNull() ?: 0.0
        if (amount == 0.0) {
            showErrorDialog(requireContext(), MessageError.AMOUNT_ERROR)
            return
        }
        if (isOnline(requireContext())) {
            searchNumber()
        } else {
            showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
        }
    }

    private fun searchNumber() {
        val type =AppConstant.mapperType( SessionManager(requireContext()).getLoginType())
        val countryCode  = binding.tvCountryCode.text.replace("[()]".toRegex(), "")
        show(requireActivity())
        lifecycleScope.launch {
            viewModel.searchNewNumberRequest(userInputNumber,countryCode,type).collect {
                hide(requireActivity())
                when(it){
                    is NetworkResult.Success ->{
                        val data = it.data?.data
                        Log.d("userData", "number$data")
                        data?.let { userList->
                            if (userList.isNotEmpty()){
                                val receiverItem = userList[0]
                                val receiver = Receiver(((receiverItem.first_name?:"")+" " + (receiverItem.last_name?:"")),receiverItem.id,receiverItem.phone,receiverItem.role)
                                val json = Gson().toJson(receiver)
                                val bundle = Bundle()
                                bundle.putString("receiver_json", json)
                                bundle.putString("backType", "Number")
                                bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.QR)
                                findNavController().navigate(R.id.sendMoneyFragment, bundle)
                            }else{
                                Log.d("contactsList","size"+contactsList.size)
                                val inputNumber = normalizeNumber(userInputNumber)
                                val userItem = contactsList.find { userNumber ->
                                    removeCountryCode(userNumber.phone.toString()) == inputNumber
                                }
                                if (userItem != null) {
                                    val bundle = Bundle()
                                    bundle.putString("name", userItem.name)
                                    bundle.putString("number", "$countryCode $inputNumber")
                                    findNavController().navigate(R.id.inviteContactFragment, bundle)
                                }
                            }
                        }?:run {
                            val inputNumber = normalizeNumber(userInputNumber)
                            val userItem = contactsList.find { userNumber ->
                                removeCountryCode(userNumber.phone.toString()) == inputNumber
                            }
                            if (userItem != null) {
                                val bundle = Bundle()
                                bundle.putString("name", userItem.name)
                                bundle.putString("number", "$countryCode $inputNumber")
                                findNavController().navigate(R.id.inviteContactFragment, bundle)
                            }
                        }
                    }
                    is NetworkResult.Error ->{
                        showErrorDialog(requireContext(), it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        // optional: loading indicator dismayed
                    }
                }
            }
        }
    }
    fun normalizeNumber(number: String): String {
        return number.replace("+", "")
            .replace(" ", "")
            .takeLast(10)
    }
    fun removeCountryCode(number: String): String {
        // Remove spaces, hyphens, parentheses
        var cleaned = number.replace("[^0-9+]".toRegex(), "")
        // Remove leading "+"
        cleaned = cleaned.removePrefix("+")
        // If number ends with 10 digits, extract it
        val match = Regex("(\\d{10})$").find(cleaned)
        return match?.value ?: cleaned
    }


}
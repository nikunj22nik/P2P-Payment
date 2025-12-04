package com.p2p.application.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.adapter.AdapterToContact
import com.p2p.application.databinding.FragmentToContactBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.listener.ItemClickListener
import com.p2p.application.model.contactmodel.ContactModel
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.NumberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ToContactFragment : Fragment(), ItemClickListener {
    private lateinit var adapter: AdapterToContact
    private lateinit var binding: FragmentToContactBinding
    private lateinit var viewModel : NumberViewModel
    private val readContactsPermission = 100
    private var contactsList : MutableList<ContactModel> = mutableListOf()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentToContactBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[NumberViewModel::class.java]
        sessionManager = SessionManager(requireContext())
        adapter=AdapterToContact(requireContext(),this,contactsList)
        binding.itemRcy.adapter=adapter
        askContactPermission()

        loadBalance()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
                    adapter.updateList(filtered)
                    binding.itemRcy.visibility = View.VISIBLE
                    binding.layGift.visibility = View.GONE
                    binding.layInfo.visibility = View.GONE
                } else {
                    adapter.updateList(contactsList)
                    binding.itemRcy.visibility = View.VISIBLE
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
    }

    override fun onItemClick(data: String) {
        findNavController().navigate(R.id.sendMoneyFragment)
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
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
        }
    }

    private fun askContactPermission() {
        if (checkSelfPermission(requireContext(),android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
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
                val phone = it.getString(phoneIdx)
                contactsList.add(ContactModel(id = id, name = name, phone = phone))
            }
        }
        // Example: print to console or update UI
        contactsList.forEach { println(it) }
        if (contactsList.isNotEmpty()){
            adapter.updateList(contactsList)
        }

    }
}
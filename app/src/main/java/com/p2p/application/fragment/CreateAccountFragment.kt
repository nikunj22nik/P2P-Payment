package com.p2p.application.fragment

import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.p2p.application.R
import com.p2p.application.adapter.AdapterCountry
import com.p2p.application.databinding.FragmentCreateAccountBinding

import com.p2p.application.util.AppConstant
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.viewModel.SendOtpRegisterViewModel

import com.p2p.application.listener.ItemClickListener

import androidx.core.graphics.drawable.toDrawable
import com.p2p.application.activity.MainActivity
import com.p2p.application.model.countrymodel.Country
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.model.countrymodel.Data


class CreateAccountFragment : Fragment(),ItemClickListener {

    private var _binding: FragmentCreateAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""

    private lateinit var viewModel : SendOtpRegisterViewModel


    private var popupWindow: PopupWindow?=null
    private lateinit var adapter: AdapterCountry
    private var countryList: MutableList<Data> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        handleBackPress()
       viewModel = ViewModelProvider(this)[SendOtpRegisterViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserRoleView()
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
            btncreate.setOnClickListener {
                val bundle = bundleOf("screenType" to "Registration")

                findNavController().navigate(R.id.OTPFragment, bundle)
            }
        }

        binding.layCountry.setOnClickListener {
            val mainActivity = requireActivity() as MainActivity
            mainActivity.countryListApi { data ->
                countryList.clear()
                countryList.addAll(data)
                if (countryList.isNotEmpty()) {
                    showCountry()
                }
            }

        }

    }


    fun showCountry() {
        val anchorView = binding.layCountry
        anchorView.post {
            val inflater = LayoutInflater.from(requireContext())
            val popupView = inflater.inflate(R.layout.alert_country, null)
            popupWindow = PopupWindow(popupView, anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            val rcyCountry = popupView.findViewById<RecyclerView>(R.id.rcyCountry)
            adapter = AdapterCountry(requireContext(), this)
            rcyCountry.adapter = adapter
            popupWindow?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            popupWindow?.isOutsideTouchable = true
            // 🟢 popup exactly anchor ke niche
            popupWindow?.showAsDropDown(anchorView)
        }
    }




    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        )
    }

    private fun setupUserRoleView() {
        val title = when (selectedType) {
            AppConstant.USER -> "User Registration"
            AppConstant.MERCHANT -> "Merchant Registration"
            MessageError.AGENT -> "Agent Registration"
            MessageError.MASTER_AGENT -> "Master Agent Registration"
            else -> "Login"
        }
        binding.tvText.text = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // avoid memory leaks
    }

    override fun onItemClick(data: String) {
        popupWindow?.dismiss()
    }
}
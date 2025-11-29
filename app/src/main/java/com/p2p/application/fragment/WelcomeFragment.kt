package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.p2p.application.R
import com.p2p.application.adapter.AdapterHomeTransaction
import com.p2p.application.adapter.AdapterMerchant
import com.p2p.application.databinding.FragmentUserWelcomeBinding
import com.p2p.application.util.CommonFunction.Companion.SPLASH_DELAY
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt
import com.p2p.application.listener.ItemClickListener

class WelcomeFragment : Fragment(), ItemClickListener {

    private lateinit var binding: FragmentUserWelcomeBinding
    private lateinit var adapter: AdapterHomeTransaction
    private lateinit var adapterMerchant: AdapterMerchant
    private lateinit var sessionManager: SessionManager
    private var selectedType: String=""


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserWelcomeBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        selectedType=sessionManager.getLoginType()?:""
        adapter=AdapterHomeTransaction(requireContext(),this)
        adapterMerchant=AdapterMerchant(requireContext())
        binding.itemRcy.adapter=adapter
        if (selectedType.equals(MessageError.AGENT,true) || selectedType.equals(MessageError.MASTER_AGENT,true)){
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

            lifecycleScope.launch {
                delay(SPLASH_DELAY)
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
                adapter.updateColor("#FFFFFF")
            }
        }else{
            lifecycleScope.launch {
                delay(SPLASH_DELAY)
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
                    binding.tvHeader.text = "Welcome to \\nMany Mobile Money"
                    binding.tvHSubHeader.text = "Send and receive money instantly with just a few \ntaps."

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
            }
        }

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
        val dialogWeight = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialogWeight.setContentView(R.layout.send_alert)
        dialogWeight.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialogWeight.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogWeight.window?.setGravity(Gravity.BOTTOM)

        val bottomSheet = dialogWeight.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val layNumber = dialogWeight.findViewById<LinearLayout>(R.id.layNumber)
        val layContact = dialogWeight.findViewById<LinearLayout>(R.id.layContact)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isHideable = true // Prevent swipe down to hide
            behavior.state = BottomSheetBehavior.STATE_EXPANDED // Fully expand
            behavior.skipCollapsed = true
        }

        layContact?.setOnClickListener {
            dialogWeight.dismiss()
            findNavController().navigate(R.id.toContactFragment)
        }
        layNumber?.setOnClickListener {
            dialogWeight.dismiss()
            findNavController().navigate(R.id.newNumberFragment)
        }
        dialogWeight.show()
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


}
package com.p2p.application.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.p2p.application.R
import com.p2p.application.activity.MainActivity
import com.p2p.application.databinding.FragmentSettingBinding
import com.p2p.application.util.SessionManager


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layswitch.setOnClickListener {
          showAlertSwitch()
        }

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
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

    }

    fun showAlertSwitch(){
        val dialogWeight = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        dialogWeight.setContentView(R.layout.switch_alert)
        dialogWeight.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        dialogWeight.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogWeight.window?.setGravity(Gravity.BOTTOM)

        val bottomSheet = dialogWeight.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.isHideable = true // Prevent swipe down to hide
            behavior.state = BottomSheetBehavior.STATE_EXPANDED // Fully expand
            behavior.skipCollapsed = true
        }
        dialogWeight.show()
    }

    fun alertSessionClear(viewType: String){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        if (viewType.equals("delete",true)){
            dialog?.setContentView(R.layout.delete_alert)
        }else{
            dialog?.setContentView(R.layout.logout_alert)
        }
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
            dialog.dismiss()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
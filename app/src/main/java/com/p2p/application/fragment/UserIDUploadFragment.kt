package com.p2p.application.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.p2p.application.R
import com.p2p.application.databinding.FragmentUserIDUploadBinding
import com.p2p.application.util.SessionManager


class UserIDUploadFragment : Fragment() {


    private lateinit var binding: FragmentUserIDUploadBinding
    private lateinit var sessionManager: SessionManager
    private var selectedType: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserIDUploadBinding.inflate(layoutInflater, container, false)
        sessionManager = SessionManager(requireContext())
        handleBackPress()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBack.setOnClickListener {
            findNavController().navigate(R.id.accountLimitFragment)
        }
        binding.btnFront.setOnClickListener {
            selectedType="front"
            ImagePicker.with(this)
                .crop()
                .compress(1024 * 5)
                .maxResultSize(250, 250) // Set max resolution
                .createIntent { intent -> pickImageLauncher.launch(intent) }
        }
        binding.btnBack.setOnClickListener {
            selectedType="back"
            ImagePicker.with(this)
                .crop()
                .compress(1024 * 5)
                .maxResultSize(250, 250) // Set max resolution
                .createIntent { intent -> pickImageLauncher.launch(intent) }
        }
        binding.btnSubmit.setOnClickListener {
            showAlertDialog()
        }
        binding.cross1.setOnClickListener {
            binding.viewFront.visibility = View.GONE
        }
        binding.cross2.setOnClickListener {
            binding.viewBack.visibility = View.GONE
        }

    }


    private val pickImageLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (selectedType.equals("front",true)){
                binding.viewFront.visibility = View.VISIBLE
                result.data?.data?.let { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(binding.imgFront)
                }
            }
            if (selectedType.equals("back",true)){
                binding.viewBack.visibility = View.VISIBLE
                result.data?.data?.let { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(binding.imgBackUpload)
                }
            }
        }
    }
    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.accountLimitFragment)
                }
            }
        )
    }
    fun showAlertDialog(){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.account_create_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams
        val btnContinue: LinearLayout =dialog.findViewById(R.id.btnContinue)
        val tvSubHeader: TextView =dialog.findViewById(R.id.tvSubHeader)
        val logo: ImageView =dialog.findViewById(R.id.logo)
        val tvHeader: TextView =dialog.findViewById(R.id.tvHeader)
        val tvBtn: TextView =dialog.findViewById(R.id.tvBtn)
        logo.setBackgroundResource(R.drawable.lsicon_submit_filled)
        tvHeader.text="Documents Submitted"
        tvBtn.text="Got it"
        tvSubHeader.text="Your ID document has been sent for \nverification. You’ll be notified once your \naccount limit is increased."
        btnContinue.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }
        dialog.show()
    }


}
package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.p2p.application.R
import com.p2p.application.adapter.AdapterMerchantVerification
import com.p2p.application.databinding.FragmentMerchantVerificationBinding
import com.p2p.application.databinding.FragmentSettingBinding
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.MessageError
import com.p2p.application.util.MultipartUtil
import com.p2p.application.util.SessionManager
import kotlinx.coroutines.selects.select
import org.w3c.dom.Text


class MerchantVerificationFragment : Fragment() {


    private lateinit var binding: FragmentMerchantVerificationBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var adapterBusinessId: AdapterMerchantVerification
    private lateinit var adapterBusinessRegister: AdapterMerchantVerification
    private lateinit var adapterBusinessTax: AdapterMerchantVerification
    private var businessIdUri : MutableList<Uri> = mutableListOf()
    private var businessRegister : MutableList<Uri> = mutableListOf()
    private var taxIdUri : MutableList<Uri> = mutableListOf()
    private var selected = AppConstant.BUSINESS_ID
    val MAX_TOTAL_IMAGES = 5
    private var selectedImageUri : Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val type =selected

                Log.d("TESTING_SIZE",type.toString())


                val clipData = result.data?.clipData
                val singleUri = result.data?.data
                if (clipData != null) {
                    Log.d("TESTING_SIZE","clip data is"+clipData.itemCount.toString())

                    for (i in 0 until clipData.itemCount) {
                        val newUri = clipData.getItemAt(i).uri

                        when (type) {
                            AppConstant.BUSINESS_ID -> {
                                // Check if businessIdUri already contains the newUri
                                if (!businessIdUri.contains(newUri)) {
                                    businessIdUri.add(newUri)
                                    if(businessIdUri.size>5) break
                                }
                            }
                            AppConstant.BUSINESS_REGISTER -> {
                                // Check if businessRegister already contains the newUri
                                if (!businessRegister.contains(newUri)) {
                                    businessRegister.add(newUri)
                                    if(businessRegister.size>5)break

                                }
                            }
                            AppConstant.TAX_ID -> {
                                // Check if taxIdUri already contains the newUri
                                if (!taxIdUri.contains(newUri)) {
                                    taxIdUri.add(newUri)
                                    if(taxIdUri.size>5)break
                                }
                            }

                            AppConstant.PROFILE ->{ binding.mainImage.setImageURI(newUri)
                             selectedImageUri = newUri
                            }
                        }
                    }
                }
                else if(singleUri != null){
                    binding.mainImage.setImageURI(singleUri)
                    selectedImageUri = singleUri
                }
                when(type){
                    AppConstant.BUSINESS_ID->{
                        Log.d("TESTING_SIZE",businessIdUri.size.toString())
                        checkAndShowErrorForImageSize(businessIdUri)
                        adapterBusinessId.updateAdapter(businessIdUri)
                    }
                    AppConstant.BUSINESS_REGISTER ->{
                        checkAndShowErrorForImageSize(businessRegister)
                        adapterBusinessRegister.updateAdapter(businessRegister)
                    }
                    AppConstant.TAX_ID ->{
                        checkAndShowErrorForImageSize(taxIdUri)
                        adapterBusinessTax.updateAdapter(taxIdUri)
                    }


                }


            }
        }

    private fun checkAndShowErrorForImageSize(arr:  MutableList<Uri>){
        val originalSize = arr.size

        arr.removeAll { uri ->
            MultipartUtil.isFileLargerThan5MB(requireContext(), uri)
        }

        if (arr.size < originalSize) {
            LoadingUtils.showErrorDialog(
                requireContext(), "Please upload images less than 5 MB."
            )
        }
        Log.d("TESTING_SIZE",arr.size.toString())

    }

    fun openGallery(selectType: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.putExtra("SELECT_TYPE", selectType) // <<----- parameter
        imagePickerLauncher.launch(intent)
    }
    fun openGalleryForSingle(selectType: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra("SELECT_TYPE", selectType) // Optional param
        imagePickerLauncher.launch(intent)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMerchantVerificationBinding.inflate(layoutInflater, container, false)
        sessionManager= SessionManager(requireContext())
        adapterInitializationTask()


        handleBackPress()
        handleClickListener()
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVerify.setOnClickListener {
            showAlert()
        }
    }

    private fun adapterInitializationTask(){

        binding.constUploadWork.setOnClickListener {
            openGalleryForSingle(AppConstant.PROFILE)
        }

        adapterBusinessId= AdapterMerchantVerification(requireContext()) { position, uri ->
            Log.d("AdapterCallback", "size is"+businessIdUri.size +"position"+position)
            businessIdUri.removeAt(position)
            adapterBusinessId.notifyItemRemoved(position)
        }

        adapterBusinessRegister=AdapterMerchantVerification(requireContext()){ position, uri ->
            businessRegister.removeAt(position)
            Log.d("AdapterCallback", "Item removed at $position -> $uri")
            adapterBusinessRegister.notifyItemRemoved(position)
        }
        adapterBusinessTax=AdapterMerchantVerification(requireContext()){ position, uri ->
         taxIdUri.removeAt(position)

            Log.d("AdapterCallback", "Item removed at $position -> $uri")
            adapterBusinessTax.notifyItemRemoved(position)
        }


        binding.rcyID.adapter = adapterBusinessId
        binding.rcyNumber.adapter = adapterBusinessRegister
        binding.rcyTaxId.adapter = adapterBusinessTax


    }

    private fun handleClickListener(){
        binding.businessId.setOnClickListener {
            selected = AppConstant.BUSINESS_ID
            if(businessIdUri.size <5) {
                openGallery(AppConstant.BUSINESS_ID)
            }else{
                LoadingUtils.showErrorDialog(requireContext(),"A maximum of 5 images can be uploaded.")
            }
        }
        binding.businesRegister.setOnClickListener {
            selected =AppConstant.BUSINESS_REGISTER
            if(businessRegister.size<5) openGallery(AppConstant.BUSINESS_REGISTER) else{
                LoadingUtils.showErrorDialog(requireContext(),"A maximum of 5 images can be uploaded.")
            }
        }
        binding.txId.setOnClickListener {
            selected = AppConstant.TAX_ID
            if(taxIdUri.size<5)
            openGallery(AppConstant.TAX_ID)else{
                LoadingUtils.showErrorDialog(requireContext(),"A maximum of 5 images can be uploaded.")
            }
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


    @SuppressLint("SetTextI18n")
    fun showAlert(){
        val dialog= context?.let { Dialog(it, R.style.BottomSheetDialog) }
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.id_submit_alert)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog?.window!!.attributes)
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams
        val btnContinue: LinearLayout =dialog.findViewById(R.id.btnContinue)
        val tvHeader: TextView =dialog.findViewById(R.id.tvHeader)
        val tvSubHeader: TextView =dialog.findViewById(R.id.tvSubHeader)
        val tvBtn: TextView =dialog.findViewById(R.id.tvBtn)
        val logo: ImageView =dialog.findViewById(R.id.logo)
        tvHeader.text="Verification Submitted!"
        tvBtn.text="Back to Login"
        tvSubHeader.text="Your documents have been submitted successfully. We'll review them and notify you within 24-48 hours."
        logo.setBackgroundResource(R.drawable.material_symbols_fact_check_outline_rounded)
        btnContinue.setOnClickListener {
            dialog.dismiss()
            findNavController().navigate(R.id.loginFragment)
        }
        dialog.show()
    }



}
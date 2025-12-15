package com.p2p.application.fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.p2p.application.BuildConfig
import com.p2p.application.R
import com.p2p.application.databinding.FragmentQRBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.Receiver
import com.p2p.application.util.AppConstant
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.SessionManager
import com.p2p.application.util.MessageError
import com.p2p.application.viewModel.QrCodeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QRFragment : Fragment() {
    private lateinit var viewModel : QrCodeViewModel
    private lateinit var scanQrBtn: TextView
    private lateinit var scannedValueTv: TextView
    private var isScannerInstalled = false
    private lateinit var scanner: GmsBarcodeScanner
    private lateinit var binding: FragmentQRBinding
    private var selectedType: String=""
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQRBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[QrCodeViewModel::class.java]
        sessionManager= SessionManager(requireContext())
        selectedType = sessionManager.getLoginType().orEmpty()
        installGoogleScanner()
        initVars()
        binding.qrCodeImage.visibility =View.INVISIBLE

//        if (selectedType.equals(AppConstant.MERCHANT,true)){
//            binding.bottomTabs.visibility  = View.INVISIBLE
//        }
        registerUiListener()

        return binding.root
    }
    private fun installGoogleScanner() {
        val moduleInstall = ModuleInstall.getClient(requireContext())
        val moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(GmsBarcodeScanning.getClient(requireContext()))
            .build()

        moduleInstall.installModules(moduleInstallRequest).addOnSuccessListener {
            isScannerInstalled = true
        }.addOnFailureListener {
            isScannerInstalled = false
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }
        gettingMyQrCode()
    }
    private fun gettingMyQrCode(){
        lifecycleScope.launch{
            LoadingUtils.show(requireActivity())
            viewModel.qrCodeScanner().collect {
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hide(requireActivity())
                        binding.qrCodeImage.visibility =View.VISIBLE
                        val imgPath = if(it.data != null)BuildConfig.MEDIA_URL + it.data else null
                        Glide.with(requireContext())
                            .load(imgPath)
                            .into(binding.qrCodeImage)
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hide(requireActivity())
                        LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                    }
                    else->{

                    }
                }
            }
        }
    }
    private fun initVars() {
        scanQrBtn = binding.tvScanner
        scannedValueTv = binding.tvScanVal
        val options = initializeGoogleScanner()
        scanner = GmsBarcodeScanning.getClient(requireContext(), options)
    }

    override fun onResume() {
        super.onResume()
        scanQrBtn.background = null
        scanQrBtn.setTextColor("#ffffff".toColorInt())
        binding.myCard.setTextColor("#1B1B1B".toColorInt())
        binding.myCard.setBackgroundResource(R.drawable.active)

    }
    private fun initializeGoogleScanner(): GmsBarcodeScannerOptions {
        return GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom().build()
    }
    private fun registerUiListener() {
        scanQrBtn.setOnClickListener {
            scanQrBtn.setBackgroundResource(R.drawable.active)
            binding.myCard.background = null
            scanQrBtn.setTextColor("#1B1B1B".toColorInt())
            binding.myCard.setTextColor("#ffffff".toColorInt())
            if (isScannerInstalled) {
                startScanning()
            } else {
                Toast.makeText(requireContext(), "Please try again...", Toast.LENGTH_SHORT).show()
            }
        }
        binding.myCard.setOnClickListener {
            scanQrBtn.background = null
            scanQrBtn.setTextColor("#ffffff".toColorInt())
            binding.myCard.setTextColor("#1B1B1B".toColorInt())
            binding.myCard.setBackgroundResource(R.drawable.active)
        }
        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun startScanning() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val result = barcode.rawValue
                try {
                    val receiver = Gson().fromJson(result, Receiver::class.java)
                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.RESUMED) {
                            val json = Gson().toJson(receiver)
                            val bundle = Bundle()
                            bundle.putString("receiver_json", json)
                            if(SessionManager(requireContext()).getLoginType().equals(AppConstant.MERCHANT,true)){
                                if(receiver.user_type.equals("user",true)){
                                    LoadingUtils.showErrorDialog(requireContext(),"Merchants are not allowed to send money to users.")
                                     return@repeatOnLifecycle
                                }
                            }
                            bundle.putString(AppConstant.SCREEN_TYPE, AppConstant.QR)
                            findNavController().navigate(R.id.sendMoneyFragment, bundle)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Error","*******"+e.message)
                    LoadingUtils.showErrorDialog(requireContext(), MessageError.showQRError)

                }

            }
            .addOnCanceledListener {

            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Oops! We couldn’t locate a merchant account with that ID.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }



}


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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.p2p.application.R
import com.p2p.application.databinding.FragmentQRBinding
import com.p2p.application.model.Receiver


class QRFragment : Fragment() {


    private lateinit var scanQrBtn: TextView
    private lateinit var scannedValueTv: TextView
    private var isScannerInstalled = false
    private lateinit var scanner: GmsBarcodeScanner
    private lateinit var binding: FragmentQRBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQRBinding.inflate(layoutInflater, container, false)
        installGoogleScanner()
        initVars()
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
                    if (receiver.user_id == null) {
                        throw Exception("error")
                    }

                    lifecycleScope.launchWhenResumed {
                        val json = Gson().toJson(receiver)
                        val bundle = Bundle()
                        bundle.putString("receiver_json", json)
                        findNavController().navigate(R.id.sendMoneyFragment, bundle)
                    }

                } catch (e: Exception) {
                    Log.d("Error","*******"+e.message)
                    Toast.makeText(
                        requireContext(),
                        "Oops! We couldn’t locate a merchant account with that ID.",
                        Toast.LENGTH_SHORT
                    ).show()
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


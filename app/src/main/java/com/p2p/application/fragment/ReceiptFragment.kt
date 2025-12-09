package com.p2p.application.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentReceiptBinding
import com.p2p.application.databinding.FragmentTransferStatusBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.receiptmodel.ReceiptModel
import com.p2p.application.util.DownloadWorker
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.getBitmapFromView
import com.p2p.application.util.LoadingUtils.Companion.hide
import com.p2p.application.util.LoadingUtils.Companion.isOnline
import com.p2p.application.util.LoadingUtils.Companion.show
import com.p2p.application.util.MessageError
import com.p2p.application.util.SessionManager
import com.p2p.application.view.applyExactGradient
import com.p2p.application.viewModel.HomeViewModel
import com.p2p.application.viewModel.ReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ReceiptFragment : Fragment() {

    private lateinit var binding: FragmentReceiptBinding
    private lateinit var sessionManager: SessionManager
    private var selectType: String = ""
    private var receiptId: String = ""
    private lateinit var viewModel: ReceiptViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReceiptBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[ReceiptViewModel::class.java]
        requireActivity().window.setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, android.view.WindowManager.LayoutParams.FLAG_SECURE)
        sessionManager = SessionManager(requireContext())
        selectType = sessionManager.getLoginType() ?: ""
        receiptId=arguments?.getString("receiptId","")?:""

        loadApi()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.layPrice.applyExactGradient()

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.userWelcomeFragment)
        }

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.imgShare.setOnClickListener {
            shareCard(binding.cardHide,requireContext())
        }

        binding.pullToRefresh.setOnRefreshListener {
            loadApi()
        }
        binding.llDownload.setOnClickListener {
            callingReceiptDownload()
        }

    }

    private fun loadApi(){
        if (isOnline(requireContext())) {
            show(requireActivity())
            lifecycleScope.launch {
                viewModel.receiptRequest(receiptId).collect {
                    hide(requireActivity())
                    binding.pullToRefresh.isRefreshing = false
                    when(it){
                        is NetworkResult.Success ->{
                            showUiData(it.data)
                        }
                        is NetworkResult.Error ->{
                            LoadingUtils.showErrorDialog(requireContext(),it.message.toString())
                        }
                        is NetworkResult.Loading -> {
                            // optional: loading indicator dismayed
                        }
                    }
                }
            }
        } else {
            LoadingUtils.showErrorDialog(requireContext(), MessageError.NETWORK_ERROR)
        }
    }
    private fun callingReceiptDownload(){
        lifecycleScope.launch {
            LoadingUtils.show(requireActivity())
            viewModel.generateTransactionPdf(transactionId = receiptId).collect {
                when(it){
                    is NetworkResult.Success ->{
                        LoadingUtils.hide(requireActivity())
                        it.data?.let {

                            DownloadWorker().downloadPdfWithNotification(
                                binding.root.context,
                                it,
                                "Transaction_" + "file_${(10000..99999).random()}.pdf"
                            )
                        }
                    }
                    is NetworkResult.Error ->{
                        LoadingUtils.hide(requireActivity())
                    }
                    else ->{

                    }
                }
            }
        }
    }


    private fun showUiData(data: ReceiptModel?) {
        data?.let { userData->
            if (userData.data?.status.toString().equals("success",true)){
                binding.tvStatus.text = "Transfer successful!"
                binding.tvStatus.setTextColor("#03B961".toColorInt())
                binding.tvText.text = "Your transaction has been completed successfully."

                binding.tvStatus1.text = "Transfer successful!"
                binding.tvStatus1.setTextColor("#03B961".toColorInt())
                binding.tvText1.text = "Your transaction has been completed successfully."
            }else{
                binding.tvStatus.text = "Transfer Failed!"
                binding.tvStatus.setTextColor("#F90B1B".toColorInt())
                binding.tvText.text = "Your transaction has been failed."

                binding.tvStatus1.text = "Transfer Failed!"
                binding.tvStatus1.setTextColor("#F90B1B".toColorInt())
                binding.tvText1.text = "Your transaction has been failed."

            }
            binding.layPrice.text = (userData.data?.amount?:"") + " "+(userData.data?.currency?:"")
            binding.tvPrice.text = (userData.data?.amount?:"") + " "+(userData.data?.currency?:"")
            binding.tvName.text = (userData.data?.receiver?.first_name?:"") + " " + (userData.data?.receiver?.last_name?:"")
            binding.tvPhone.text = userData.data?.receiver?.phone?:""
            binding.tvDate.text = userData.data?.date?:""
            binding.tvTime.text = userData.data?.time?:""
            binding.tvReference.text = userData.data?.reference_no?:""
            binding.tvFree.text = userData.data?.transaction_fee?:""

            //  show Data share receipt
            binding.layPrice1.text = (userData.data?.amount?:"") + " "+(userData.data?.currency?:"")
            binding.tvPrice1.text = (userData.data?.amount?:"") + " "+(userData.data?.currency?:"")
            binding.tvName1.text = (userData.data?.receiver?.first_name?:"") + " " + (userData.data?.receiver?.last_name?:"")
            binding.tvPhone1.text = userData.data?.receiver?.phone?:""
            binding.tvDate1.text = userData.data?.date?:""
            binding.tvTime1.text = userData.data?.time?:""
            binding.tvReference1.text = userData.data?.reference_no?:""
            binding.tvFree1.text = userData.data?.transaction_fee?:""
        }
    }

    fun shareCard(cardView: View, context: Context) {
        val bitmap = getBitmapFromView(cardView)
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "receipt.png")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
        val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        requireActivity().startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
    }


}
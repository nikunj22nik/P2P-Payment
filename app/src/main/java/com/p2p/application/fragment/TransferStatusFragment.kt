package com.p2p.application.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.p2p.application.R
import com.p2p.application.databinding.FragmentTransferStatusBinding
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.Transaction
import com.p2p.application.util.DownloadWorker
import com.p2p.application.util.LoadingUtils
import com.p2p.application.util.LoadingUtils.Companion.getBitmapFromView
import com.p2p.application.util.SessionManager
import com.p2p.application.view.applyExactGradient
import com.p2p.application.viewModel.ReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


@AndroidEntryPoint
class TransferStatusFragment : Fragment() {

    private lateinit var binding: FragmentTransferStatusBinding
    private lateinit var sessionManager: SessionManager
    private var selectType: String = ""
    private var transaction : Long? = null
    private lateinit var viewModel : ReceiptViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransferStatusBinding.inflate(inflater, container, false)
//        requireActivity().window.setFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE, android.view.WindowManager.LayoutParams.FLAG_SECURE)

        viewModel = ViewModelProvider(this)[ReceiptViewModel::class.java]

        sessionManager = SessionManager(requireContext())

        selectType = sessionManager.getLoginType() ?: ""

        handleBackPress()

        if(requireArguments().containsKey("transaction_id")){
            transaction = requireArguments().getLong("transaction_id")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layPrice.applyExactGradient()
        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.userWelcomeFragment)
        }
        binding.btnShare.setOnClickListener {
            shareCard(binding.card,requireContext())
        }
        settingDataToUi()
    }

    private fun settingDataToUi(){
        transaction?.let {
           callingGetTransactionDetailApi(transactionId = transaction)
        }
    }



    @SuppressLint("SetTextI18n")
    private fun callingGetTransactionDetailApi(transactionId: Long?) {

        lifecycleScope.launch {
            transactionId?.let {
                LoadingUtils.show(requireActivity())
                viewModel.receiptRequest(transactionId.toString()).collect {
                    when(it){
                        is NetworkResult.Success ->{
                            LoadingUtils.hide(requireActivity())
                            val data = it.data?.data
                            binding.layPrice.text = data?.amount +" "+data?.currency
                            binding.nameNumber.text = data?.receiver?.first_name +" "+data?.receiver?.last_name+" "+data?.receiver?.phone
                            binding.amnt.text = data?.amount+" "+data?.currency
                            binding.date.text = data?.date?:""
                            binding.time.text = data?.time?:""
                            binding.tvReference.text = data?.reference_no?:""
                            binding.tvFees.text = data?.transaction_fee?:""
                            binding.card.visibility=View.VISIBLE

                            if (it.data?.data?.receiver?.role?.equals("merchant", ignoreCase = true) == true) {
                                binding.rlTotalPayment.visibility =View.VISIBLE
                                val total = (data?.amount?.toDouble() ?: 0.0) +
                                        (data?.transaction_fee?.toDouble() ?: 0.0)
                                binding.tvFeesTotal.setText(total.toString())
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
    }



    fun Fragment.getReceiverArgTransfer(): Transaction? {
        val json = arguments?.getString("transaction_json") ?: run {
            Log.w("ARG_WARNING", "transaction_json missing")
            return null
        }

        return try {
            Gson().fromJson(json, Transaction::class.java)
        } catch (e: Exception) {
            Log.e("ARG_ERROR", "Failed to parse receiver_json", e)
            null
        }
    }


    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.userWelcomeFragment)
                }
            }
        )
    }

    fun shareCard(cardView: View, context: Context) {
        val bitmap = getBitmapFromView(cardView)
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "receipt.png")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
        fileOutputStream.close()
        val uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        requireActivity().startActivity(Intent.createChooser(shareIntent, "Share Receipt"))
    }

}
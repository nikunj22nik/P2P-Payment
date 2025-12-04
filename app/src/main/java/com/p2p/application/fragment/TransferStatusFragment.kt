package com.p2p.application.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentAccountTypeBinding
import com.p2p.application.databinding.FragmentTransferStatusBinding
import com.p2p.application.util.LoadingUtils.Companion.getBitmapFromView
import com.p2p.application.util.SessionManager
import com.p2p.application.view.applyExactGradient
import java.io.File
import java.io.FileOutputStream


class TransferStatusFragment : Fragment() {

    private lateinit var binding: FragmentTransferStatusBinding
    private lateinit var sessionManager: SessionManager
    private var selectType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransferStatusBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectType = sessionManager.getLoginType() ?: ""

        handleBackPress()

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

    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
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
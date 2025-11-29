package com.p2p.application.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentReceiptBinding
import com.p2p.application.databinding.FragmentTransferStatusBinding
import com.p2p.application.util.SessionManager
import com.p2p.application.view.applyExactGradient


class ReceiptFragment : Fragment() {


    private lateinit var binding: FragmentReceiptBinding
    private lateinit var sessionManager: SessionManager
    private var selectType: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReceiptBinding.inflate(inflater, container, false)
        sessionManager = SessionManager(requireContext())
        selectType = sessionManager.getLoginType() ?: ""
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
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this cool app!")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

    }

}
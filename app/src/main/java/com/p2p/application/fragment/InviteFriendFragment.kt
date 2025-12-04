package com.p2p.application.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentInviteContactBinding
import com.p2p.application.databinding.FragmentInviteFriendBinding


class InviteFriendFragment : Fragment() {


    private lateinit var binding: FragmentInviteFriendBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInviteFriendBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btninvite.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this cool app!")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }

    }


}
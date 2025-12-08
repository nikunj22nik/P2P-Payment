package com.p2p.application.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.p2p.application.databinding.FragmentInviteContactBinding


class InviteContactFragment : Fragment() {


    private lateinit var binding: FragmentInviteContactBinding
    private var name: String=""
    private var number: String=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInviteContactBinding.inflate(layoutInflater, container, false)
        name= arguments?.getString("name","")?:""
        number= arguments?.getString("number","")?:""
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvName.text = name
        binding.tvNumber.text = number
        binding.btnInvite.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this cool app!")
            startActivity(Intent.createChooser(shareIntent, "Share via"))
        }
        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}
package com.p2p.application.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.adapter.AdapterToContact
import com.p2p.application.databinding.FragmentToContactBinding
import com.p2p.application.listener.ItemClickListener


class ToContactFragment : Fragment(), ItemClickListener {
    private lateinit var adapter: AdapterToContact
    private lateinit var binding: FragmentToContactBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToContactBinding.inflate(layoutInflater, container, false)

        adapter=AdapterToContact(requireContext(),this)
        binding.itemRcy.adapter=adapter

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(value: Editable) {
                if (value.toString().isNotEmpty()) {
                    binding.layGift.visibility = View.VISIBLE
                    binding.layInfo.visibility = View.VISIBLE
                    binding.itemRcy.visibility = View.GONE
                }else{
                    binding.layGift.visibility = View.GONE
                    binding.layInfo.visibility = View.GONE
                    binding.itemRcy.visibility = View.VISIBLE
                }
            }


        })
        binding.imgScan.setOnClickListener {
            findNavController().navigate(R.id.QRFragment)
        }

        binding.imgInvite.setOnClickListener {
            findNavController().navigate(R.id.inviteContactFragment)
        }

    }

    override fun onItemClick(data: String) {
        findNavController().navigate(R.id.sendMoneyFragment)
    }

}
package com.p2p.application.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.navigation.fragment.findNavController
import com.p2p.application.R
import com.p2p.application.databinding.FragmentInviteFriendBinding
import com.p2p.application.databinding.FragmentRebalancingBinding


class RebalancingFragment : Fragment() {

    private lateinit var binding: FragmentRebalancingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRebalancingBinding.inflate(layoutInflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.layDeposit.setOnClickListener {
            binding.layDeposit.setBackgroundResource(R.drawable.button_custom)
            binding.layDone.setBackgroundResource(R.drawable.button_custom)
            binding.layWithdrawal.setBackgroundResource(R.drawable.bg_withdrawal)
            binding.tvSubmit.text = "Deposit"
            binding.tvDeposit.setTextColor("#FFFFFF".toColorInt())
            binding.imgAdd.setColorFilter("#FFFFFF".toColorInt())
            binding.tvWithdrawal.setTextColor("#E2692B".toColorInt())
            binding.imgMinus.setColorFilter("#E2692B".toColorInt())


        }

        binding.layWithdrawal.setOnClickListener {
            binding.layDeposit.setBackgroundResource(R.drawable.button_custom_border)
            binding.layDone.setBackgroundResource(R.drawable.bg_withdrawal_fill)
            binding.layWithdrawal.setBackgroundResource(R.drawable.bg_withdrawal_fill)
            binding.tvSubmit.text = "Withdrawal"
            binding.tvWithdrawal.setTextColor("#FFFFFF".toColorInt())
            binding.imgMinus.setColorFilter("#FFFFFF".toColorInt())
            binding.tvDeposit.setTextColor("#B13A7E".toColorInt())
            binding.imgAdd.setColorFilter("#B13A7E".toColorInt())

        }

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }



    }



}
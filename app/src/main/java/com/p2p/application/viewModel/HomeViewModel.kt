package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.Transaction
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.model.homemodel.HomeModel
import com.p2p.application.model.recentmerchant.RecentMerchantModel
import com.p2p.application.model.recentpepole.RecentPeopleModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {


    suspend fun homeRequest() : Flow<NetworkResult<HomeModel>>{
        return repository.homeRequest()
    }

    suspend fun homeMerchantRequest() : Flow<NetworkResult<RecentMerchantModel>>{
        return repository.homeMerchantRequest()
    }

    suspend fun sendMoney(
        senderType: String,
        receiver_id: Int,
        receiverType: String,
        amount: String,
        confirmAmount: String,
        currentTime: String,
        currentDate: String
    ): Flow<NetworkResult<Transaction>> {

        return repository.sendMoney(senderType,receiver_id,receiverType,amount,confirmAmount,
            currentTime,currentDate
        ).onEach {

        }

    }

}
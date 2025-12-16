package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.Transaction
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.model.newnumber.NewNumberModel
import com.p2p.application.model.receiptmodel.ReceiptModel
import com.p2p.application.model.recentpepole.RecentPeopleModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class NumberViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {


    suspend fun countryRequest() : Flow<NetworkResult<CountryModel>>{
        return repository.countryRequest()
    }

    suspend fun recentPeopleRequest() : Flow<NetworkResult<RecentPeopleModel>>{
        return repository.recentPeopleRequest()
    }

    suspend fun searchNewNumberRequest(
        phone: String,
        countryCode: String,
        apiType: String
    ) : Flow<NetworkResult<NewNumberModel>>{
        return repository.searchNewNumberRequest(phone,countryCode,apiType)
    }

    suspend fun balanceRequest() : Flow<NetworkResult<String>>{
        return repository.balanceRequest()
    }

    suspend fun checkSecretCode(secretCode: String): Flow<NetworkResult<Boolean>>{
        return repository.checkSecretCode(secretCode).onEach {

        }
    }

    suspend fun rebalancingRequest(
        amount: String,
        country: String,
        mobile: String,
        currentTime: String,
        currentDate: String
    ): Flow<NetworkResult<Transaction>>{
        return repository.rebalancingRequest(amount,country,mobile,currentTime,currentDate)
    }

}
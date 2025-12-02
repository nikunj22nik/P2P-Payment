package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class SendOtpForgotSecretViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {

    suspend fun sendOtp(
        phone: String, userType: String?,
        countryCode: String,
        apiType: String,
    ) : Flow<NetworkResult<String>>{
        return repository.sendOtp(phone,userType,countryCode,apiType)
    }


    suspend fun countryRequest() : Flow<NetworkResult<CountryModel>>{
        return repository.countryRequest()
    }

}
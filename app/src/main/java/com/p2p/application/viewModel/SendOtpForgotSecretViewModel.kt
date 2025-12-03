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

    suspend fun countryRequest() : Flow<NetworkResult<CountryModel>>{
        return repository.countryRequest()
    }


    suspend fun sendSecretCodeRequest(
        countryCode: String,
        phone: String,
        apiType: String,
    ) : Flow<NetworkResult<String>>{
        return repository.sendSecretCodeRequest(countryCode,phone,apiType)
    }

    suspend fun otpVerifySecretCodeRequest(
        countryCode: String,
        phone: String,
        otp: String,
        apiType: String,
    ) : Flow<NetworkResult<String>>{
        return repository.otpVerifySecretCodeRequest(countryCode,phone,otp,apiType)
    }

    suspend fun setSecretCodeRequest(
        code: String,
        apiType: String,
    ) : Flow<NetworkResult<String>>{
        return repository.setSecretCodeRequest(code,apiType)
    }


}
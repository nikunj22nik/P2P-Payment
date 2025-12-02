package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.RegisterResponse
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OtpViewModel  @Inject constructor(private var repository: P2PRepository): ViewModel() {

     suspend fun register(
        firstName: String,
        lastName: String,
        countryCode: String,
        phone: String,
        otp: String,
        userType: String,
        fcmToken: String
    ): Flow<NetworkResult<RegisterResponse>>{
       return repository.register(firstName,lastName,countryCode,phone,otp,userType,fcmToken).onEach {

       }
    }


}
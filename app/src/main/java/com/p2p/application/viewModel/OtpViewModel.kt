package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.LoginModel
import com.p2p.application.model.LoginUserModel
import com.p2p.application.model.RegisterResponse
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class OtpViewModel  @Inject constructor(private var repository: P2PRepository): ViewModel() {

    var selectedType: String = ""
     var firstName :String=""
     var lastName :String=""
     var phoneNumber :String=""
     var countryCode :String =""
     var otp :String =""
    var screenType :String =""

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

    suspend fun login(
        phone: String,
        otp: String,
        countryCode: String,
        userType: String,
        fcmToken: String
    ): Flow<NetworkResult<LoginModel>> {
        return repository.login(phone,otp,countryCode,userType,fcmToken).onEach {

        }
    }

    suspend fun resendOtp(
        phone: String, userType: String?,
        countryCode: String,
        apiType: String,
    ) : Flow<NetworkResult<String>>{
        return repository.resendOtp(phone,userType,countryCode,apiType)
    }


}
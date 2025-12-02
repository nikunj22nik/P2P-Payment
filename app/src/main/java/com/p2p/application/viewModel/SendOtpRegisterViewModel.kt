package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import retrofit2.http.Field
import javax.inject.Inject


@HiltViewModel
class SendOtpRegisterViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {

    suspend fun sendOtp(phone :String, userType :String,
                       countryCode :String,
                        apiType :String,
    ) : Flow<NetworkResult<String>>{
        return repository.sendOtp(phone,userType,countryCode,apiType).onEach{
          
        }
    }
}
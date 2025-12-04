package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.LoginModel
import com.p2p.application.model.switchmodel.SwitchUserModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class SettingViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {
    suspend fun apiCallLogOutAndDelete(viewType: String): Flow<NetworkResult<String>>{
        return repository.apiCallLogOutAndDelete(viewType)
    }

    suspend fun userAccountList(): Flow<NetworkResult<SwitchUserModel>>{
        return repository.userAccountList()
    }
    suspend fun switchUserApiRequest(id: String,phone: String,loginType: String,fcmToken: String): Flow<NetworkResult<LoginModel>>{
        return repository.switchUserApiRequest(id,phone,loginType,fcmToken)
    }



}
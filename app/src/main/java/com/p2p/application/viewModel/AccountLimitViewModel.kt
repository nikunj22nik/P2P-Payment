package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.accountlimit.AccountLimitModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class AccountLimitViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {

    suspend fun accountLimitRequest(): Flow<NetworkResult<AccountLimitModel>>{
        return repository.accountLimitRequest()
    }

}
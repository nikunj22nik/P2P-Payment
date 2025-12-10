package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.TransactionNotification
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel  @Inject constructor(private var repository: P2PRepository): ViewModel() {


    suspend fun getAllNotification(): Flow<NetworkResult<MutableList<TransactionNotification>>>{
        return repository.getAllNotification().onEach {

        }
    }
}
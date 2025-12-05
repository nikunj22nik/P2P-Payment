package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.LoginModel
import com.p2p.application.model.receiptmodel.ReceiptModel
import com.p2p.application.model.switchmodel.SwitchUserModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class ReceiptViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {

    suspend fun receiptRequest(id: String): Flow<NetworkResult<ReceiptModel>>{
        return repository.receiptRequest(id)
    }

}
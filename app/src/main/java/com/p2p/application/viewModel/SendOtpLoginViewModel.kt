package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class SendOtpLoginViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {

    suspend fun countryRequest() : Flow<NetworkResult<CountryModel>>{
        return repository.countryRequest()
    }

}
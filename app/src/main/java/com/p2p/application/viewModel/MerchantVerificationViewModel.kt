package com.p2p.application.viewModel

import androidx.lifecycle.ViewModel
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.repository.P2PRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class MerchantVerificationViewModel @Inject constructor(private var repository: P2PRepository): ViewModel() {

    suspend fun merchantVerification(
        businessIdList: ArrayList<MultipartBody.Part>?,
        businessRegisterList: ArrayList<MultipartBody.Part>?,
        taxIdList: ArrayList<MultipartBody.Part>?,
        profileImage: MultipartBody.Part?,
        userType: RequestBody
    ) : Flow<NetworkResult<String>>{
        return repository.merchantVerification(
            businessIdList,businessRegisterList,taxIdList,profileImage,userType
        ).onEach {

        }
    }

}

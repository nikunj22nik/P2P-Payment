package com.p2p.application.repository


import com.google.gson.Gson
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.remote.P2PApi
import com.p2p.application.util.AppConstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Field
import javax.inject.Inject

class P2PRepositoryImpl @Inject constructor(private val api: P2PApi) :P2PRepository {

    override suspend fun sendOtp(@Field("phone")phone :String, @Field("user_type")userType :String,
                                 @Field("country_code")countryCode :String, @Field("apiType") apiType :String,
    ) : Flow<NetworkResult<String>> = flow {
        try {
            api.sendOtp(phone, userType, countryCode, apiType).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("status") && resp.get("status").asBoolean) {
                            var obj = resp.get("data").asJsonObject
                            var otp = obj.get("otp").asInt
                            emit(NetworkResult.Success<String>(otp.toString()))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            }
        }catch (e: Exception) {
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun countryRequest(): Flow<NetworkResult<CountryModel>> = flow {
        try {
            val response = api.countryRequest()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    val countryResponse = Gson().fromJson(respBody, CountryModel::class.java)
                    if (countryResponse.success) {
                        emit(NetworkResult.Success(countryResponse))
                    } else {
                        emit(NetworkResult.Error(countryResponse.message))
                    }
                } else {
                    emit(NetworkResult.Error(AppConstant.unKnownError))
                }
            } else {
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }




}
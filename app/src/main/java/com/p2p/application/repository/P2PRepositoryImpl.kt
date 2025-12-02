package com.p2p.application.repository


import com.p2p.application.Error.ErrorHandler
import com.p2p.application.di.NetworkResult
import com.p2p.application.remote.P2PApi
import com.p2p.application.util.AppConstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Field
import javax.inject.Inject
import kotlin.collections.get
import kotlin.text.get
import kotlin.toString

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



}
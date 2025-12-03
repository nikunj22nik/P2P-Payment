package com.p2p.application.repository


import com.google.gson.Gson
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.LoginModel
import com.p2p.application.model.LoginUserModel
import com.p2p.application.model.RegisterResponse
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.model.homemodel.HomeModel
import com.p2p.application.model.recentpepole.RecentPeopleModel
import com.p2p.application.remote.P2PApi
import com.p2p.application.util.AppConstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Field
import javax.inject.Inject

class P2PRepositoryImpl @Inject constructor(private val api: P2PApi) :P2PRepository {

    override suspend fun sendOtp(@Field("phone")phone :String, @Field("user_type")userType :String?,
                                 @Field("country_code")countryCode :String, @Field("apiType") apiType :String,
    ) : Flow<NetworkResult<String>> = flow {
        try {
            api.sendOtp(phone, userType, countryCode, apiType).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
                            val obj = resp.get("data").asJsonObject
                            val otp = obj.get("otp").asInt
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

    override suspend fun setSecretCodeRequest(
        code: String,
        apiType: String
    ) : Flow<NetworkResult<String>> = flow {
        try {
            api.setSecretCodeRequest(code, apiType).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
                            emit(NetworkResult.Success(resp.toString()))
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

    override suspend fun searchNewNumberRequest(
        phone: String,
        countryCode: String,
        apiType: String
    ) : Flow<NetworkResult<String>> = flow {
        try {
            api.searchNewNumberRequest(phone,countryCode, apiType).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
                            emit(NetworkResult.Success(resp.toString()))
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

    override suspend fun sendSecretCodeRequest(
        countryCode: String,
        phone: String,
        apiType: String
    ) : Flow<NetworkResult<String>> = flow {
        try {
            api.sendSecretCodeRequest(countryCode, phone,apiType).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
                            val obj = resp.get("data").asJsonObject
                            val otp = obj.get("otp").asInt
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

    override suspend fun otpVerifySecretCodeRequest(
        countryCode: String,
        phone: String,
        otp: String,
        apiType: String
    ) : Flow<NetworkResult<String>> = flow {
        try {
            api.otpVerifySecretCodeRequest(countryCode, phone,otp,apiType).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
                            emit(NetworkResult.Success(resp.toString()))
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

    override suspend fun homeRequest(): Flow<NetworkResult<HomeModel>> = flow {
        try {
            val response = api.homeRequest()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    val countryResponse = Gson().fromJson(respBody, HomeModel::class.java)
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

    override suspend fun recentPeopleRequest(): Flow<NetworkResult<RecentPeopleModel>> = flow {
        try {
            val response = api.recentPeopleRequest()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    val peopleResponse = Gson().fromJson(respBody, RecentPeopleModel::class.java)
                    if (peopleResponse.success) {
                        if (peopleResponse.data!=null){
                            emit(NetworkResult.Success(peopleResponse))
                        }else{
                            emit(NetworkResult.Error(peopleResponse.message))
                        }
                    } else {
                        emit(NetworkResult.Error(peopleResponse.message))
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

    override suspend fun register(
        firstName: String,
        lastName: String,
        countryCode: String,
        phone: String,
        otp: String,
        userType: String,
        fcmToken: String
    ): Flow<NetworkResult<RegisterResponse>> = flow {
        try {
            api.register(firstName, lastName, countryCode, phone,otp,userType,fcmToken).apply {
                if (isSuccessful) {
                    body()?.let {
                        resp -> if (resp.has("success") && resp.get("success").asBoolean) {
                         val data = resp.get("data").asJsonObject
                        val registerResponse = Gson().fromJson(data, RegisterResponse::class.java)
                            emit(NetworkResult.Success<RegisterResponse>(registerResponse))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun login(
        phone: String,
        otp: String,
        countryCode: String,
        userType: String,
        fcmToken: String
    ): Flow<NetworkResult<LoginModel>> =flow{
        try {
            api.login(phone, otp, countryCode, userType,fcmToken).apply {
                if (isSuccessful) {
                    body()?.let {
                            resp -> if (resp.has("success") && resp.get("success").asBoolean) {
                        val data = resp.get("data").asJsonObject
                        var loginResponse = Gson().fromJson(data, LoginModel::class.java)
                        emit(NetworkResult.Success<LoginModel>(loginResponse))
                    } else {
                        emit(NetworkResult.Error(resp.get("message").asString))
                    }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun resendOtp(
        phone: String,
        userType: String?,
        countryCode: String,
        apiType: String
    ): Flow<NetworkResult<String>> =flow{
        try {
            api.sendOtp(phone, userType, countryCode, apiType).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
                            val obj = resp.get("data").asJsonObject
                            val otp = obj.get("otp").asInt
                            emit(NetworkResult.Success<String>(otp.toString()))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            }
        }
        catch (e: Exception) {
            emit(NetworkResult.Error(AppConstant.serverError))
        }

    }


}
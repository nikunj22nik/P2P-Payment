package com.p2p.application.repository


import com.google.gson.Gson
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.LoginModel
import com.p2p.application.model.RegisterResponse

import com.p2p.application.model.TransactionHistoryResponse
import com.p2p.application.model.TransactionItem
import com.p2p.application.model.UserInfo

import com.p2p.application.model.accountlimit.AccountLimitModel

import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.model.homemodel.HomeModel
import com.p2p.application.model.recentpepole.RecentPeopleModel
import com.p2p.application.model.switchmodel.SwitchUserModel
import com.p2p.application.remote.P2PApi
import com.p2p.application.util.AppConstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
                            emit(NetworkResult.Success(otp.toString()))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
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
            e.printStackTrace()
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
                            emit(NetworkResult.Success(otp.toString()))
                        } else {
                            emit(NetworkResult.Error(resp.get("message").asString))
                        }
                    } ?: emit(NetworkResult.Error(AppConstant.unKnownError))
                } else {
                    emit(NetworkResult.Error(AppConstant.serverError))
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
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
            e.printStackTrace()
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

    override suspend fun balanceRequest(): Flow<NetworkResult<String>> = flow {
        try {
            val response = api.balanceRequest()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        val currency = if (data.has("currency") && !data.get("currency").isJsonNull) {
                            data.get("currency").asString
                        } else {
                            ""
                        }
                        val balance = if (data.has("balance") && !data.get("balance").isJsonNull) {
                            data.get("balance").asString +" "+ currency
                        } else {
                            "0"
                        }
                        emit(NetworkResult.Success(balance))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
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

    override suspend fun accountLimitRequest(): Flow<NetworkResult<AccountLimitModel>> = flow {
        try {
            val response = api.accountLimitRequest()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val response = Gson().fromJson(respBody, AccountLimitModel::class.java)
                        if (response.success) {
                            emit(NetworkResult.Success(response))
                        } else {
                            emit(NetworkResult.Error(response.message))
                        }
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
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

    override suspend fun apiCallLogOutAndDelete(viewType: String): Flow<NetworkResult<String>> = flow {
        try {
            val response =  if (viewType.equals("logout",true)){
                api.logOutApi()
            }else{
                api.deleteApi()
            }
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        emit(NetworkResult.Success(respBody.toString()))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
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

    override suspend fun switchUserApiRequest(
        id: String,
        phone: String,
        loginType: String,
        fcmToken: String
    ): Flow<NetworkResult<LoginModel>> = flow {
        try {
            val response = api.switchUserApiRequest(id,loginType,phone,fcmToken)
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        val loginResponse = Gson().fromJson(data, LoginModel::class.java)
                        emit(NetworkResult.Success(loginResponse))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
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

    override suspend fun userAccountList(): Flow<NetworkResult<SwitchUserModel>> = flow {
        try {
            val response = api.userAccountList()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    val response = Gson().fromJson(respBody, SwitchUserModel::class.java)
                    if (response.success) {
                        emit(NetworkResult.Success(response))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
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
                            emit(NetworkResult.Success(registerResponse))
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
            e.printStackTrace()
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
                        val loginResponse = Gson().fromJson(data, LoginModel::class.java)
                        emit(NetworkResult.Success(loginResponse))
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
            e.printStackTrace()
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
                            emit(NetworkResult.Success(otp.toString()))
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
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }

    }

    override suspend fun merchantVerification(
        businessIdList: ArrayList<MultipartBody.Part>?,
        list: ArrayList<MultipartBody.Part>?,
        pdfList: ArrayList<MultipartBody.Part>?,
        profileImage: MultipartBody.Part?,
        userType: RequestBody
    ): Flow<NetworkResult<String>> = flow {
        try {
            api.merchantVerification(businessIdList, list,pdfList, profileImage, userType).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {

                            val message = resp.get("message").asString
                            emit(NetworkResult.Success(message))
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
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getTransactionHistory(
        page: Int,
        limit: Int
    ): Flow<NetworkResult<TransactionHistoryResponse>>
        = flow {

            try {
                api.getTransactionHistory(page, limit).apply {

                    if (isSuccessful) {

                        body()?.let { resp ->

                            if (resp.has("success") && resp.get("success").asBoolean) {

                                val dataObject = resp.get("data").asJsonObject

                                val list = dataObject.getAsJsonArray("data").map { element ->
                                    val obj = element.asJsonObject

                                    TransactionItem(
                                        id = obj.get("id").asInt,
                                        amount = obj.get("amount").asString,
                                        currency = obj.get("currency").asString,
                                        status = obj.get("status").asString,
                                        date = obj.get("date").asString,
                                        time = obj.get("time").asString,
                                        transaction_type = obj.get("transaction_type").asString,
                                        user = obj.get("user").asJsonObject.let { u ->
                                            UserInfo(
                                                id = u.get("id").asInt,
                                                first_name = u.get("first_name").asString,
                                                last_name = u.get("last_name").asString,
                                                phone = u.get("phone").asString,
                                                business_logo = if (u.get("business_logo").isJsonNull) null else u.get(
                                                    "business_logo"
                                                ).asString
                                            )
                                        }
                                    )
                                }

                                val finalData = TransactionHistoryResponse(
                                    page = dataObject.get("page").asInt,
                                    limit = dataObject.get("limit").asInt,
                                    total = dataObject.get("total").asInt,
                                    total_page = dataObject.get("total_page").asInt,
                                    data = list
                                )

                                emit(NetworkResult.Success(finalData))

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
                e.printStackTrace()
                emit(NetworkResult.Error(AppConstant.serverError))
            }
        }

    override suspend fun genOneToOneTransactionHistory(userId: Int): Flow<NetworkResult<TransactionHistoryResponse>> =flow{
        try {
            api.genOneToOneTransactionHistory(userId).apply {
                if (isSuccessful) {
                    body()?.let { resp ->
                        if (resp.has("success") && resp.get("success").asBoolean) {
                           // val dataObject = resp.get("data").asJsonObject
                            val list = resp.getAsJsonArray("data").map { element ->
                                val obj = element.asJsonObject
                                TransactionItem(
                                    id = obj.get("id").asInt,
                                    amount = obj.get("amount").asString,
                                    currency = obj.get("currency").asString,
                                    status = obj.get("status").asString,
                                    date = obj.get("date").asString,
                                    time = obj.get("time").asString,
                                    transaction_type = obj.get("transaction_type").asString,
                                    user = obj.get("user").asJsonObject.let { u ->
                                        UserInfo(
                                            id = u.get("id").asInt,
                                            first_name = u.get("first_name").asString,
                                            last_name = u.get("last_name").asString,
                                            phone = u.get("phone").asString,
                                            business_logo = if (u.get("business_logo").isJsonNull) null else u.get(
                                                "business_logo"
                                            ).asString
                                        )
                                    }
                                )
                            }

                            val finalData = TransactionHistoryResponse(
                                page =1,
                                limit = 20,
                                total = 1,
                                total_page = 1,
                                data = list
                            )

                            emit(NetworkResult.Success(finalData))

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
            e.printStackTrace()
            emit(NetworkResult.Error(AppConstant.serverError))
        }
    }

    override suspend fun getQrCode(): Flow<NetworkResult<String>> =flow{
        try {
            val response = api.getQrCode()
            if (response.isSuccessful) {
                val respBody = response.body()
                if (respBody != null) {
                    if (respBody.has("success") && respBody.get("success").asBoolean) {
                        val data = respBody.get("data").asJsonObject
                        val qrCode= data.get("qr_code").asString
                        emit(NetworkResult.Success(qrCode))
                    } else {
                        emit(NetworkResult.Error(respBody.get("message").asString))
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



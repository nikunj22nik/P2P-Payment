package com.p2p.application.repository

import com.google.gson.JsonObject
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.LoginModel
import com.p2p.application.model.ReceiverInfo
import com.p2p.application.model.RegisterResponse
import com.p2p.application.model.Transaction

import com.p2p.application.model.TransactionHistoryResponse
import com.p2p.application.model.accountlimit.AccountLimitModel

import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.model.homemodel.HomeModel
import com.p2p.application.model.newnumber.NewNumberModel
import com.p2p.application.model.recentpepole.RecentPeopleModel
import com.p2p.application.model.switchmodel.SwitchUserModel
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.Part

interface P2PRepository {
    suspend fun sendOtp(@Field("phone")phone :String,@Field("user_type")userType :String?,
                        @Field("countryCode")countryCode :String ,@Field("apiType") apiType :String,
    ) :Flow<NetworkResult<String>>


    suspend fun setSecretCodeRequest(@Field("secret_code")code :String ,@Field("apiType") apiType :String,
    ) :Flow<NetworkResult<String>>

    suspend fun searchNewNumberRequest(@Field("phone")phone :String ,@Field("countryCode") countryCode :String,@Field("apiType") apiType :String
    ) :Flow<NetworkResult<NewNumberModel>>

    suspend fun sendSecretCodeRequest(@Field("countryCode")countryCode :String ,
                                      @Field("phone") phone :String,
                                      @Field("apiType") apiType :String,
    ) :Flow<NetworkResult<String>>

    suspend fun otpVerifySecretCodeRequest(@Field("countryCode")countryCode :String ,
                                      @Field("phone") phone :String,
                                      @Field("otp") otp :String,
                                      @Field("apiType") apiType :String,
    ) :Flow<NetworkResult<String>>


    suspend fun countryRequest() :Flow<NetworkResult<CountryModel>>
    suspend fun homeRequest() :Flow<NetworkResult<HomeModel>>

    suspend fun recentPeopleRequest() :Flow<NetworkResult<RecentPeopleModel>>

    suspend fun balanceRequest() :Flow<NetworkResult<String>>
    suspend fun accountLimitRequest() :Flow<NetworkResult<AccountLimitModel>>

    suspend fun apiCallLogOutAndDelete(viewType: String):Flow<NetworkResult<String>>

    suspend fun switchUserApiRequest(id: String,phone: String,loginType: String,fcmToken: String):Flow<NetworkResult<LoginModel>>
    suspend fun userAccountList():Flow<NetworkResult<SwitchUserModel>>


    suspend fun register(
        @Field("firstName") firstName :String,
        @Field("lastName") lastName :String,
        @Field("countryCode") countryCode :String,
        @Field("phone") phone :String,
        @Field("otp") otp :String,
        @Field("user_type") userType :String,
        @Field("fcm_token") fcmToken :String
    ) :Flow<NetworkResult<RegisterResponse>>

    suspend fun login(
        @Field("phone") phone :String,
        @Field("otp") otp:String,
        @Field("countryCode") countryCode :String,
        @Field("user_type") userType :String,
        @Field("fcm_token") fcmToken :String
    ) :Flow<NetworkResult<LoginModel>>

    suspend fun resendOtp(@Field("phone")phone :String,@Field("user_type")userType :String?,
                        @Field("countryCode")countryCode :String ,@Field("apiType") apiType :String,
    ) :Flow<NetworkResult<String>>


    suspend fun merchantVerification(
        @Part businessIdList : ArrayList<MultipartBody.Part>?,
        @Part list : ArrayList<MultipartBody.Part>?,
        @Part pdfList : ArrayList<MultipartBody.Part>?,
        @Part profileImage: MultipartBody.Part?,
        @Part("user_type") userType : RequestBody
    ): Flow<NetworkResult<String>>

    suspend fun getTransactionHistory(
        @Field("page")page: Int,
        @Field("limit")limit: Int
    ): Flow<NetworkResult<TransactionHistoryResponse>>

    suspend fun genOneToOneTransactionHistory(
        @Field("user_id") userId :Int
    ) :Flow<NetworkResult<TransactionHistoryResponse>>

    suspend fun getQrCode() :Flow<NetworkResult<String>>

    suspend fun sendMoney(
        @Field("sender_type") senderType :String,
        @Field("receiver_id")receiver_id :Int,
        @Field("receiver_type") receiverType :String,
        @Field("amount") amount :String
    ) : Flow<NetworkResult<Transaction>>

    suspend fun receiverProfileImage(
        receiverId:Int
    ) :Flow<NetworkResult<ReceiverInfo>>

    suspend fun checkSecretCode(secret_code:String) : Flow<NetworkResult<Boolean>>


}
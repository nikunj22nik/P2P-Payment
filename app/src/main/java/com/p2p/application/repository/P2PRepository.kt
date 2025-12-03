package com.p2p.application.repository

import com.google.gson.JsonObject
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.RegisterResponse
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.model.homemodel.HomeModel
import com.p2p.application.model.recentpepole.RecentPeopleModel
import com.p2p.application.util.AppConstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.http.Field

interface P2PRepository {
    suspend fun sendOtp(@Field("phone")phone :String,@Field("user_type")userType :String?,
                        @Field("country_code")countryCode :String ,@Field("apiType") apiType :String,
    ) :Flow<NetworkResult<String>>


    suspend fun setSecretCodeRequest(@Field("secret_code")code :String ,@Field("apiType") apiType :String,
    ) :Flow<NetworkResult<String>>

    suspend fun searchNewNumberRequest(@Field("phone")phone :String ,@Field("countryCode") countryCode :String,@Field("apiType") apiType :String
    ) :Flow<NetworkResult<String>>

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


    suspend fun register(
        @Field("firstName") firstName :String,
        @Field("lastName") lastName :String,
        @Field("CountryCode") countryCode :String,
        @Field("phone") phone :String,
        @Field("otp") otp :String,
        @Field("user_type") userType :String,
        @Field("fcm_token") fcmToken :String
    ) :Flow<NetworkResult<RegisterResponse>>



}
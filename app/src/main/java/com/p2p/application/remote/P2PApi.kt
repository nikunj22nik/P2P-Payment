package com.p2p.application.remote

import com.google.gson.JsonObject
import com.p2p.application.di.NetworkResult
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface P2PApi {

    @POST("send_otp")
    @FormUrlEncoded
    suspend fun sendOtp(@Field("phone")phone :String,
                        @Field("user_type")userType :String?,
                        @Field("countryCode")countryCode :String ,
                        @Field("apiType") apiType :String,
                      ) :Response<JsonObject>


    @GET("get_country")
    suspend fun countryRequest() :Response<JsonObject>

    @POST("home")
    suspend fun homeRequest() :Response<JsonObject>

    @POST("get_recent_people")
    suspend fun recentPeopleRequest() :Response<JsonObject>


    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("firstName") firstName :String,
        @Field("lastName") lastName :String,
        @Field("countryCode") countryCode :String,
        @Field("phone") phone :String,
        @Field("otp") otp :String,
        @Field("user_type") userType :String,
        @Field("fcm_token") fcmToken :String
    ) : Response<JsonObject>


    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("phone") phone :String,
        @Field("otp") otp:String,
        @Field("countryCode") countryCode :String,
        @Field("user_type") userType :String,
        @Field("fcm_token") fcmToken :String
    ) : Response<JsonObject>

    @POST("set_edit_secret_code")
    @FormUrlEncoded
    suspend fun setSecretCodeRequest(
        @Field("secret_code") code :String,
        @Field("user_type") userType :String
    ) : Response<JsonObject>

    @POST("search_new_number")
    @FormUrlEncoded
    suspend fun searchNewNumberRequest(
        @Field("phone") code :String,
        @Field("countryCode") countryCode :String,
        @Field("user_type") userType :String
    ) : Response<JsonObject>

    @POST("forgot_secret_code")
    @FormUrlEncoded
    suspend fun sendSecretCodeRequest(
        @Field("countryCode")countryCode :String ,
        @Field("phone") phone :String,
        @Field("user_type") apiType :String
    ) : Response<JsonObject>

    @POST("forgot_secret_code")
    @FormUrlEncoded
    suspend fun otpVerifySecretCodeRequest(
        @Field("countryCode")countryCode :String ,
        @Field("phone") phone :String,
        @Field("otp") otp :String,
        @Field("user_type") apiType :String
    ) : Response<JsonObject>


    @POST("send_otp")
    @FormUrlEncoded
    suspend fun resendOtp(@Field("phone")phone :String,
                        @Field("user_type")userType :String?,
                        @Field("countryCode")countryCode :String ,
                        @Field("apiType") apiType :String,
    ) :Response<JsonObject>


  }
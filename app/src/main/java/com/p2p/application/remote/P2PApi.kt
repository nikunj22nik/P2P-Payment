package com.p2p.application.remote

import com.google.gson.JsonObject
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




  }
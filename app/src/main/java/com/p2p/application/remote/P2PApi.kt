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
    suspend fun sendOtp(@Field("phone")phone :String,@Field("user_type")userType :String,
                      @Field("country_code")countryCode :String ,@Field("apiType") apiType :String,
                      ) :Response<JsonObject>


    @GET("get_country")
    suspend fun countryRequest() :Response<JsonObject>

  }
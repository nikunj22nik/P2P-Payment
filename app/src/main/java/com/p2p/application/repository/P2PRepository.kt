package com.p2p.application.repository

import com.google.gson.JsonObject
import com.p2p.application.di.NetworkResult
import com.p2p.application.model.countrymodel.CountryModel
import com.p2p.application.util.AppConstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.http.Field

interface P2PRepository {
    suspend fun sendOtp(@Field("phone")phone :String,@Field("user_type")userType :String,
                        @Field("country_code")countryCode :String ,@Field("apiType") apiType :String,
    ) :Flow<NetworkResult<String>>


    suspend fun countryRequest() :Flow<NetworkResult<CountryModel>>




}
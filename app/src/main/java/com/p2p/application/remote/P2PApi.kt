package com.p2p.application.remote

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


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

    @POST("get_balance")
    suspend fun balanceRequest() :Response<JsonObject>

    @POST("get_user_max_balance")
    suspend fun accountLimitRequest() :Response<JsonObject>

    @POST("logout")
    suspend fun logOutApi() :Response<JsonObject>


    @POST("switch_account")
    @FormUrlEncoded
    suspend fun switchUserApiRequest(@Field("user_id")userId :String,
                                     @Field("user_type")userType :String?,
                                     @Field("phone")phone :String ,
                                     @Field("fcm_token") fcmToken :String,) :Response<JsonObject>

    @POST("get_user_all_accounts")
    suspend fun userAccountList() :Response<JsonObject>

    @POST("logout")
    suspend fun deleteApi() :Response<JsonObject>


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


    @POST("merchant_verification")
    @Multipart
    suspend fun merchantVerification(
        @Part businessIdList : ArrayList<MultipartBody.Part>?,
        @Part list : ArrayList<MultipartBody.Part>?,
        @Part pdfList : ArrayList<MultipartBody.Part>?,
        @Part profileImage: MultipartBody.Part?,
        @Part("user_type") userType : RequestBody
    ) : Response<JsonObject>

    @POST("get_transaction_history")
    @FormUrlEncoded
    suspend fun getTransactionHistory(
        @Field("page")page:Int,

        @Field("limit") limit:Int
    ) : Response<JsonObject>

    @POST("get_one_to_one_transaction_history")
    @FormUrlEncoded
    suspend fun genOneToOneTransactionHistory(
        @Field("user_id") userId :Int
    ) : Response<JsonObject>

  }
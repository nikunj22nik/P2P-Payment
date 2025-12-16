package com.p2p.application.di


import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.p2p.application.remote.P2PApi
import com.p2p.application.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.p2p.application.repository.P2PRepository
import com.p2p.application.repository.P2PRepositoryImpl
import com.p2p.application.util.AuthInterceptor


@Module
@InstallIn(SingletonComponent::class)
object  NetworkModule {

    @Provides
    fun p2pConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    @Singleton
    @Provides
    fun p2pApi(retrofit: Retrofit.Builder, okHttpClient: OkHttpClient): P2PApi {
        return  retrofit.client(okHttpClient).build().create(P2PApi::class.java)
    }

    @Provides
    @Singleton
    fun p2pRepository(api: P2PApi): P2PRepository {
        return P2PRepositoryImpl(api)
    }

    @Singleton
    @Provides
    fun p2pAuthInterceptor(@ApplicationContext context: Context): AuthInterceptor =
        AuthInterceptor(context)



    @Singleton
    @Provides
    fun p2pOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message -> Log.d("RetrofitLog", message) }
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }else{
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val requestBody = request.body
                val bodyString = StringBuilder()

                if (requestBody is okhttp3.FormBody) {
                    for (i in 0 until requestBody.size) {
                        bodyString.append(requestBody.name(i))
                            .append(" = ")
                            .append(requestBody.value(i))
                            .append("\n")
                    }
                }

                Log.d("@@@@@@@@", """
        REQUEST →
        URL: ${request.url}
        METHOD: ${request.method}
        HEADERS: ${request.headers}
        FIELDS:
        ${if (bodyString.isNotEmpty()) bodyString.toString() else "NO FIELDS"}
    """.trimIndent())


                val response = chain.proceed(chain.request())
                val responseBody = response.peekBody(Long.MAX_VALUE).string()
                Log.d("@@@@@@@@", "response body:\n$responseBody")
                if (response.code == 401) {
                    SessionEventBus.emitSessionExpired()
                }
                response
            }.connectTimeout(60,java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60,java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60,java.util.concurrent.TimeUnit.SECONDS)
            .build()

    }

    @Singleton
    @Provides
    fun p2pRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())


}
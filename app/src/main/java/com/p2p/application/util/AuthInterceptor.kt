package com.p2p.application.util

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.edit
import com.p2p.application.activity.SplashActivity


import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val context: Context): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val sessionManager = SessionManager(context)
        val originalRequest = chain.request()

        val token = sessionManager.getAuthToken()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Accept", "application/json")

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        if (response.code == 401) {
            response.close()
            handleTokenExpiration(sessionManager)
        }

        return response
    }




    private fun handleTokenExpiration(sessionManager: SessionManager) {
        var intent  = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        sessionManager.clearSession()
        context.startActivity(intent)
    }

}

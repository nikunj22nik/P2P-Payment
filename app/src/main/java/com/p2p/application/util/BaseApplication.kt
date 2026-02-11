package com.p2p.application.util

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging

import dagger.hilt.android.HiltAndroidApp
import kotlin.String

@HiltAndroidApp
class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        AppContextProvider.initialize(this)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val localeManager = getSystemService(android.app.LocaleManager::class.java)
            localeManager.applicationLocales =
                android.os.LocaleList.forLanguageTags(
                    if (AppConfig.USE_FRENCH) "fr" else "en"
                )
        }

        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseInstallations.getInstance().id
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                     Log.w("FIS", "getId failed", task.exception)
                   return@addOnCompleteListener
                }
                Log.d("FIS", "Installation ID: " + task.result)
            }

    }

    override fun attachBaseContext(base: Context) {
        val context = LocaleHelper.applyLanguage(base)
        super.attachBaseContext(context)
    }
}
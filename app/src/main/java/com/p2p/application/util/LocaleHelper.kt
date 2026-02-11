package com.p2p.application.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object AppConfig {
    const val USE_FRENCH = true
}

object LocaleHelper {

    fun applyLanguage(context: Context): Context {
        val languageCode = if (AppConfig.USE_FRENCH) "fr" else "en"

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)

        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}



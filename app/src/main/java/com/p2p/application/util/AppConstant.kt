package com.p2p.application.util

import kotlin.math.floor

class AppConstant {

    companion object {
        const val LOGIN_SESSION:String="login_session"
        const val IMAGE_BASE_URL ="https://alertapp.tgastaging.com/storage/"
        const val LOGIN_TYPE:String="login_type"
        const val FORGOT_TYPE:String="forgotCode"
        const val PINT_TYPE:String="isPin"
        const val IS_LOGIN:String="isLogin"
        const val BUSINESS_ID :String ="businessId"
        const val BUSINESS_REGISTER :String ="business_register"
        const val TAX_ID :String ="tax_id"
        const val PROFILE :String ="profile"
        const val AuthToken :String ="AuthToken"
        const val USER:String="User"
        const val MERCHANT:String="Merchant"
        const val AGENT:String="Agent"
        const val MASTER_AGENT:String="Master Agent"
        const val unKnownError = "There was an unknown error. Check your connection, and try again."
        const val serverError = "There was an unknown error. Check your connection, and try again."
        const val NAME ="name"
        const val LASTNAME ="last_name"
        const val phoneNumber ="phone_number"
        const val BACK_TO_LOGIN ="Back to Login"
        const val BACK_TO_HOME ="Back to Home"
        const val TRY_AGAIN ="Try Again"
        const val SCREEN_TYPE ="Screen Type"
        const val QR ="qr"
        const val SESSION_ERROR:String="Your session has expired. Please log in again to continue."
        fun mapperType(value: String?): String {
            return when (value) {
                USER -> "user"
                AGENT -> "agent"
                MERCHANT -> "merchant"
                MASTER_AGENT -> "master_agent"
                else -> "unknown"
            }
        }

        fun roundHalfUp(value: Double?): Int {
            return value?.let {
                floor(it + 0.5).toInt()
            } ?: 0
        }

        fun roundHalfUpStr(value: String?): Int {
            return value
                ?.toDoubleOrNull()
                ?.let { floor(it + 0.5).toInt() }
                ?: 0
        }

    }
}


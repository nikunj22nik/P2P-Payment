package com.p2p.application.util

class AppConstant {

    companion object {
        const val LOGIN_SESSION:String="login_session"
        const val LOGIN_TYPE:String="login_type"
        const val FORGOT_TYPE:String="forgotCode"
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
        val unKnownError = "There was an unknown error. Check your connection, and try again."
        val serverError = "There was an unknown error. Check your connection, and try again."
        val NAME ="name"
        val LASTNAME ="last_name"
        val phoneNumber ="phone_number"

        fun mapperType(value: String?): String {
            return when (value) {
                USER -> "user"
                AGENT -> "agent"
                MERCHANT -> "merchant"
                MASTER_AGENT -> "master_agent"
                else -> "unknown"
            }
        }


    }
}
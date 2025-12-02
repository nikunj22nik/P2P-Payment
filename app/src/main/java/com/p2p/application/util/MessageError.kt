package com.p2p.application.util

class MessageError {
    companion object {

        // set the session value when user click the type of select
        const val USER:String="User"
        const val MERCHANT:String="Merchant"
        const val AGENT:String="Agent"
        const val MASTER_AGENT:String="Master Agent"


        // Show Error sms
        const val SELECT_TYPE:String="Please select the type"
        const val NETWORK_ERROR:String="Please check your internet connection."
        const val PHONE_NUMBER:String="Phone Can't be empty"
        const val OTP_NUMBER:String="OTP Can't be empty"
        const val OTP_NOT_MATCH:String ="Otp Not Matched"
    }
}
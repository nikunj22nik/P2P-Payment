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
        const val NAME_ERROR:String="The name must be at least 3 characters long."
        const val NUMBER_VALIDATION:String="Please Enter a Valid Phone Number"
        const val OTP_NUMBER:String="OTP Can't be empty"
        const val OTP_NOT_MATCH:String ="Otp Not Matched"
        const val SECRET_CODE:String ="Code Can't be empty"
        const val CODE_NOT_MATCH:String ="Code Not Matched"
        const val UPLOAD_BUSINESS_ID ="Please Upload Business Id."
        const val UPLOAD_BUSINESS_Register ="Please Upload Business Registration Document."
        val UPLOAD_TAX_ID ="Please Upload Your Tax Verification Document."
        val UPLOAD_BUSINESS_LOGO= "Please Upload Your Business logo."
    }
}
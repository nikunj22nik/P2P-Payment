package com.p2p.application.util


import com.p2p.application.R
class MessageError {
    companion object {

        // set the session value when user click the type of select
 /*       const val USER:String="User"
        const val MERCHANT:String="Merchant"
        const val AGENT:String="Agent"
        const val MASTER_AGENT:String="Master Agent"
        const val INVALID_SECRET:String ="The secret code you entered is invalid."
        const val AMOUNT_NULL ="Receiver info or amount is null"
        const val INVALID_AMOUNT="Please Enter Valid Amount"
        const val showQRError ="Oops! We couldn’t locate a merchant account with that ID."
        // Show Error sms
        const val SELECT_TYPE:String="Please select the type"
        const val NETWORK_ERROR:String="Please check your internet connection."
        const val AMOUNT_ERROR:String="Insufficient amount"
        const val PHONE_NUMBER:String="You must enter a phone number to proceed"
        const val NAME_ERROR:String="The name must be at least 3 characters long."
        const val NUMBER_VALIDATION:String="Please Enter a Valid Phone Number"
        const val OTP_NUMBER:String="OTP Can't be empty"
        const val AMOUNT__ERROR:String="Amount Can't be empty"
        const val AMOUNT_CNF_ERROR:String="Confirm Amount Can't be empty"
        const val AMOUNT_MATCH_ERROR:String="Amount Not Matched"
        const val OTP_NOT_MATCH:String ="Incorrect OTP. Please try again."
        const val SECRET_CODE:String ="Code Can't be empty"
        const val CODE_NOT_MATCH:String ="Code Not Matched"
        const val UPLOAD_BUSINESS_ID ="Please Upload Business Id."
        const val UPLOAD_BUSINESS_Register ="Please Upload Business Registration Document."
        val UPLOAD_TAX_ID ="Please Upload Your Tax Verification Document."
        val UPLOAD_BUSINESS_LOGO= "Please Upload Your Business logo."
        val FRONT_SMS= "Please upload front image."
        val BACK_SMS= "Please upload back image."

        */





                // User Types
      /*  val USER = AppContextProvider.getContext().getString(R.string.type_user)
                val MERCHANT = AppContextProvider.getContext().getString(R.string.type_merchant)
                val AGENT = AppContextProvider.getContext().getString(R.string.type_agent)
                val MASTER_AGENT = AppContextProvider.getContext().getString(R.string.type_master_agent)*/

              const val USER:String="User"
        const val MERCHANT:String="Merchant"
        const val AGENT:String="Agent"
        const val MASTER_AGENT:String="Master Agent"
                // Validation Errors
                val INVALID_SECRET = AppContextProvider.getContext().getString(R.string.invalid_secret)
                val AMOUNT_NULL = AppContextProvider.getContext().getString(R.string.amount_null)
                val INVALID_AMOUNT =AppContextProvider.getContext().getString( R.string.invalid_amount)
                val showQRError = AppContextProvider.getContext().getString(R.string.qr_error)
                val SELECT_TYPE =AppContextProvider.getContext().getString( R.string.select_type)
                val NETWORK_ERROR =AppContextProvider.getContext().getString( R.string.network_error)
                val AMOUNT_ERROR =AppContextProvider.getContext().getString( R.string.amount_error)
                val PHONE_NUMBER = AppContextProvider.getContext().getString(R.string.phone_number_required)
                val NAME_ERROR = AppContextProvider.getContext().getString(R.string.name_error)
                val NUMBER_VALIDATION = AppContextProvider.getContext().getString(R.string.number_validation)
                val OTP_NUMBER =AppContextProvider.getContext().getString( R.string.otp_empty)
                val AMOUNT__ERROR = AppContextProvider.getContext().getString(R.string.amount_empty)
                val AMOUNT_CNF_ERROR = AppContextProvider.getContext().getString(R.string.amount_confirm_empty)
                val AMOUNT_MATCH_ERROR = AppContextProvider.getContext().getString(R.string.amount_match_error)
                val OTP_NOT_MATCH = AppContextProvider.getContext().getString(R.string.otp_not_match)
                val SECRET_CODE = AppContextProvider.getContext().getString(R.string.secret_code_empty)
                val CODE_NOT_MATCH = AppContextProvider.getContext().getString(R.string.code_not_match)

                // Upload Errors
                val UPLOAD_BUSINESS_ID = AppContextProvider.getContext().getString(R.string.upload_business_id)
                val UPLOAD_BUSINESS_Register = AppContextProvider.getContext().getString(R.string.upload_business_register)
                val UPLOAD_TAX_ID = AppContextProvider.getContext().getString(R.string.upload_tax_id)
                val UPLOAD_BUSINESS_LOGO = AppContextProvider.getContext().getString(R.string.upload_business_logo1)
                val FRONT_SMS = AppContextProvider.getContext().getString(R.string.upload_front_image)
                val BACK_SMS = AppContextProvider.getContext().getString(R.string.upload_back_image)

                // Help
                const val HELP_NUMBER="0123456789"




    }
}
package com.p2p.application.model

data class LoginUserModel(
    val id: Int,
    val role: String,
    val first_name: String?,
    val last_name: String?,
    val phone: String?,
    val otp: String?,
    val qr_code: String?,
    val mpin: String?,
    val email_verified_at: String?,
    val fcm_token: String?,
    val user_status: Int,
    val verification_status: Int,
    val business_logo: String?,
    val business_registration_docs: String?,
    val business_registration_certificate: String?,
    val tax_identification_docs: String?,
    val created_at: String?,
    val updated_at: String?,
    val deleted_at: String?,
    val verification_docs_upload_status:Int,
    val reject_reason:String =""
)

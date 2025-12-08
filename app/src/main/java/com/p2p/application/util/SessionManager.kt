package com.p2p.application.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(var context: Context) {

    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null


    init {
        pref = context.getSharedPreferences(AppConstant.LOGIN_SESSION, Context.MODE_PRIVATE)
        editor = pref?.edit()
    }

    fun setLoginType(data: String){
        editor?.putString(AppConstant.LOGIN_TYPE, data)
        editor?.commit()
    }

    fun setIsLogin(data: Boolean){
        editor?.putBoolean(AppConstant.IS_LOGIN, data)
        editor?.commit()
    }

    fun sessionClear() {
        editor?.apply()
        editor?.clear()
        editor?.commit()
    }

    fun setScreenType(data: String){
        editor?.putString(AppConstant.FORGOT_TYPE, data)
        editor?.commit()
    }

    fun setIsWelcome(value: Boolean) {
        editor?.putBoolean("WELCOME", value)
        editor?.apply()
    }

    fun setIsPin(value: Boolean) {
        editor?.putBoolean(AppConstant.PINT_TYPE, value)
        editor?.apply()
    }

    fun getIsWelcome(): Boolean {
        return pref?.getBoolean("WELCOME", false) ?: false
    }

    fun getIsPin(): Boolean {
        return pref?.getBoolean(AppConstant.PINT_TYPE, false) ?: false
    }


    fun setFirstName(name :String){
        editor?.putString(AppConstant.NAME, name)
        editor?.commit()
    }

    fun setLastName(lastName:String){
        editor?.putString(AppConstant.LASTNAME, lastName)
        editor?.commit()
    }

    fun setPhoneNumber(phone:String){
        editor?.putString(AppConstant.phoneNumber, phone)
        editor?.commit()
    }

    fun getPhoneNumber():String{
        return pref?.getString(AppConstant.phoneNumber, "")?:""
    }

    fun getFirstName(): String {
        return pref?.getString(AppConstant.NAME, "")?:""
    }

    fun getLastName(): String {
        return pref?.getString(AppConstant.LASTNAME, "")?:""
    }



    fun getLoginType(): String? {
        return pref?.getString(AppConstant.LOGIN_TYPE, "")
    }

    fun getScreenType(): String? {
        return pref?.getString(AppConstant.FORGOT_TYPE, "")
    }

    fun getIsLogin(): Boolean?{
        return pref?.getBoolean(AppConstant.IS_LOGIN, false)
    }

    fun getAuthToken():String?{
        return pref?.getString(AppConstant.AuthToken,"")
    }

    fun setAuthToken(token:String){
        pref?.edit {
            putString(AppConstant.AuthToken, token)
        }
    }

    fun clearSession() {
        pref?.edit {
            clear()
            apply() // this is optional; the extension applies automatically
        }
    }



}
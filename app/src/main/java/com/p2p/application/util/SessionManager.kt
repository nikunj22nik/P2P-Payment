package com.p2p.application.util

import android.content.Context
import android.content.SharedPreferences

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


    fun getLoginType(): String? {
        return pref?.getString(AppConstant.LOGIN_TYPE, "")
    }


}
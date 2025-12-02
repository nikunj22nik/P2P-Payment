package com.p2p.application.Error

import android.util.Log
import com.p2p.application.util.AppConstant
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

object ErrorHandler {


    fun emitError(e: Exception) :String {
        when (e) {
            is IOException -> {
                Log.e("ERROR", "IO exception: ${e.message} :: ${e.localizedMessage}")
                return e.message ?: AppConstant.unKnownError

            }
            else -> {
                Log.e("ERROR", "Unexpected error: ${e.message} :: ${e.stackTraceToString()}")
                return e.message ?: AppConstant.unKnownError
            }
        }
    }


    fun handleErrorBody(errorBody: String?) :String {
        try {
            if (errorBody.isNullOrEmpty()) {
                return   AppConstant.unKnownError
            }
            val jsonObj = JSONObject(errorBody)
            val errorMessage = jsonObj.optString("message", AppConstant.unKnownError)
            return errorMessage

        } catch (e: JSONException) {
            e.printStackTrace()
            return AppConstant.unKnownError
        }
    }

}
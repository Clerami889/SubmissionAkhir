package com.clerami.intermediate.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.clerami.intermediate.data.remote.response.LoginResponse
import com.clerami.intermediate.data.remote.response.LoginResult
import com.google.gson.Gson

object SessionManager {

    private const val PREF_NAME = "user_session"
    private const val KEY_LOGIN_RESULT = "login_result"
    private const val KEY_ERROR = "error"
    private const val KEY_MESSAGE = "message"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    fun saveSession(context: Context, loginResponse: LoginResponse) {
        val editor = getPreferences(context).edit()
        val loginResultJson = Gson().toJson(loginResponse.loginResult)
        editor.putString(KEY_LOGIN_RESULT, loginResultJson)
        editor.putBoolean(KEY_ERROR, loginResponse.error ?: false)
        editor.putString(KEY_MESSAGE, loginResponse.message)
        editor.apply()

        Log.d("SessionManager", "Session saved. loginResult: $loginResultJson, error: ${loginResponse.error}, message: ${loginResponse.message}")
    }


    fun isLoggedIn(context: Context): Boolean {
        val loginResultJson = getPreferences(context).getString(KEY_LOGIN_RESULT, null)
        return loginResultJson != null
    }

    fun getToken(context: Context): String? {
        val loginResultJson = getPreferences(context).getString(KEY_LOGIN_RESULT, null)
        return try {
            if (loginResultJson != null) {
                val loginResult = Gson().fromJson(loginResultJson, LoginResult::class.java)
                loginResult.token
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getLoginResult(context: Context): LoginResult? {
        val loginResultJson = getPreferences(context).getString(KEY_LOGIN_RESULT, null)
        return try {
            if (loginResultJson != null) {
                Gson().fromJson(loginResultJson, LoginResult::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }



    fun clearSession(context: Context) {
        val editor = getPreferences(context).edit()
        editor.clear()
        editor.apply()
    }

}

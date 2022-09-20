package com.example.grayscale3.Network

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.grayscale.R

class SessionManager(context: Context) {
    //Normal Shared Preferences
    private var perfs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)!!



    companion object {
        const val USER_TOKEN = "access_token"
    }

    //To save Token
    fun saveToken(token: String){
        val editor = perfs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }
    //To fetch Token
    fun fetchToken(): String? {
        return perfs.getString(USER_TOKEN, null)
    }

    fun deleteToken(){
        perfs.edit().clear().apply()
    }


}
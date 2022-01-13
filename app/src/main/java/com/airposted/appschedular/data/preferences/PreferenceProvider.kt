package com.airposted.appschedular.data.preferences

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext

    private val sharedPreferences: SharedPreferences = appContext.getSharedPreferences("MySharedPref",MODE_PRIVATE)


    fun saveSharedPreferences(key: String, data: String) {
        sharedPreferences.edit().putString(
            key,
            data
        ).apply()
    }

    fun getSharedPreferences(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun deleteSharedPreferences(key: String) {
        sharedPreferences.edit().remove(
            key
        ).apply()
    }
}
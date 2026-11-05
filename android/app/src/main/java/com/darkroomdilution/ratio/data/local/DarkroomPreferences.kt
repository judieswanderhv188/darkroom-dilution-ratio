package com.darkroomdilution.ratio.data.local

import android.content.Context
import android.content.SharedPreferences

class DarkroomPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("darkroom_dilution_prefs", Context.MODE_PRIVATE)

    fun readString(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun readInt(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun readFloat(key: String, fallback: Float): Float = prefs.getFloat(key, fallback)

    fun writeFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}

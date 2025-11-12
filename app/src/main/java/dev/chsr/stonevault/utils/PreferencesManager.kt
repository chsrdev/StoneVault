package dev.chsr.stonevault.utils

import android.content.Context
import android.util.Base64
import androidx.core.content.edit

class PreferencesManager(private val context: Context) {
    private val sharedPreferencesName = "settings"
    private val sharedPreferences = context.getSharedPreferences(sharedPreferencesName, 0)

    fun saveString(key: String, value: String) {
        sharedPreferences.edit {
            putString(key, value)
        }
    }

    fun readString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveByteArray(key: String, value: ByteArray) {
        val encoded = Base64.encodeToString(value, Base64.NO_WRAP)
        saveString(key, encoded)
    }

    fun readByteArray(key: String, defaultValue: ByteArray): ByteArray {
        val encodedDefault = Base64.encodeToString(defaultValue, Base64.NO_WRAP)
        val encodedValue = readString(key, encodedDefault)
        return Base64.decode(encodedValue, Base64.NO_WRAP)
    }
}
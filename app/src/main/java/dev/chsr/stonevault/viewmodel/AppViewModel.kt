package dev.chsr.stonevault.viewmodel

import android.app.Application
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.chsr.stonevault.utils.PreferencesManager
import javax.crypto.spec.SecretKeySpec

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val saltKey = "salt"
    private val vectorKey = "vector"
    private val vectorIvKey = "vectorIv"
    private val encryptedVectorKey = "encryptedVector"
    private val preferencesManager = PreferencesManager(application)
    private val emptyByteArray = ByteArray(0)

    fun getSalt(): ByteArray = preferencesManager.readByteArray(saltKey, emptyByteArray)
    fun getVector(): ByteArray = preferencesManager.readByteArray(vectorKey, emptyByteArray)
    fun getVectorIv(): ByteArray {
        val encodedIv = preferencesManager.readString(vectorIvKey, "")
        return if (encodedIv.isNotEmpty()) {
            Base64.decode(encodedIv, Base64.NO_WRAP)
        } else {
            ByteArray(0)
        }
    }
    fun getEncryptedVector(): String = preferencesManager.readString(encryptedVectorKey, "")

    fun setSalt(value: ByteArray) = preferencesManager.saveByteArray(saltKey, value)
    fun setVector(value: ByteArray) = preferencesManager.saveByteArray(vectorKey, value)
    fun setVectorIv(value: ByteArray) = preferencesManager.saveByteArray(vectorIvKey, value)
    fun setEncryptedVector(value: String) = preferencesManager.saveString(encryptedVectorKey, value)

    class AppViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AppViewModel::class.java)) {

                return AppViewModel(application) as T

            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
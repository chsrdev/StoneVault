package dev.chsr.stonevault.security

import android.os.Handler
import android.os.Looper
import javax.crypto.spec.SecretKeySpec

object SessionManager {
    private var _secretKey: SecretKeySpec? = null

    fun setKey(key: SecretKeySpec) {
        _secretKey = key
    }

    fun getKey(): SecretKeySpec {
        return _secretKey
            ?: throw IllegalStateException("Session expired")
    }

    fun clear() {
        _secretKey = null
    }

    fun isInitialized() = _secretKey != null
}
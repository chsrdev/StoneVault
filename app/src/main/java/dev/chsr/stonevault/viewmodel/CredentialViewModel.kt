package dev.chsr.stonevault.viewmodel

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.chsr.stonevault.database.AppDatabase
import dev.chsr.stonevault.entity.Credential
import dev.chsr.stonevault.entity.DecodedCredential
import dev.chsr.stonevault.security.SessionManager
import dev.chsr.stonevault.utils.AES
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CredentialViewModel(db: AppDatabase) : ViewModel() {
    private val credentialDao = db.credentialDao()

    private val _credentials = MutableStateFlow<List<Credential>>(emptyList())
    val credentials = _credentials.asStateFlow()

    init {
        loadCredentials()
    }

    private fun getKey(): SecretKeySpec = SessionManager.getKey()

    fun loadCredentials() {
        viewModelScope.launch {
            _credentials.value = credentialDao.getAll()
        }
    }

    fun getCredentialById(id: Int): Credential? {
        return _credentials.value.firstOrNull { it.id == id }
    }

    fun getDecodedCredentialById(id: Int): DecodedCredential? {
        val credential = getCredentialById(id) ?: return null

        return DecodedCredential(
            id = credential.id,
            title = decode(credential.title, credential.titleIv),
            password = decode(credential.password, credential.passwordIv),
            email = decode(credential.email, credential.emailIv),
            notes = decode(credential.notes, credential.notesIv)
        )
    }

    fun getDecodedCredentials(): List<DecodedCredential> {
        return _credentials.value.mapNotNull {
            getDecodedCredentialById(it.id)
        }
    }

    fun addCredential(decodedCredential: DecodedCredential) {
        val key = getKey()
        val titleIv = AES.generateIV()
        val passwordIv = AES.generateIV()
        val emailIv = AES.generateIV()
        val notesIv = AES.generateIV()

        val credential = Credential(
            title = AES.encode(decodedCredential.title, key, titleIv),
            titleIv = Base64.encodeToString(titleIv.iv, Base64.NO_WRAP),
            password = AES.encode(decodedCredential.password, key, passwordIv),
            passwordIv = Base64.encodeToString(passwordIv.iv, Base64.NO_WRAP),
            email = AES.encode(decodedCredential.email, key, emailIv),
            emailIv = Base64.encodeToString(emailIv.iv, Base64.NO_WRAP),
            notes = AES.encode(decodedCredential.notes, key, notesIv),
            notesIv = Base64.encodeToString(notesIv.iv, Base64.NO_WRAP)
        )

        viewModelScope.launch {
            credentialDao.insertAll(credential)
            loadCredentials()
        }
    }

    fun updateCredential(decodedCredential: DecodedCredential) {
        val id = decodedCredential.id ?: return
        val original = getCredentialById(id) ?: return
        val key = getKey()

        val updated = Credential(
            id = original.id,
            title = AES.encode(decodedCredential.title, key, ivFromBase64(original.titleIv)),
            titleIv = original.titleIv,
            password = AES.encode(decodedCredential.password, key, ivFromBase64(original.passwordIv)),
            passwordIv = original.passwordIv,
            email = AES.encode(decodedCredential.email, key, ivFromBase64(original.emailIv)),
            emailIv = original.emailIv,
            notes = AES.encode(decodedCredential.notes, key, ivFromBase64(original.notesIv)),
            notesIv = original.notesIv
        )

        viewModelScope.launch {
            credentialDao.update(updated)
            loadCredentials()
        }
    }

    fun deleteCredential(id: Int) {
        viewModelScope.launch {
            credentialDao.delete(id)
            loadCredentials()
        }
    }

    fun clear() {
        viewModelScope.launch {
            credentialDao.clear()
            loadCredentials()
        }
    }

    fun reEncryptAll(newKey: SecretKeySpec) {
        viewModelScope.launch {
            val oldKey = getKey()
            val decoded = getDecodedCredentials()

            credentialDao.clear()

            decoded.forEach { credential ->
                val titleIv = AES.generateIV()
                val passwordIv = AES.generateIV()
                val emailIv = AES.generateIV()
                val notesIv = AES.generateIV()

                credentialDao.insertAll(
                    Credential(
                        id = credential.id!!,
                        title = AES.encode(credential.title, newKey, titleIv),
                        titleIv = Base64.encodeToString(titleIv.iv, Base64.NO_WRAP),
                        password = AES.encode(credential.password, newKey, passwordIv),
                        passwordIv = Base64.encodeToString(passwordIv.iv, Base64.NO_WRAP),
                        email = AES.encode(credential.email, newKey, emailIv),
                        emailIv = Base64.encodeToString(emailIv.iv, Base64.NO_WRAP),
                        notes = AES.encode(credential.notes, newKey, notesIv),
                        notesIv = Base64.encodeToString(notesIv.iv, Base64.NO_WRAP)
                    )
                )
            }

            SessionManager.setKey(newKey)
            loadCredentials()
        }
    }

    private fun decode(text: String, ivBase64: String): String {
        return AES.decode(text, getKey(), ivFromBase64(ivBase64))
    }

    private fun ivFromBase64(ivBase64: String): IvParameterSpec {
        return IvParameterSpec(Base64.decode(ivBase64, Base64.NO_WRAP))
    }

    class CredentialViewModelFactory(
        private val db: AppDatabase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CredentialViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CredentialViewModel(db) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
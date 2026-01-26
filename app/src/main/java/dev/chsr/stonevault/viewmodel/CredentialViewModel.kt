package dev.chsr.stonevault.viewmodel

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.chsr.stonevault.utils.AES
import dev.chsr.stonevault.database.AppDatabase
import dev.chsr.stonevault.entity.Credential
import dev.chsr.stonevault.entity.DecodedCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CredentialViewModel(private val db: AppDatabase, val secretKey: SecretKeySpec) : ViewModel() {
    private val credentialDao = db.credentialDao()

    private val _credentials = MutableStateFlow<List<Credential>>(emptyList())
    val credentials = _credentials.asStateFlow()

    init {
        loadCredentials()
    }

    fun loadCredentials() {
        viewModelScope.launch {
            _credentials.value = credentialDao.getAll()
        }
    }

    fun getCredentialById(id: Int): Credential? {
        return credentials.value.first { it.id == id }
    }

    fun getDecodedCredentialById(id: Int): DecodedCredential? {
        val credential = getCredentialById(id) ?: return null
        return DecodedCredential(
            credential.id,
            decode(credential.title, Base64.decode(credential.titleIv, Base64.NO_WRAP)),
            decode(credential.password, Base64.decode(credential.passwordIv, Base64.NO_WRAP)),
            decode(credential.email, Base64.decode(credential.emailIv, Base64.NO_WRAP)),
            decode(credential.notes, Base64.decode(credential.notesIv, Base64.NO_WRAP))
        )
    }

    fun updateCredential(decodedCredential: DecodedCredential) {
        if (decodedCredential.id == null)
            return

        val original = getCredentialById(decodedCredential.id) ?: return

        updateCredential(
            Credential(
                original.id,
                encode(decodedCredential.title, original.titleIv),
                original.titleIv,
                encode(decodedCredential.password, original.passwordIv),
                original.passwordIv,
                encode(decodedCredential.email, original.emailIv),
                original.emailIv,
                encode(decodedCredential.notes, original.notesIv),
                original.notesIv
            )
        )
        loadCredentials()
    }

    fun updateCredential(credential: Credential) {
        viewModelScope.launch {
            credentialDao.update(credential)
            loadCredentials()
        }
    }

    fun addCredential(credential: Credential) {
        viewModelScope.launch {
            credentialDao.insertAll(credential)
            loadCredentials()
        }
    }

    fun addCredential(decodedCredential: DecodedCredential) {
        val titleIv = AES.generateIV()
        val passwordIv = AES.generateIV()
        val emailIv = AES.generateIV()
        val notesIv = AES.generateIV()

        addCredential(
            Credential(
                title = encode(decodedCredential.title, titleIv),
                titleIv = Base64.encodeToString(titleIv.iv, Base64.NO_WRAP),
                password = encode(decodedCredential.password, passwordIv),
                passwordIv = Base64.encodeToString(passwordIv.iv, Base64.NO_WRAP),
                email = encode(decodedCredential.email, emailIv),
                emailIv = Base64.encodeToString(emailIv.iv, Base64.NO_WRAP),
                notes = encode(decodedCredential.notes, notesIv),
                notesIv = Base64.encodeToString(notesIv.iv, Base64.NO_WRAP)
            )
        )
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

    private fun encode(text: String, iv: IvParameterSpec): String = AES.encode(text, secretKey, iv)

    private fun decode(text: String, iv: ByteArray): String =
        AES.decode(text, secretKey, IvParameterSpec(iv))
    private fun encode(text: String, iv: String): String =
        AES.encode(text, secretKey, IvParameterSpec(Base64.decode(iv, Base64.NO_WRAP)))

    class CredentialViewModelFactory(
        private val db: AppDatabase,
        private val secretKey: SecretKeySpec
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CredentialViewModel::class.java)) {

                return CredentialViewModel(db, secretKey) as T

            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
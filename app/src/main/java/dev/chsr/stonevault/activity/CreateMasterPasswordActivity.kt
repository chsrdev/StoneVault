package dev.chsr.stonevault.activity

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import dev.chsr.stonevault.utils.AES
import dev.chsr.stonevault.R
import dev.chsr.stonevault.activity.component.MasterPasswordTextField
import dev.chsr.stonevault.screen.updateLocale
import dev.chsr.stonevault.ui.theme.StoneVaultTheme
import dev.chsr.stonevault.utils.PreferencesManager
import dev.chsr.stonevault.viewmodel.AppViewModel
import java.security.SecureRandom
import java.util.Locale
import javax.crypto.spec.SecretKeySpec

class CreateMasterPasswordActivity : AppCompatActivity() {
    val viewModel: AppViewModel by viewModels {
        AppViewModel.AppViewModelFactory(application)
    }
    val argon2Kt = Argon2Kt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(applicationContext)
          val savedCode = preferencesManager.readString("language", Locale.getDefault().language)
        updateLocale(savedCode)

        enableEdgeToEdge()
        setContent {
            StoneVaultTheme {
                val masterPasswordValue = remember { mutableStateOf("") }
                val confirmMasterPasswordValue = remember { mutableStateOf("") }
                val isWrongPasswordInput = remember { mutableStateOf(false) }
                val haptic = LocalHapticFeedback.current

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(modifier = Modifier.imePadding()) {
                            MasterPasswordTextField(
                                isWrongPasswordInput,
                                masterPasswordValue,
                                stringResource(R.string.create_master_password)
                            )
                            MasterPasswordTextField(
                                isWrongPasswordInput,
                                confirmMasterPasswordValue,
                                stringResource(R.string.confirm_master_password)
                            )

                            Button(
                                modifier = Modifier
                                    .padding(top = 32.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    if (masterPasswordValue.value == confirmMasterPasswordValue.value && masterPasswordValue.value.isNotEmpty()) {
                                        val key = saveFirstEnter(masterPasswordValue.value)
                                        startActivity(
                                            Intent(
                                                applicationContext,
                                                MainActivity::class.java
                                            ).apply {
                                                putExtra("SECRET_KEY", key.encoded)
                                            }
                                        )
                                    } else {
                                        haptic.performHapticFeedback(HapticFeedbackType.Reject)
                                        isWrongPasswordInput.value = true
                                    }
                                }) {
                                Text(stringResource(R.string.done))
                            }
                        }
                    }
                }
            }
        }
    }

    fun saveFirstEnter(masterPassword: String): SecretKeySpec {
        val salt = SecureRandom().generateSeed(16)
        val verifyVector = SecureRandom().generateSeed(16)
        val verifyVectorString =
            Base64.encodeToString(verifyVector, Base64.NO_WRAP)
        val verifyVectorIv = AES.generateIV()

        val hashResult: Argon2KtResult = argon2Kt.hash(
            mode = Argon2Mode.ARGON2_I,
            password = masterPassword.toByteArray(),
            salt = salt
        )
        val secretKey =
            SecretKeySpec(hashResult.rawHashAsByteArray(), "AES")

        val encryptedVerifyVector = AES.encode(
            verifyVectorString, secretKey, verifyVectorIv
        )

        viewModel.setSalt(salt)
        viewModel.setVector(verifyVector)
        viewModel.setVectorIv(verifyVectorIv.iv)
        viewModel.setEncryptedVector(encryptedVerifyVector)

        return secretKey
    }
}
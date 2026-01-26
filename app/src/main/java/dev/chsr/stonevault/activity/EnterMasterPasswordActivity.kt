package dev.chsr.stonevault.activity

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import java.util.Locale
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.getValue

class EnterMasterPasswordActivity : AppCompatActivity() {
    val viewModel: AppViewModel by viewModels {
        AppViewModel.AppViewModelFactory(application)
    }
    val argon2Kt = Argon2Kt()
    var secretKey: SecretKeySpec? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferencesManager = PreferencesManager(applicationContext)
        val savedCode = preferencesManager.readString("language", Locale.getDefault().language)
        updateLocale(savedCode)

        enableEdgeToEdge()
        setContent {
            StoneVaultTheme {
                val masterPasswordValue = remember { mutableStateOf("") }
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
                                stringResource(R.string.enter_master_password)
                            )
                            Button(
                                modifier = Modifier
                                    .padding(top = 32.dp)
                                    .align(Alignment.CenterHorizontally),
                                onClick = {
                                    if (verifyMasterPassword(masterPasswordValue.value)) {
                                        startActivity(
                                            Intent(
                                                applicationContext,
                                                MainActivity::class.java
                                            ).apply {
                                                putExtra("SECRET_KEY", secretKey!!.encoded)
                                            }
                                        )
                                        finish()
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

    fun verifyMasterPassword(masterPassword: String): Boolean {
        val salt = viewModel.getSalt()
        val verifyVector = viewModel.getVector()
        val verifyVectorString =
            Base64.encodeToString(verifyVector, Base64.NO_WRAP)
        val verifyVectorIv = viewModel.getVectorIv()

        val hashResult: Argon2KtResult = argon2Kt.hash(
            mode = Argon2Mode.ARGON2_I,
            password = masterPassword.toByteArray(),
            salt = salt
        )
        secretKey =
            SecretKeySpec(hashResult.rawHashAsByteArray(), "AES")

        val encryptedVerifyVector = AES.encode(
            verifyVectorString, secretKey!!, IvParameterSpec(verifyVectorIv)
        )

        return encryptedVerifyVector == viewModel.getEncryptedVector()
    }
}
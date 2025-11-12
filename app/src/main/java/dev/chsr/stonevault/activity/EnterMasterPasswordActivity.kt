package dev.chsr.stonevault.activity

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import dev.chsr.stonevault.utils.AES
import dev.chsr.stonevault.R
import dev.chsr.stonevault.ui.theme.StoneVaultTheme
import dev.chsr.stonevault.viewmodel.AppViewModel
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
        enableEdgeToEdge()
        setContent {
            StoneVaultTheme {
                var masterPasswordValue by remember { mutableStateOf("") }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(modifier = Modifier.imePadding()) {
                            OutlinedTextField(
                                value = masterPasswordValue,
                                onValueChange = { masterPasswordValue = it },
                                label = {
                                    Text(stringResource(R.string.enter_master_password))
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                            Button(onClick = {
                                if (verifyMasterPassword(masterPasswordValue)) {
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            MainActivity::class.java
                                        ).apply {
                                            putExtra("SECRET_KEY", secretKey!!.encoded)
                                        }
                                    )
                                    finish()
                                }
                            }) {
                                Text(stringResource(R.string.ok))
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
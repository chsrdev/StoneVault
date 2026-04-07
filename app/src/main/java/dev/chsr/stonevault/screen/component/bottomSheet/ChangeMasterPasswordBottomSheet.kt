package dev.chsr.stonevault.screen.component.bottomSheet

import android.content.Intent
import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.currentCompositionContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.lambdapioneer.argon2kt.Argon2Kt
import com.lambdapioneer.argon2kt.Argon2KtResult
import com.lambdapioneer.argon2kt.Argon2Mode
import dev.chsr.stonevault.R
import dev.chsr.stonevault.activity.MainActivity
import dev.chsr.stonevault.activity.component.MasterPasswordTextField
import dev.chsr.stonevault.screen.component.fab.SavePasswordFab
import dev.chsr.stonevault.security.SessionManager
import dev.chsr.stonevault.utils.AES
import dev.chsr.stonevault.viewmodel.AppViewModel
import dev.chsr.stonevault.viewmodel.CredentialViewModel
import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec

@OptIn(ExperimentalMaterial3Api::class, InternalComposeApi::class)
@Composable
fun ChangeMasterPasswordBottomSheet(show: MutableState<Boolean>, appViewModel: AppViewModel, credentialViewModel: CredentialViewModel) {

    val isWrongInput = remember { mutableStateOf(false) }
    val masterPassword = remember { mutableStateOf("") }
    val confirmMasterPassword = remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = {
            show.value = false
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                MasterPasswordTextField(
                    isWrongInput, masterPassword, stringResource(R.string.create_master_password)
                )
            }
            item {
                MasterPasswordTextField(
                    isWrongInput,
                    confirmMasterPassword,
                    stringResource(R.string.confirm_master_password)
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val haptic = LocalHapticFeedback.current
                        SavePasswordFab {
                            if (masterPassword.value == confirmMasterPassword.value && masterPassword.value.isNotEmpty()) {
                                val oldKey = SessionManager.getKey()
                                val newKey = saveFirstEnter(masterPassword.value, appViewModel)

                                credentialViewModel.reEncryptAll(newKey)

                                SessionManager.setKey(newKey)

                                context.startActivity(
                                    Intent(
                                        context,
                                        MainActivity::class.java
                                    )
                                )
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.Reject)
                                isWrongInput.value = true
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun saveFirstEnter(masterPassword: String, viewModel: AppViewModel): SecretKeySpec {
    val argon2Kt = Argon2Kt()
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
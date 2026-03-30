package dev.chsr.stonevault.screen.component.bottomSheet

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.chsr.stonevault.security.PasswordStrength
import dev.chsr.stonevault.R
import dev.chsr.stonevault.entity.DecodedCredential
import dev.chsr.stonevault.screen.component.fab.OpenGeneratePasswordSheetFab
import dev.chsr.stonevault.screen.component.fab.SavePasswordFab
import dev.chsr.stonevault.security.analyzePassword
import dev.chsr.stonevault.viewmodel.CredentialViewModel
import dev.chsr.stonevault.viewmodel.LocalizationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordBottomSheet(
    credentialViewModel: CredentialViewModel,
    showNewPasswordBottomSheet: MutableState<Boolean>,
    localizationViewModel: LocalizationViewModel
) {
    val credentials by credentialViewModel.credentials.collectAsState()

    var title by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var isWrongTitle by remember { mutableStateOf(false) }

    val animatedTitleColor by animateColorAsState(
        targetValue = if (isWrongTitle) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "color_animation_title"
    )

    val allPasswords = credentials.mapNotNull { credential ->
        val decoded = credentialViewModel.getDecodedCredentialById(credential.id)
        decoded?.let { credential.id to it.password }
    }

    val passwordAnalysis = remember(password.value, allPasswords) {
        analyzePassword(
            password = password.value,
            currentId = -1,
            allPasswords = allPasswords
        )
    }

    val passwordBorderTargetColor = when (passwordAnalysis.strength) {
        PasswordStrength.WEAK -> MaterialTheme.colorScheme.error
        PasswordStrength.MEDIUM -> Color(0xFFFFC107)
        PasswordStrength.STRONG -> Color(0xFF4CAF50)
    }

    val animatedPasswordBorderColor by animateColorAsState(
        targetValue = passwordBorderTargetColor,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "password_border_animation"
    )

    val passwordLabelRes = when (passwordAnalysis.strength) {
        PasswordStrength.WEAK -> R.string.password_label_weak
        PasswordStrength.MEDIUM -> R.string.password_label_medium
        PasswordStrength.STRONG -> R.string.password_label_strong
    }

    val reusedMessage = when {
        passwordAnalysis.reusedCount > 1 -> stringResource(
            R.string.password_reused_multiple_entries,
            passwordAnalysis.reusedCount
        )

        passwordAnalysis.isReused -> stringResource(R.string.password_reused_one_entry)
        else -> null
    }

    val showGeneratePasswordBottomSheet = remember { mutableStateOf(false) }

    if (showGeneratePasswordBottomSheet.value)
        GeneratePasswordSheet(
            password,
            showGeneratePasswordBottomSheet,
            localizationViewModel
        )

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = {
            showNewPasswordBottomSheet.value = false
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        onValueChange = {
                            title = it
                            isWrongTitle = false
                        },
                        label = {
                            Text(stringResource(R.string.title))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = stringResource(R.string.password_title_icon),
                                tint = animatedTitleColor
                            )
                        }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(stringResource(R.string.email))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = stringResource(R.string.password_email_icon),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = {
                            Text(stringResource(passwordLabelRes))
                        },
                        supportingText = {
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                reusedMessage?.let {
                                    Text(
                                        text = it,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                passwordAnalysis.recommendations.take(3)
                                    .forEach { recommendationRes ->
                                        Text(
                                            text = stringResource(recommendationRes),
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                            focusedBorderColor = animatedPasswordBorderColor,
                            unfocusedBorderColor = animatedPasswordBorderColor,
                            focusedLeadingIconColor = animatedPasswordBorderColor,
                            unfocusedLeadingIconColor = animatedPasswordBorderColor,
                            focusedLabelColor = animatedPasswordBorderColor,
                            unfocusedLabelColor = animatedPasswordBorderColor,
                            cursorColor = animatedPasswordBorderColor
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(R.string.password_icon)
                            )
                        }
                    )

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = notes,
                        onValueChange = { notes = it },
                        label = {
                            Text(stringResource(R.string.notes))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onBackground
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.password_notes_icon),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    )
                }
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
                        OpenGeneratePasswordSheetFab {
                            showGeneratePasswordBottomSheet.value = true
                        }

                        SavePasswordFab {
                            if (title.isNotEmpty()) {
                                credentialViewModel.addCredential(
                                    DecodedCredential(
                                        title = title.trim(),
                                        password = password.value.trim(),
                                        email = email.trim(),
                                        notes = notes.trim()
                                    )
                                )
                                showNewPasswordBottomSheet.value = false
                            } else {
                                isWrongTitle = true
                            }
                        }
                    }
                }
            }
        }
    }
}


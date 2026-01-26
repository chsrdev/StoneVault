package dev.chsr.stonevault.screen.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chsr.stonevault.R
import dev.chsr.stonevault.entity.DecodedCredential
import dev.chsr.stonevault.viewmodel.CredentialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPasswordBottomSheet(
    id: Int,
    credentialViewModel: CredentialViewModel,
    showEditPasswordBottomSheet: MutableState<Boolean>
) {
    if (id == -1) {
        showEditPasswordBottomSheet.value = false
        return
    }

    val decodedCredential = credentialViewModel.getDecodedCredentialById(id!!)
    if (decodedCredential == null) {
        showEditPasswordBottomSheet.value = false
        return
    }

    var title by remember { mutableStateOf(decodedCredential.title) }
    var password by remember { mutableStateOf(decodedCredential.password) }
    var email by remember { mutableStateOf(decodedCredential.email) }
    var notes by remember { mutableStateOf(decodedCredential.notes) }

    var isWrongTitle by remember { mutableStateOf(false) }
    val animatedTitleColor by animateColorAsState(
        targetValue = if (isWrongTitle) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "color_animation_title"
    )

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = {
            showEditPasswordBottomSheet.value = false
        },
        sheetState = sheetState
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = {
                            Text(stringResource(R.string.title))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = stringResource(R.string.password_title_icon),
                                tint = animatedTitleColor
                            )
                        }
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(stringResource(R.string.email))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = stringResource(R.string.password_email_icon)
                            )
                        }
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(stringResource(R.string.password))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(R.string.password_icon),
                            )
                        }
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = {
                            Text(stringResource(R.string.notes))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.password_notes_icon)
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
                        DeletePasswordFab {
                            credentialViewModel.deleteCredential(id)
                            showEditPasswordBottomSheet.value = false
                        }
                        SavePasswordFab {
                            if (title.isNotEmpty()) {
                                credentialViewModel.updateCredential(
                                    DecodedCredential(
                                        id = id,
                                        title = title,
                                        email = email,
                                        password = password,
                                        notes = notes
                                    )
                                )
                                showEditPasswordBottomSheet.value = false
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

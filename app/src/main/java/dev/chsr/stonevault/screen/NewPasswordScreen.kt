package dev.chsr.stonevault.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chsr.stonevault.R
import dev.chsr.stonevault.entity.DecodedCredential
import dev.chsr.stonevault.viewmodel.CredentialViewModel

@Composable
fun NewPasswordScreen(credentialViewModel: CredentialViewModel) {
    var title by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(), contentAlignment = Alignment.Center
    ) {
        Column {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = {
                    Text(stringResource(R.string.title))
                }
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = {
                    Text(stringResource(R.string.email))
                }
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(stringResource(R.string.password))
                }
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = {
                    Text(stringResource(R.string.notes))
                }
            )
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        credentialViewModel.addCredential(
                            DecodedCredential(
                                title = title,
                                password = password,
                                email = email,
                                notes = notes
                            )
                        )
                    } // todo: else highlight
                },
            ) {
                Text(stringResource(R.string.done))
            }
        }
    }
}
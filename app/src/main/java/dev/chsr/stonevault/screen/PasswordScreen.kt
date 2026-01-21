package dev.chsr.stonevault.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.chsr.stonevault.R
import dev.chsr.stonevault.entity.DecodedCredential
import dev.chsr.stonevault.viewmodel.CredentialViewModel

@Composable
fun PasswordScreen(
    id: Int?,
    credentialViewModel: CredentialViewModel,
    navController: NavController
) {
    if (id == -1) {
        navController.popBackStack()
        return
    }

    val decodedCredential = credentialViewModel.getDecodedCredentialById(id!!)
    if (decodedCredential == null) {
        navController.popBackStack()
        return
    }

    var title by remember { mutableStateOf(decodedCredential.title) }
    var password by remember { mutableStateOf(decodedCredential.password) }
    var email by remember { mutableStateOf(decodedCredential.email) }
    var notes by remember { mutableStateOf(decodedCredential.notes) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
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
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = {
                            Text(stringResource(R.string.email))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(stringResource(R.string.password))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = {
                            Text(stringResource(R.string.notes))
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
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
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White
                        ),
                        onClick = {
                            credentialViewModel.deleteCredential(id)
                            navController.popBackStack()
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                }
            }
        }
    }
}
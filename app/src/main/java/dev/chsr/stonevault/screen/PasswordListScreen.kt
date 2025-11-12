package dev.chsr.stonevault.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.chsr.stonevault.viewmodel.CredentialViewModel

data class PasswordListEntry(
    val id: Int,
    val title: String
)

@Composable
fun PasswordListScreen(credentialViewModel: CredentialViewModel) {
    credentialViewModel.loadCredentials()
    val credentials = credentialViewModel.credentials.collectAsState()
    val entries = credentials.value.map { PasswordListEntry(it.id, credentialViewModel.getDecodedCredential(it.id)!!.title) }
        LazyColumn {
            items(entries) { entry ->
                Text(entry.title)
            }
        }
}
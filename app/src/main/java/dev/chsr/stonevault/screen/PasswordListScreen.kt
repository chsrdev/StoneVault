package dev.chsr.stonevault.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.chsr.stonevault.screen.component.EditPasswordBottomSheet
import dev.chsr.stonevault.screen.component.NewPasswordBottomSheet
import dev.chsr.stonevault.screen.component.NewPasswordFab
import dev.chsr.stonevault.screen.component.PasswordCard
import dev.chsr.stonevault.viewmodel.CredentialViewModel

data class PasswordListEntry(
    val id: Int,
    val title: String
)

@Composable
fun PasswordListScreen(credentialViewModel: CredentialViewModel, navController: NavController) {
    credentialViewModel.loadCredentials()
    val credentials by credentialViewModel.credentials.collectAsState()
    val entries = credentials.map {
        PasswordListEntry(
            it.id,
            credentialViewModel.getDecodedCredentialById(it.id)!!.title
        )
    }

    val showNewPasswordBottomSheet = remember { mutableStateOf(false) }
    val showEditPasswordBottomSheet = remember { mutableStateOf(false) }
    var editPasswordId by remember { mutableStateOf(-1) }

    Scaffold(
        floatingActionButton = { NewPasswordFab(showNewPasswordBottomSheet) }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(entries) { entry ->
                PasswordCard(entry) {
                    editPasswordId = entry.id
                    showEditPasswordBottomSheet.value = true
                }
            }
        }

        if (showNewPasswordBottomSheet.value) {
            NewPasswordBottomSheet(
                credentialViewModel,
                showNewPasswordBottomSheet
            )
        }
        if (showEditPasswordBottomSheet.value) {
            EditPasswordBottomSheet(
                editPasswordId,
                credentialViewModel,
                showEditPasswordBottomSheet
            )
        }
    }
}
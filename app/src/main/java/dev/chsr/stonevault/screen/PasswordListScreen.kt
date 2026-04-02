package dev.chsr.stonevault.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.chsr.stonevault.screen.component.bottomSheet.EditPasswordBottomSheet
import dev.chsr.stonevault.screen.component.bottomSheet.NewPasswordBottomSheet
import dev.chsr.stonevault.screen.component.fab.NewPasswordFab
import dev.chsr.stonevault.screen.component.PasswordCard
import dev.chsr.stonevault.screen.component.SearchBar
import dev.chsr.stonevault.viewmodel.CredentialViewModel
import dev.chsr.stonevault.viewmodel.LocalizationViewModel

data class PasswordListEntry(
    val id: Int,
    val title: String
)

@Composable
fun PasswordListScreen(
    credentialViewModel: CredentialViewModel,
    localizationViewModel: LocalizationViewModel,
    navController: NavController
) {
    credentialViewModel.loadCredentials()
    val credentials by credentialViewModel.credentials.collectAsState()
    val entries = mutableStateOf(credentials.map {
        PasswordListEntry(
            it.id,
            credentialViewModel.getDecodedCredentialById(it.id)!!.title
        )
    })
    val filteredEntries = mutableStateOf(entries.value)


    val showNewPasswordBottomSheet = remember { mutableStateOf(false) }
    val showEditPasswordBottomSheet = remember { mutableStateOf(false) }
    var editPasswordId by remember { mutableStateOf(-1) }

    Scaffold(
        floatingActionButton = { NewPasswordFab(showNewPasswordBottomSheet) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            item {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    entries = entries.value,
                    filteredEntries = filteredEntries
                )
            }

            items(filteredEntries.value) { entry ->
                PasswordCard(entry) {
                    editPasswordId = entry.id
                    showEditPasswordBottomSheet.value = true
                }
            }
        }

        if (showNewPasswordBottomSheet.value) {
            NewPasswordBottomSheet(
                credentialViewModel,
                showNewPasswordBottomSheet,
                localizationViewModel
            )
        }
        if (showEditPasswordBottomSheet.value) {
            EditPasswordBottomSheet(
                editPasswordId,
                credentialViewModel,
                showEditPasswordBottomSheet,
                localizationViewModel
            )
        }
    }
}
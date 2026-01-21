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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.chsr.stonevault.screen.component.NewPasswordFab
import dev.chsr.stonevault.viewmodel.CredentialViewModel

data class PasswordListEntry(
    val id: Int,
    val title: String
)

@Composable
fun PasswordListScreen(credentialViewModel: CredentialViewModel, navController: NavController) {
    credentialViewModel.loadCredentials()
    val credentials = credentialViewModel.credentials.collectAsState()
    val entries = credentials.value.map {
        PasswordListEntry(
            it.id,
            credentialViewModel.getDecodedCredentialById(it.id)!!.title
        )
    }
    val showNewPasswordBottomSheet = mutableStateOf(false)

    Scaffold(
        floatingActionButton = { NewPasswordFab(showNewPasswordBottomSheet) }
    ) {
        LazyColumn {
            items(entries) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .clickable {
                            navController.navigate("${Screen.Password.route}/${entry.id}")
                        },
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
                        text = entry.title,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
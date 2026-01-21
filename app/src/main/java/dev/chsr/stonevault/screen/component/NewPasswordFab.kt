package dev.chsr.stonevault.screen.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import dev.chsr.stonevault.R

@Composable
fun NewPasswordFab(showNewPasswordBottomSheet: MutableState<Boolean>) {
    FloatingActionButton(
        onClick = { showNewPasswordBottomSheet.value = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.add_new_password_icon)
        )
    }
}
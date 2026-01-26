package dev.chsr.stonevault.screen.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chsr.stonevault.R
import dev.chsr.stonevault.screen.PasswordListEntry

@Composable
fun SearchBar(modifier: Modifier, entries: List<PasswordListEntry>, filteredEntries: MutableState<List<PasswordListEntry>>) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = modifier,
        value = text,
        onValueChange = { newText ->
            text = newText
            if (text.isNotEmpty()) {
                filteredEntries.value = entries.filter { text in it.title }
            } else {
                filteredEntries.value = entries
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon)
            )
        },
        placeholder = {
            Text(stringResource(R.string.search))
        }
    )
}
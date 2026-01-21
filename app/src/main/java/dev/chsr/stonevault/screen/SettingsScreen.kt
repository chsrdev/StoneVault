package dev.chsr.stonevault.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chsr.stonevault.R
import dev.chsr.stonevault.utils.Languages
import dev.chsr.stonevault.viewmodel.LocalizationViewModel

@Composable
fun SettingsScreen(localizationViewModel: LocalizationViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val selected = localizationViewModel.currentLanguage.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ){
        item {
//            Card {
//                Row(
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
//                ) {
//                    Text(stringResource(R.string.language))
//
//                    Text(
//                        text = stringResource(selected.value.stringId),
//                        modifier = Modifier.clickable {
//                            expanded = !expanded
//                        }
//                    )
//                    DropdownMenu(
//                        expanded = expanded,
//                        onDismissRequest = { expanded = false },
//                    ) {
//                        Languages.allLanguages.forEach { lang ->
//                            DropdownMenuItem(
//                                text = {
//                                    Text(stringResource(lang.stringId))
//                                },
//                                onClick = {
//                                    expanded = false
//                                    localizationViewModel.setLanguage(lang)
//                                }
//                            )
//                        }
//                    }
//                }
            }
    }
}
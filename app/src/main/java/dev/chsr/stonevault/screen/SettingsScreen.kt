package dev.chsr.stonevault.screen

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chsr.stonevault.R
import dev.chsr.stonevault.screen.component.bottomSheet.ChangeMasterPasswordBottomSheet
import dev.chsr.stonevault.utils.Language
import dev.chsr.stonevault.utils.Languages
import dev.chsr.stonevault.viewmodel.AppViewModel
import dev.chsr.stonevault.viewmodel.CredentialViewModel
import dev.chsr.stonevault.viewmodel.LocalizationViewModel
import dev.chsr.stonevault.viewmodel.theme.ThemeMode
import dev.chsr.stonevault.viewmodel.theme.ThemeViewModel
import java.util.Locale

val LocalAppLocale = staticCompositionLocalOf { Locale.getDefault() }

@Composable
fun localizedString(resId: Int, locale: Locale): String {
    val context = LocalContext.current
    val config = Configuration(context.resources.configuration).apply {
        setLocale(locale)
    }
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.resources.getString(resId)
}

fun AppCompatActivity.updateLocale(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun AppCompatActivity.restartActivity() {
    finish()
    startActivity(intent)
}


@Composable
fun SettingsScreen(
    localizationViewModel: LocalizationViewModel,
    themeViewModel: ThemeViewModel,
    appViewModel: AppViewModel,
    credentialViewModel: CredentialViewModel
) {
    val selectedLanguage by localizationViewModel.currentLanguage.collectAsState()
    val selectedTheme by themeViewModel.currentTheme.collectAsState()
    val activity = LocalContext.current as? AppCompatActivity

    val languageMenuExpanded = remember { mutableStateOf(false) }
    val themeMenuExpanded = remember { mutableStateOf(false) }
    val showChangeMasterPasswordAlert = remember { mutableStateOf(false) }
    val showChangeMasterPasswordBottomSheet = remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            SettingsLanguage(
                languageMenuExpanded,
                selectedLanguage,
                activity,
                localizationViewModel
            )
        }

        item {
            SettingsTheme(themeMenuExpanded, selectedTheme, activity, themeViewModel)
        }

        item {
            SettingsChangeMasterPassword(showChangeMasterPasswordAlert)
        }
    }

    if (showChangeMasterPasswordAlert.value)
        AlertDialog(
            onDismissRequest = {
                showChangeMasterPasswordAlert.value = false
            },
            text = {
                Text(stringResource(R.string.are_you_sure_you_want_to_change_your_master_password_your_current_passwords_will_remain_the_same))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showChangeMasterPasswordAlert.value = false
                        showChangeMasterPasswordBottomSheet.value = true
                    }
                ) {
                    Text(stringResource(R.string.change_master_password))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showChangeMasterPasswordAlert.value = false
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )

    if (showChangeMasterPasswordBottomSheet.value)
        ChangeMasterPasswordBottomSheet(
            showChangeMasterPasswordBottomSheet,
            appViewModel,
            credentialViewModel
        )
}

@Composable
private fun SettingsCard(
    content: @Composable (ColumnScope.() -> Unit)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        content = content
    )
}

@Composable
private fun SettingsChangeMasterPassword(
    showChangeMasterPasswordAlert: MutableState<Boolean>
) {
    SettingsCard {
        Column(
            modifier = Modifier
                .clickable {
                    showChangeMasterPasswordAlert.value = true
                }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.change_master_password))
                }
            }
        }
    }
}

@Composable
private fun SettingsTheme(
    themeMenuExpanded: MutableState<Boolean>,
    selectedTheme: ThemeMode,
    activity: AppCompatActivity?,
    themeViewModel: ThemeViewModel
) {
    SettingsCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.theme))
                }

                Box {
                    TextButton(
                        onClick = { themeMenuExpanded.value = true }
                    ) {
                        Text(text = stringResource(selectedTheme.stringId))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = themeMenuExpanded.value,
                        onDismissRequest = { themeMenuExpanded.value = false }
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(mode.stringId))
                                },
                                onClick = {
                                    themeMenuExpanded.value = false
                                    activity?.let {
                                        themeViewModel.setTheme(mode, it)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsLanguage(
    languageMenuExpanded: MutableState<Boolean>,
    selectedLanguage: Language,
    activity: AppCompatActivity?,
    localizationViewModel: LocalizationViewModel
) {
    SettingsCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = stringResource(R.string.language))
                }

                Box {
                    TextButton(
                        onClick = { languageMenuExpanded.value = true }
                    ) {
                        Text(text = stringResource(selectedLanguage.stringId))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = languageMenuExpanded.value,
                        onDismissRequest = { languageMenuExpanded.value = false }
                    ) {
                        Languages.allLanguages.forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(lang.stringId))
                                },
                                onClick = {
                                    languageMenuExpanded.value = false
                                    activity?.let {
                                        localizationViewModel.setLanguage(lang, it)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
package dev.chsr.stonevault.screen

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chsr.stonevault.R
import dev.chsr.stonevault.utils.Languages
import dev.chsr.stonevault.viewmodel.LocalizationViewModel
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
fun SettingsScreen(localizationViewModel: LocalizationViewModel) {
    val selectedLanguage = localizationViewModel.currentLanguage.collectAsState()

    val activity = LocalContext.current as? AppCompatActivity

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.language))
                    Spacer(modifier = Modifier.height(16.dp))

                    Languages.allLanguages.forEach { lang ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable {
                                    activity?.let {
                                        localizationViewModel.setLanguage(lang, it)
                                    }
                                }
                        ) {
                            RadioButton(
                                selected = selectedLanguage.value == lang,
                                onClick = {
                                    activity?.let {
                                        localizationViewModel.setLanguage(lang, it)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(lang.stringId))
                        }
                    }
                }
            }
        }
    }
}


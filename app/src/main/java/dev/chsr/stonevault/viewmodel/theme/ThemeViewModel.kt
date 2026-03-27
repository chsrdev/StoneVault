package dev.chsr.stonevault.viewmodel.theme

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.core.content.edit
import androidx.lifecycle.ViewModelProvider
import dev.chsr.stonevault.activity.MainActivity
import dev.chsr.stonevault.screen.restartActivity
import dev.chsr.stonevault.utils.PreferencesManager
import dev.chsr.stonevault.viewmodel.LocalizationViewModel

class ThemeViewModel(private val application: Application) : ViewModel() {
    private val prefs = PreferencesManager(application.applicationContext)
    private val _currentTheme = MutableStateFlow(
        ThemeMode.valueOf(prefs.readString("theme", ThemeMode.SYSTEM.name))
    )
    val currentTheme = _currentTheme.asStateFlow()

    fun setTheme(theme: ThemeMode, activity: AppCompatActivity) {
        _currentTheme.value = theme
        prefs.saveString("theme", theme.name)
        activity.restartActivity()
    }

    class ThemeViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                return ThemeViewModel(application) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
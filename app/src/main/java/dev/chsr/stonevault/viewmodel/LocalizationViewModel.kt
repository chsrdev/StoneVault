package dev.chsr.stonevault.viewmodel

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dev.chsr.stonevault.screen.restartActivity
import dev.chsr.stonevault.screen.updateLocale
import dev.chsr.stonevault.utils.Language
import dev.chsr.stonevault.utils.Languages
import dev.chsr.stonevault.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class LocalizationViewModel(private val application: Application) : ViewModel() {

    private val languageKey = "language"
    private val preferencesManager = PreferencesManager(application.applicationContext)

    private val _currentLanguage = MutableStateFlow(Languages.English)
    val currentLanguage: StateFlow<Language> = _currentLanguage

    init {
        val savedCode = preferencesManager.readString(languageKey, Locale.getDefault().language)
        val language = Languages.getByCode(savedCode)
        _currentLanguage.value = language
    }

    fun setLanguage(language: Language, activity: AppCompatActivity) {
        _currentLanguage.value = language
        saveLanguagePreference(language.code)
        activity.updateLocale(language.code)
        activity.restartActivity()
    }

    private fun saveLanguagePreference(languageCode: String) {
        preferencesManager.saveString(languageKey, languageCode)
    }

    private fun getSavedLanguageCode(): String {
        return preferencesManager.readString(languageKey, Locale.getDefault().language)
    }

    private fun loadSavedLanguage() {
        val savedCode = getSavedLanguageCode()
        val language = Languages.getByCode(savedCode)
        _currentLanguage.value = language
    }

    class LocalizationViewModelFactory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LocalizationViewModel::class.java)) {
                return LocalizationViewModel(application) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

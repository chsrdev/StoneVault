package dev.chsr.stonevault.viewmodel

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    fun setLanguage(language: Language) {
        viewModelScope.launch {
            _currentLanguage.value = language
            updateAppLocale(language.code)
            saveLanguagePreference(language.code)
        }
    }

    fun loadSavedLanguage() {
        viewModelScope.launch {
            val savedLanguageCode = getSavedLanguageCode()
            val language = Languages.getByCode(savedLanguageCode)
            _currentLanguage.value = language
            updateAppLocale(language.code)
        }
    }

    private fun saveLanguagePreference(languageCode: String) {
        preferencesManager.saveString(languageKey, languageCode)
    }

    private fun getSavedLanguageCode(): String {
        return preferencesManager.readString(languageKey, Locale.getDefault().language)
    }

    private fun updateAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = application.applicationContext.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)

        application.applicationContext.createConfigurationContext(configuration)
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
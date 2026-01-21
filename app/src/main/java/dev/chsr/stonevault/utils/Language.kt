package dev.chsr.stonevault.utils

import androidx.compose.ui.res.stringResource
import dev.chsr.stonevault.R

data class Language(
    val code: String,
    val stringId: Int
)

object Languages {
    val English = Language("en", R.string.english)
    val Russian = Language("ru", R.string.russian)

    val allLanguages = listOf(English, Russian)

    fun getByCode(code: String): Language {
        return allLanguages.find { it.code == code } ?: English
    }
}
package dev.chsr.stonevault.viewmodel.theme

import androidx.compose.ui.res.stringResource
import dev.chsr.stonevault.R

enum class ThemeMode(val stringId: Int) {
    LIGHT(R.string.light),
    DARK(R.string.dark),
    SYSTEM(R.string.system)
}
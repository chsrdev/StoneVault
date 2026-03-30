package dev.chsr.stonevault.screen.component.bottomSheet

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.chsr.stonevault.R
import dev.chsr.stonevault.screen.component.fab.GeneratePasswordFab
import dev.chsr.stonevault.viewmodel.LocalizationViewModel
import java.security.SecureRandom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratePasswordSheet(
    passwordText: MutableState<String>,
    showGeneratePasswordBottomSheet: MutableState<Boolean>,
    localizationViewModel: LocalizationViewModel
) {
    val context = LocalContext.current
    val currentLangCode = localizationViewModel.currentLanguage.collectAsState().value.code

    val englishWords = stringArrayResource(R.array.english_words).toList()
    val russianWords = stringArrayResource(R.array.russian_words).toList()
    val separators = stringArrayResource(R.array.separators).toList()

    var easyToRemember by remember { mutableStateOf(false) }

    var wordCount by remember { mutableStateOf(4) }
    val useEnglish = currentLangCode == "en"
    val useRussian = currentLangCode == "ru"

    var passwordLength by remember { mutableStateOf(15) }
    var includeUppercase by remember { mutableStateOf(true) }
    var includeLowercase by remember { mutableStateOf(true) }
    var includeDigits by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { showGeneratePasswordBottomSheet.value = false },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .animateContentSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.easy_to_remember))
                Checkbox(
                    checked = easyToRemember,
                    onCheckedChange = { easyToRemember = !easyToRemember }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Crossfade(
                targetState = easyToRemember,
                animationSpec = tween(durationMillis = 300)
            ) { isEasy ->
                if (!isEasy) {
                    Column {
                        Text(stringResource(R.string.password_length, passwordLength))
                        Slider(
                            value = passwordLength.toFloat(),
                            onValueChange = { passwordLength = it.toInt() },
                            valueRange = 6f..32f,
                            steps = 26
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.include_uppercase))
                            Switch(checked = includeUppercase, onCheckedChange = { includeUppercase = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.include_lowercase))
                            Switch(checked = includeLowercase, onCheckedChange = { includeLowercase = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.include_digits))
                            Switch(checked = includeDigits, onCheckedChange = { includeDigits = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(R.string.include_symbols))
                            Switch(checked = includeSymbols, onCheckedChange = { includeSymbols = it })
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                } else {
                    Column {
                        Text(stringResource(R.string.number_of_words, wordCount))
                        Slider(
                            value = wordCount.toFloat(),
                            onValueChange = { wordCount = it.toInt() },
                            valueRange = 2f..8f,
                            steps = 6
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GeneratePasswordFab {
                        val secureRandom = SecureRandom()

                        val generatedPassword = if (easyToRemember) {
                            generatePassphrase(
                                wordCount = wordCount,
                                useEnglish = useEnglish,
                                useRussian = useRussian,
                                englishWords = englishWords,
                                russianWords = russianWords,
                                separators = separators,
                                random = secureRandom
                            )
                        } else {
                            generateRandomString(
                                length = passwordLength,
                                includeUppercase = includeUppercase,
                                includeLowercase = includeLowercase,
                                includeDigits = includeDigits,
                                includeSymbols = includeSymbols,
                                random = secureRandom
                            )
                        }

                        showGeneratePasswordBottomSheet.value = false
                        passwordText.value = generatedPassword
                    }
                }
            }
        }
    }
}


private fun generateRandomString(
    length: Int,
    includeUppercase: Boolean,
    includeLowercase: Boolean,
    includeDigits: Boolean,
    includeSymbols: Boolean,
    random: SecureRandom
): String {
    val charSets = mutableListOf<String>()
    if (includeUppercase) charSets.add("ABCDEFGHIJKLMNOPQRSTUVWXYZ")
    if (includeLowercase) charSets.add("abcdefghijklmnopqrstuvwxyz")
    if (includeDigits) charSets.add("0123456789")
    if (includeSymbols) charSets.add("!@#\$%^&*()-_=+[]{}")

    val allChars = charSets.joinToString("")
    if (allChars.isEmpty()) return ""

    return StringBuilder(length).apply {
        repeat(length) {
            append(allChars[random.nextInt(allChars.length)])
        }
    }.toString()
}

private fun generatePassphrase(
    wordCount: Int,
    useEnglish: Boolean,
    useRussian: Boolean,
    englishWords: List<String>,
    russianWords: List<String>,
    separators: List<String>,
    random: SecureRandom
): String {
    val availableWords = mutableListOf<String>()
    if (useEnglish) availableWords.addAll(englishWords)
    if (useRussian) availableWords.addAll(russianWords)

    if (availableWords.isEmpty()) {
        availableWords.addAll(englishWords)
    }

    val selectedWords = List(wordCount) {
        availableWords[random.nextInt(availableWords.size)]
    }

    val transformedWords = selectedWords.map { word ->
        if (random.nextBoolean()) {
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } else {
            word
        }
    }

    val separator = separators[random.nextInt(separators.size)]
    val passphrase = transformedWords.joinToString(separator)

    val suffix = buildString {
        append(random.nextInt(10))
        append(separators[random.nextInt(separators.size)])
    }

    return "$passphrase$suffix"
}
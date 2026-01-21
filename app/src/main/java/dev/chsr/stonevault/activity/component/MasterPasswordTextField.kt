package dev.chsr.stonevault.activity.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import dev.chsr.stonevault.R
import kotlinx.coroutines.launch

@Composable
fun MasterPasswordTextField(
    isWrongPasswordInput: MutableState<Boolean>,
    masterPasswordValue: MutableState<String>,
    fieldLabel: String
) {
    val animatedContentMasterPasswordColor by animateColorAsState(
        targetValue = if (isWrongPasswordInput.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(
            durationMillis = 500,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "color_animation_content"
    )
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }

    if (isWrongPasswordInput.value) {
        scope.launch {
            offsetX.stop()
            offsetX.snapTo(0F)
            repeat(2) {
                offsetX.animateTo(-8f, tween(60))
                offsetX.animateTo(8f, tween(60))
            }
            offsetX.animateTo(0f, tween(80))
            isWrongPasswordInput.value = false
        }
    }

    OutlinedTextField(
        modifier = Modifier.graphicsLayer {
            translationX = offsetX.value
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(R.string.lock_icon),
                tint = animatedContentMasterPasswordColor
            )
        },
        label = { Text(fieldLabel) },
        value = masterPasswordValue.value,
        onValueChange = {
            masterPasswordValue.value = it
            isWrongPasswordInput.value = false
        },
    )
}
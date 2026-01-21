package dev.chsr.stonevault.activity.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import dev.chsr.stonevault.R

@Composable
fun MasterPasswordTextField(
    isWrongPasswordInput: MutableState<Boolean>,
    masterPasswordValue: MutableState<String>
) {
    val textFieldTransition = rememberInfiniteTransition(label = "shake")
    val offsetX by textFieldTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )
    val animatedContentMasterPasswordColor by animateColorAsState(
        targetValue = if (isWrongPasswordInput) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
        animationSpec = tween(
            durationMillis = 500,
            easing = androidx.compose.animation.core.FastOutSlowInEasing
        ),
        label = "color_animation_content"
    )

    OutlinedTextField(
        modifier = Modifier.graphicsLayer {
            translationX = if (isWrongPasswordInput.value) offsetX else 0f
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = stringResource(R.string.lock_icon),
                tint = animatedContentMasterPasswordColor
            )
        },
        label = {
            Text(
                text = stringResource(R.string.enter_master_password),
                color = animatedContentMasterPasswordColor
            )
        },
        value = masterPasswordValue,
        onValueChange = {
            masterPasswordValue.value = it
            isWrongPasswordInput.value = false
        },
    )
}
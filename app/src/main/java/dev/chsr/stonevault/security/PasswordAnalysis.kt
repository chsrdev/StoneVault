package dev.chsr.stonevault.security

import dev.chsr.stonevault.R

enum class PasswordStrength {
    WEAK, MEDIUM, STRONG
}

data class PasswordAnalysis(
    val strength: PasswordStrength,
    val score: Int,
    val recommendations: List<Int>,
    val isReused: Boolean,
    val reusedCount: Int
)

fun analyzePassword(
    password: String,
    currentId: Int,
    allPasswords: List<Pair<Int, String>>
): PasswordAnalysis {
    val recommendations = mutableListOf<Int>()

    val trimmedPassword = password.trim()
    val lower = trimmedPassword.lowercase()

    val isReused = allPasswords.any { (id, savedPassword) ->
        id != currentId && savedPassword == trimmedPassword && trimmedPassword.isNotEmpty()
    }

    val reusedCount = allPasswords.count { (id, savedPassword) ->
        id != currentId && savedPassword == trimmedPassword && trimmedPassword.isNotEmpty()
    }

    var score = 0

    if (trimmedPassword.length >= 12) {
        score += 2
    } else if (trimmedPassword.length >= 8) {
        score += 1
    } else {
        recommendations.add(R.string.password_recommendation_length)
    }

    val hasLowercase = trimmedPassword.any { it.isLowerCase() }
    val hasUppercase = trimmedPassword.any { it.isUpperCase() }
    val hasDigit = trimmedPassword.any { it.isDigit() }
    val hasSpecial = trimmedPassword.any { !it.isLetterOrDigit() }

    if (hasLowercase) score += 1 else recommendations.add(R.string.password_recommendation_lowercase)
    if (hasUppercase) score += 1 else recommendations.add(R.string.password_recommendation_uppercase)
    if (hasDigit) score += 1 else recommendations.add(R.string.password_recommendation_digit)
    if (hasSpecial) score += 1 else recommendations.add(R.string.password_recommendation_special)

    val commonPatterns = listOf(
        "123456", "qwerty", "password", "admin", "111111", "000000", "abc123"
    )

    if (commonPatterns.any { lower.contains(it) }) {
        score -= 2
        recommendations.add(R.string.password_recommendation_common_pattern)
    }

    if (trimmedPassword.toSet().size <= 3 && trimmedPassword.length >= 6) {
        score -= 1
        recommendations.add(R.string.password_recommendation_low_unique_chars)
    }

    if (isReused) {
        score = 0
        recommendations.add(R.string.password_recommendation_reused)
    }

    val strength = when {
        isReused -> PasswordStrength.WEAK
        score <= 2 -> PasswordStrength.WEAK
        score <= 4 -> PasswordStrength.MEDIUM
        else -> PasswordStrength.STRONG
    }

    return PasswordAnalysis(
        strength = strength,
        score = score.coerceAtLeast(0),
        recommendations = recommendations.distinct(),
        isReused = isReused,
        reusedCount = reusedCount
    )
}

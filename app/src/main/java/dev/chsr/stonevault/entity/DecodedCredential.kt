package dev.chsr.stonevault.entity

data class DecodedCredential(
    val id: Int? = null,
    var title: String,
    var password: String,
    var email: String,
    var notes: String
)

package dev.chsr.stonevault.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Credential(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "title_iv") val titleIv: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "password_iv") val passwordIv: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "email_iv") val emailIv: String,
    @ColumnInfo(name = "notes") val notes: String,
    @ColumnInfo(name = "notes_iv") val notesIv: String
)

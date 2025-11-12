package dev.chsr.stonevault.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.chsr.stonevault.dao.CredentialDao
import dev.chsr.stonevault.entity.Credential

@Database(entities = [Credential::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun credentialDao(): CredentialDao
}
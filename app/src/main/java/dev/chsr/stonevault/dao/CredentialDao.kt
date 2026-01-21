package dev.chsr.stonevault.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.chsr.stonevault.entity.Credential

@Dao
interface CredentialDao {
    @Query("SELECT * FROM credential")
    suspend fun getAll(): List<Credential>

    @Query("SELECT * FROM credential WHERE id=:id")
    suspend fun getById(id: Int): Credential

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg credentials: Credential)

    @Delete
    suspend fun delete(credential: Credential)

    @Query("DELETE FROM credential WHERE id=:id")
    suspend fun delete(id: Int)

    @Update
    suspend fun update(credential: Credential)

    @Query("DELETE FROM credential")
    suspend fun clear()
}
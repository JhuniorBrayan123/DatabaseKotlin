package com.example.datossinmvvm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    suspend fun getAll(): List<User>

    @Insert
    suspend fun insert(user: User)

    @Delete()
    suspend fun delete(user: User)

    @Query("DELETE FROM user WHERE uid = (SELECT uid FROM user ORDER BY uid DESC LIMIT 1)")
    suspend fun deleteLastUser()
}

package com.example.composertutorial.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM user_database LIMIT 1")
    fun getUser(): Flow<User?>

    @Insert
    suspend fun insert (user:User)

    @Update
    suspend fun update(user:User)
}
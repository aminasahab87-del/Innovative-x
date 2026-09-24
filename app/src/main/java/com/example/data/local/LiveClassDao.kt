package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.LiveClassSession
import kotlinx.coroutines.flow.Flow

@Dao
interface LiveClassDao {
    @Query("SELECT * FROM live_classes ORDER BY createdAt DESC")
    fun getAllClassesFlow(): Flow<List<LiveClassSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(session: LiveClassSession)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClasses(sessions: List<LiveClassSession>)

    @Query("DELETE FROM live_classes WHERE id = :classId")
    suspend fun deleteClass(classId: String)
}

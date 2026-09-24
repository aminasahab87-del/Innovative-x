package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Project
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE status IN ('PUBLISHED', 'APPROVED') ORDER BY createdAt DESC")
    fun getAllPublicProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE isFeatured = 1 AND status IN ('PUBLISHED', 'APPROVED') ORDER BY createdAt DESC")
    fun getFeaturedProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE status IN ('PUBLISHED', 'APPROVED') ORDER BY viewsCount DESC LIMIT 10")
    fun getPopularProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE ownerId = :ownerId ORDER BY updatedAt DESC")
    fun getProjectsByOwner(ownerId: String): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: String): Flow<Project?>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectDirect(id: String): Project?

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT COUNT(*) FROM projects")
    fun getTotalProjectsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE status = 'PENDING_REVIEW' OR status = 'UNDER_REVIEW'")
    fun getPendingReviewsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE status IN ('APPROVED', 'PUBLISHED')")
    fun getApprovedProjectsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE status = 'REJECTED'")
    fun getRejectedProjectsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE status = 'CHANGES_REQUESTED'")
    fun getChangesRequestedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: Project)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjects(projects: List<Project>)

    @Update
    suspend fun updateProject(project: Project)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)

    @Query("UPDATE projects SET likesCount = likesCount + 1 WHERE id = :id")
    suspend fun incrementLikes(id: String)

    @Query("UPDATE projects SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)
}

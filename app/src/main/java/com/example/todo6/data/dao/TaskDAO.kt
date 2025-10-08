package com.example.todo6.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todo6.data.model.Task

@Dao
interface TaskDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY id DESC")
    fun getTaskByUserId(userId: String): LiveData<List<Task>>

    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun deleteAllTasksByUserId(userId: String)

    @Query("""
        SELECT * FROM tasks
        WHERE userId = :userId
        AND (:query IS NULL OR title LIKE '%' || :query || '%')
        AND (:status IS NULL OR completed = :status)
        AND (:startDate IS NULL OR deadline >= :startDate)
        AND (:endDate IS NULL OR deadline <= :endDate)
        ORDER BY deadline DESC
    """)
    fun getFilteredTask(
        userId: String,
        query: String?,
        status: Boolean?,
        startDate: Long?,
        endDate: Long?
    ) : LiveData<List<Task>>
}
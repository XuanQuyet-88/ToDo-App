package com.example.todo6.data.repository

import androidx.lifecycle.LiveData
import com.example.todo6.data.dao.TaskDAO
import com.example.todo6.data.model.Task

class TaskRepository(private val taskDAO: TaskDAO) {

    suspend fun insert(task: Task) {
        taskDAO.insert(task)
    }

    suspend fun update(task: Task) {
        taskDAO.update(task)
    }

    suspend fun delete(task: Task) {
        taskDAO.delete(task)
    }

    fun getTaskByUserId(userId: String): LiveData<List<Task>> =
        taskDAO.getTaskByUserId(userId)

    suspend fun deleteAllTasksByUserId(userId: String) {
        taskDAO.deleteAllTasksByUserId(userId)
    }

    fun sortTasksByDateDesc(
        userId: String,
        query: String?,
        status: Boolean?,
        startDate: Long?,
        endDate: Long?
    ): LiveData<List<Task>>{
        return taskDAO.sortTasksByDateDesc(userId, query, status, startDate, endDate)
    }

    fun sortTasksByDateAsc(
        userId: String,
        query: String?,
        status: Boolean?,
        startDate: Long?,
        endDate: Long?
    ): LiveData<List<Task>>{
        return taskDAO.sortTasksByDateAsc(userId, query, status, startDate, endDate)
    }

    fun sortTasksByName(
        userId: String,
        query: String?,
        status: Boolean?,
        startDate: Long?,
        endDate: Long?
    ) : LiveData<List<Task>>{
        return taskDAO.sortTasksByName(userId, query, status, startDate, endDate)
    }
}
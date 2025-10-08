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
}
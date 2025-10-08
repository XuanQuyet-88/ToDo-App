package com.example.todo6.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo6.data.model.Task
import com.example.todo6.data.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun deleteAllTaskByUserId(userId: String) = viewModelScope.launch {
        repository.deleteAllTasksByUserId(userId)
    }

    fun getTaskByUserId(userId: String): LiveData<List<Task>> =
        repository.getTaskByUserId(userId)
}
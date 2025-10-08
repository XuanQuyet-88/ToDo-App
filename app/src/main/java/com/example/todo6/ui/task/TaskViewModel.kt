package com.example.todo6.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo6.data.model.Task
import com.example.todo6.data.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _searchQuery = MutableLiveData<String>(null)
    private val _filterStatus = MutableLiveData<Boolean?>(null)
    private val _filterDate = MutableLiveData<Pair<Long, Long>?>(null)

    val filteredTasks: LiveData<List<Task>>
    init {
        filteredTasks = MediatorLiveData<List<Task>>().apply {
            var source: LiveData<List<Task>>? = null

            val observer = Observer<Any?>{
                source?.let { removeSource(it) }

                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Observer
                val query = _searchQuery.value
                val status = _filterStatus.value
                val startDate = _filterDate.value?.first
                val endDate = _filterDate.value?.second

                source = repository.getFilteredTasks(userId, query, status, startDate, endDate)
                source.let { newSource ->
                    addSource(newSource){tasks ->
                        value = tasks ?: emptyList()
                    }
                }
            }
            addSource(_searchQuery, observer)
            addSource(_filterStatus, observer)
            addSource(_filterDate, observer)
        }
    }

    fun setSearchQuery(query: String?) {
        _searchQuery.value = if (query.isNullOrBlank()) null else query
    }

    fun setFilterStatus(status: Boolean?) {
        _filterStatus.value = status
    }

    fun setFilterDate(calendar: Calendar?) {
        if (calendar == null) {
            _filterDate.value = null
        } else {
            // Set to start of the day
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startDate = calendar.timeInMillis

            // Set to end of the day
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endDate = calendar.timeInMillis

            _filterDate.value = Pair(startDate, endDate)
        }
    }

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
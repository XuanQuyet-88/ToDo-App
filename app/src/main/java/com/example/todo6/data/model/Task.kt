package com.example.todo6.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey val id : String = "",
    val userId : String = "",
    var title: String = "",
    var description: String = "",
    var deadline: Long? = null,
    var completed: Boolean = false
): Serializable
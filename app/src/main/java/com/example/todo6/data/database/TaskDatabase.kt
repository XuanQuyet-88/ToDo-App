package com.example.todo6.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todo6.data.dao.TaskDAO
import com.example.todo6.data.model.Task

@Database(entities = [Task::class], version = 2, exportSchema = false)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao() : TaskDAO

    companion object{
        @Volatile
        private var instance : TaskDatabase? = null

        fun getInstance(context: Context): TaskDatabase{
            return instance?: synchronized(this){
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}
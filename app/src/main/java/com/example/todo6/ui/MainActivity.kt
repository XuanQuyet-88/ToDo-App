package com.example.todo6.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todo6.R
import com.example.todo6.ui.fragments.HomeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        createNotificationChannel()
        handleNotificationIntent(intent)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                "task_channel",
                "Task reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Nhắc nhở công việc"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun handleNotificationIntent(intent: Intent){
        val taskId = intent?.getStringExtra("opendTask")
        if(taskId != null){
            val bundle = Bundle()
            bundle.putString("opendTask", taskId)
            val homeFragment = HomeFragment()
            homeFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, homeFragment)
                .commit()
        }
    }
}
package com.example.todo6.ui.task

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.todo6.data.database.TaskDatabase
import com.example.todo6.data.model.Task
import com.example.todo6.data.repository.TaskRepository
import com.example.todo6.databinding.FragmentAddTaskPopupBinding
import com.example.todo6.utils.NotificationReceiver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar
import java.util.UUID

class AddTaskPopupFragment : DialogFragment() {
    private lateinit var binding: FragmentAddTaskPopupBinding
    private var reminderTime: Long? = null
    private val taskViewModel: TaskViewModel by viewModels {
        val taskDao = TaskDatabase.getInstance(requireContext()).taskDao()
        val repository = TaskRepository(taskDao)
        TaskViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddTaskPopupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSetTime.setOnClickListener { pickDateTime() }
        binding.btnClosePopup.setOnClickListener { dismiss() }
        binding.btnAddTask.setOnClickListener { addTaskBoth() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        binding = FragmentAddTaskPopupBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    private fun addTaskBoth() {
        binding.progressCircular.visibility = View.VISIBLE
        val taskTitle = binding.edtTask.text.toString().trim()
        val taskDesc = binding.edtDescTask.text.toString().trim()
        if (taskTitle.isEmpty()) {
            binding.progressCircular.visibility = View.GONE
            binding.edtTask.error = "Please enter task"
            binding.edtTask.requestFocus()
        }
        if (taskDesc.isEmpty()) {
            binding.progressCircular.visibility = View.GONE
            binding.edtDescTask.error = "Please enter task description"
            binding.edtDescTask.requestFocus()
        }
        if(binding.tvReminder.text == "No Deadline"){
            binding.progressCircular.visibility = View.GONE
            Toast.makeText(requireActivity(), "Please set time", Toast.LENGTH_SHORT).show()
            return
        }
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
        val auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid
        val taskId = UUID.randomUUID().toString()
        val newTask = Task(
            id = taskId,
            userId = currentUserId!!,
            title = taskTitle,
            description = taskDesc,
            deadline = reminderTime,
            completed = false
        )
        //add to room database
        taskViewModel.insert(newTask)
        scheduleNotification(newTask)
        //add to firebase
        dbRef.child(currentUserId).child(taskId).setValue(newTask)
            .addOnSuccessListener {
                binding.progressCircular.visibility = View.GONE
                Toast.makeText(requireActivity(), "Task added", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener {
                binding.progressCircular.visibility = View.GONE
                    Toast.makeText(requireActivity(), "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun scheduleNotification(task: Task){
        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("title", task.title)
            putExtra("desc", task.description)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            task.deadline ?: return,
            pendingIntent
        )
    }

    private fun pickDateTime() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

                TimePickerDialog(
                    requireContext(),
                    { _, hour, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        reminderTime = calendar.timeInMillis
                        binding.tvReminder.text =
                            "Nhắc lúc: $day/${month + 1}/$year $hour:$minute"
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
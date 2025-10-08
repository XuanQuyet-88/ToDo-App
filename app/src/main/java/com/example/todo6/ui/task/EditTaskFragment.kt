package com.example.todo6.ui.task

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.Dialog
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
import com.example.todo6.databinding.FragmentEditTaskBinding
import com.example.todo6.utils.NotificationReceiver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTaskFragment() : DialogFragment() {
    private lateinit var binding: FragmentEditTaskBinding
    private var task: Task? = null
    private var newDeadline : Long? = null

    companion object{
        fun newIntance(task: Task): EditTaskFragment{
            val fragment = EditTaskFragment()
            val bundle = Bundle()
            bundle.putSerializable("task_edit", task)
            fragment.arguments = bundle
            return fragment
        }
    }
    private val taskViewModel : TaskViewModel by viewModels {
        val dao = TaskDatabase.getInstance(requireContext()).taskDao()
        val repo = TaskRepository(dao)
        TaskViewModelFactory(repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = arguments?.getSerializable("task_edit") as? Task
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditTaskBinding.inflate(LayoutInflater.from(context))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        task?.let {
            binding.edtTask.setText(it.title)
            binding.edtDescTask.setText(it.description)

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val deadlineText = it.deadline?.let { d -> sdf.format(d) } ?: "No deadline"
            binding.tvReminder.text = deadlineText
        }
        binding.btnSetTime.setOnClickListener { pickDateTime() }
        binding.btnClosePopup.setOnClickListener { dismiss() }
        binding.btnUpdateTask.setOnClickListener { updateTask() }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        binding = FragmentEditTaskBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
    private fun updateTask() {
        cancelOldNotification(task)
        val newTitle: String = binding.edtTask.text.toString().trim()
        val newDesc: String = binding.edtDescTask.text.toString().trim()
        val newDeadline = newDeadline?: task?.deadline

        if(newTitle.isEmpty() == true){
            binding.edtTask.error = "You must enter your task title"
        }

        val updatedTask = task?.copy(
            title = newTitle,
            description = newDesc,
            deadline = newDeadline
        )?: return

        val  currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
            .child(currentUserId)
            .child(updatedTask.id)

        dbRef.setValue(updatedTask)
            .addOnSuccessListener {
                taskViewModel.update(updatedTask)
                scheduleNotification(updatedTask)
                Toast.makeText(requireContext(), "Đã cập nhật task", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun cancelOldNotification(task: Task?) {
        if (task == null) return

        val intent = Intent(requireContext(), NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun scheduleNotification(task: Task){
        if (task.deadline == null) return

        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("taskTitle", task.title)
            putExtra("taskDesc", task.description)
            putExtra("taskId", task.id)
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
                        newDeadline = calendar.timeInMillis
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
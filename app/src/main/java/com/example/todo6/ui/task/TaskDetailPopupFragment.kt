package com.example.todo6.ui.task

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.todo6.data.model.Task
import com.example.todo6.databinding.FragmentTaskDetailPopupBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TaskDetailPopupFragment() : DialogFragment() {

    private lateinit var binding: FragmentTaskDetailPopupBinding
    private lateinit var task: Task
    companion object{
        fun newInstance(task: Task): TaskDetailPopupFragment{
            val fragment = TaskDetailPopupFragment()
            val bundle = Bundle()
            bundle.putSerializable("task", task)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        task = arguments?.getSerializable("task") as Task
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        binding = FragmentTaskDetailPopupBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val deadlineText = task.deadline?.let { sdf.format(it) } ?: "No deadline"

        binding.tvTaskTitle.text = task.title
        binding.tvTaskDescription.text = task.description
        binding.tvTaskDeadline.text = "Deadline: $deadlineText"
        binding.tvTaskStatus.text =
            if (task.completed == true) "Status: Completed" else "Status: Pending"
        binding.btnCloseDetail.setOnClickListener { dismiss() }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setDimAmount(0.6f)
    }
}

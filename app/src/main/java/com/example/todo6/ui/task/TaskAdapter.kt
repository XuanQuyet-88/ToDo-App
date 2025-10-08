package com.example.todo6.ui.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.todo6.R
import com.example.todo6.data.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    val taskList: MutableList<Task>,
    private val onItemClick: (Task) -> Unit,
    private val onTaskCheckedChange: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit,
    private val onTaskEdit: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TaskViewHolder, position: Int
    ) {
        holder.itemView.apply {
            val tvTitle = findViewById<TextView>(R.id.todoTask)
            val tvTaskDeadline: TextView = findViewById(R.id.tvTaskDeadline)
            var checkBox: CheckBox = findViewById(R.id.cbTaskCheck)
            val btnDelete = findViewById<ImageView>(R.id.deleteTask)
            val btnEdit = findViewById<ImageView>(R.id.editTask)
            if(taskList[position].deadline != null){
                val date = Date(taskList[position].deadline!!)
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                tvTaskDeadline.text = sdf.format(date)
            }else{
                tvTaskDeadline.text = "No deadline"
            }
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = taskList[position].completed
            tvTitle.text = taskList[position].title
            btnDelete.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Xóa công việc")
                    .setMessage("Bạn có chắc muốn xóa công việc này không?")
                    .setPositiveButton("Xóa") { dialog, _ ->
                        onTaskDelete(taskList[position])
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") {dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            btnEdit.setOnClickListener {
                onTaskEdit(taskList[position])
            }
            setOnClickListener {
                onItemClick(taskList[position])
            }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                taskList[position].completed = isChecked
                onTaskCheckedChange(taskList[position])
            }
            setOnClickListener {
                if (checkBox.isPressed) return@setOnClickListener
                onItemClick(taskList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}
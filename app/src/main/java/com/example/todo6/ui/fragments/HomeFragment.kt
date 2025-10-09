package com.example.todo6.ui.fragments

import  android.app.DatePickerDialog
import java.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todo6.R
import com.example.todo6.data.database.TaskDatabase
import com.example.todo6.data.model.Task
import com.example.todo6.data.repository.TaskRepository
import com.example.todo6.databinding.FragmentHomeBinding
import com.example.todo6.ui.task.AddTaskPopupFragment
import com.example.todo6.ui.task.EditTaskFragment
import com.example.todo6.ui.task.TaskAdapter
import com.example.todo6.ui.task.TaskDetailPopupFragment
import com.example.todo6.ui.task.TaskViewModel
import com.example.todo6.ui.task.TaskViewModelFactory
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popupAddFragment : AddTaskPopupFragment
    private lateinit var currentUserId : String
    private lateinit var dbRef : DatabaseReference
    private lateinit var navController: NavController
    private lateinit var taskAdpater : TaskAdapter

    //filter theo ngay
    private var selectedStartDate: Calendar? = null
    private var selectedEndDate: Calendar? = null
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
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val openTaskId = arguments?.getString("opendTask")
        if(openTaskId != null){
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                taskViewModel.getTaskByUserId(currentUserId).observe(viewLifecycleOwner) { tasks ->
                    val index = tasks.indexOfFirst { it.id == openTaskId }
                    if (index != -1) {
                        binding.rvTasks.scrollToPosition(index)
                        val task = tasks[index]
                        val detailPopup = TaskDetailPopupFragment.newInstance(task)
                        detailPopup.show(parentFragmentManager, "TaskDetail")
                    }
                }
            }
        }
        init(view)
        registerEvents()
        registerFilterEvents()
    }

    private fun registerFilterEvents() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                taskViewModel.setSearchQuery(newText)
                return true
            }
        })

        val headerView = binding.navigationView.getHeaderView(0)
        val chipGroupStatus = headerView.findViewById<ChipGroup>(R.id.chipGroupStatus)
        val tvStartDate = headerView.findViewById<TextView>(R.id.tvStartDate)
        val tvEndDate = headerView.findViewById<TextView>(R.id.tvEndDate)
        val btnClearDateFilter = headerView.findViewById<View>(R.id.btnClearDateFilter)

        chipGroupStatus.setOnCheckedChangeListener { _, checkedId ->
            val status = when(checkedId){
                R.id.chipCompleted -> true
                R.id.chipPending -> false
                else -> null
            }
            taskViewModel.setFilterStatus(status)
        }

        tvStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
                    selectedStartDate = selectedDate
                    taskViewModel.setFilterStartDate(selectedDate)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    tvStartDate.text = sdf.format(selectedDate.time)
                    btnClearDateFilter.visibility = View.VISIBLE
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            selectedEndDate?.let {
                datePickerDialog.datePicker.maxDate = it.timeInMillis
            }
            datePickerDialog.show()
        }

        tvEndDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                {_, year, month, dayOfMoth ->
                    val selectedDate = Calendar.getInstance().apply { set(year, month, dayOfMoth) }
                    selectedEndDate = selectedDate
                    taskViewModel.setFilterEndDate(selectedDate)
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    tvEndDate.text = sdf.format(selectedDate.time)
                    btnClearDateFilter.visibility = View.VISIBLE
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            selectedStartDate?.let {
                datePickerDialog.datePicker.minDate = it.timeInMillis
            }
            datePickerDialog.show()
        }

        btnClearDateFilter.setOnClickListener {
            taskViewModel.setFilterStartDate(null)
            taskViewModel.setFilterEndDate(null)
            selectedStartDate = null
            selectedEndDate = null
            tvStartDate.text = "Từ ngày"
            tvEndDate.text = "Đến ngày"
            it.visibility = View.GONE
        }
    }

    private fun init(view: View) {
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        currentUserId = auth.currentUser?.uid.toString()
        navController = view.findNavController()
        taskAdpater = TaskAdapter(mutableListOf(),
            onItemClick = {task ->
            val dialogDetail = TaskDetailPopupFragment.newInstance(task)
            dialogDetail.show(childFragmentManager, "TaskDetailPopup")
        },
            onTaskCheckedChange = {updatedTask ->
                //update to room db
                taskViewModel.update(updatedTask)
                //update to firebase
                dbRef.child(updatedTask.userId)
                    .child(updatedTask.id)
                    .child("completed")
                    .setValue(updatedTask.completed)
            },
            onTaskDelete = {task ->
                //delete on room db
                taskViewModel.delete(task)
                //delete on firebase
                dbRef.child(currentUserId).child(task.id).removeValue()
                Toast.makeText(requireContext(), "Delete Successfully", Toast.LENGTH_SHORT).show()
            },
            onTaskEdit = {task ->
                val editPopup = EditTaskFragment.newIntance(task)
                editPopup.show(childFragmentManager, "EditTask")
            })
    }
    private fun registerEvents() {
        //hien thi navigationView
        binding.btnShowListOptions.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        //nhan nut them task
        binding.btnAddTask.setOnClickListener {
            popupAddFragment = AddTaskPopupFragment()
            popupAddFragment.show(childFragmentManager, "popupAddFragment")
        }
        //set su kien cho navigationView
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.nav_home -> {
                    Toast.makeText(requireActivity(), "Home", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_exit -> {
                    requireActivity().finish()
                }
                R.id.nav_logout -> {
                    handleLogout()
                }
            }
            binding.drawerLayout.openDrawer(GravityCompat.START)
            true
        }
        //setup cho recyclerView
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.rvTasks.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = taskAdpater
        }

        //observe data tu room db
//        taskViewModel.getTaskByUserId(currentUserId).observe(viewLifecycleOwner){tasks ->
//            if(tasks.isNotEmpty()){
//                taskAdpater.apply {
//                    taskList.clear()
//                    taskList.addAll(tasks)
//                    notifyDataSetChanged()
//                }
//            }else{
//                loadTasksFromFB()
//            }
//        }

        taskViewModel.filteredTasks.observe(viewLifecycleOwner){ tasks ->
            taskAdpater.updatedTask(tasks)
        }

        loadTasksFromFB()
    }

    private fun loadTasksFromFB() {
        dbRef.child(currentUserId).get().addOnSuccessListener { snapshot ->
            if(snapshot.exists()){
                for(taskSnap in snapshot.children){
                    val task = taskSnap.getValue(Task::class.java)
                    if(task != null){
                        taskViewModel.insert(task)
                    }
                }
            }else{
                Toast.makeText(requireContext(), "Không có dữ liệu trên Firebase", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Lỗi khi load Firebase: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleLogout() {
        val userNeedToClear = currentUserId
        taskViewModel.deleteAllTaskByUserId(currentUserId)
        auth.signOut()
        navController.navigate(R.id.action_homeFragment_to_signInFragment)
    }
}
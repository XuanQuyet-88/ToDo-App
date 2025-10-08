package com.example.todo6.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentHomeBinding
    private lateinit var popupAddFragment : AddTaskPopupFragment
    private lateinit var currentUserId : String
    private lateinit var dbRef : DatabaseReference
    private lateinit var navController: NavController
    private lateinit var taskAdpater : TaskAdapter
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
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })

        binding.chipGroupStatus?.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.chipAll -> taskViewModel.setFilterStatus(null)
                R.id.chipCompleted -> taskViewModel.setFilterStatus(true)
                R.id.chipPending -> taskViewModel.setFilterStatus(false)
            }
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
        taskViewModel.getTaskByUserId(currentUserId).observe(viewLifecycleOwner){tasks ->
            if(tasks.isNotEmpty()){
                taskAdpater.apply {
                    taskList.clear()
                    taskList.addAll(tasks)
                    notifyDataSetChanged()
                }
            }else{
                loadTasksFromFB()
            }
        }
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
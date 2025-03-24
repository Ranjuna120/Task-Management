package com.example.mad3

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mad3.databinding.ActivityTasksBinding

class Tasks : AppCompatActivity() {

    private lateinit var binding: ActivityTasksBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<String>() // Store task list in memory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTasksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("NoteData", Context.MODE_PRIVATE)

        // Setup RecyclerView
        taskAdapter = TaskAdapter(taskList) { position ->
            deleteTask(position)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = taskAdapter

        // Load previously saved tasks
        loadTasks()

        // Save button click listener
        binding.btnSave.setOnClickListener {
            val note = binding.editText.text.toString()

            if (note.isNotEmpty()) {
                // Save the task to SharedPreferences
                taskList.add(note)
                saveTasks()

                // Update the RecyclerView
                taskAdapter.notifyDataSetChanged()

                // Clear the EditText
                binding.editText.text.clear()

                Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTasks() {
        // Retrieve tasks from SharedPreferences and add them to the taskList
        val tasksSet = sharedPreferences.getStringSet("tasks", emptySet()) ?: emptySet()
        taskList.addAll(tasksSet)
        taskAdapter.notifyDataSetChanged()
    }

    private fun saveTasks() {
        // Save the current taskList to SharedPreferences
        val tasksSet = taskList.toSet()
        val editor = sharedPreferences.edit()
        editor.putStringSet("tasks", tasksSet)
        editor.apply()
    }

    private fun deleteTask(position: Int) {
        // Remove task from list
        taskList.removeAt(position)

        // Update SharedPreferences
        saveTasks()

        // Notify adapter of changes
        taskAdapter.notifyItemRemoved(position)

        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
    }
}

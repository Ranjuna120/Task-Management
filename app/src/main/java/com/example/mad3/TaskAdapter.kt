package com.example.mad3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: MutableList<String>,
    private val onDelete: (Int) -> Unit  // Callback for delete action
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTextView: TextView = itemView.findViewById(R.id.task_text_view)
        val deleteButton: Button = itemView.findViewById(R.id.btn_delete)  // Reference to delete button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTextView.text = task

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onDelete(position)  // Call the delete callback when clicked
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}


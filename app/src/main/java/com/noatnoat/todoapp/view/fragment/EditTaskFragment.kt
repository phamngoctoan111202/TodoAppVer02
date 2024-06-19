package com.noatnoat.todoapp.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.noatnoat.todoapp.R
import com.noatnoat.todoapp.databinding.DialogEditTaskBinding
import com.noatnoat.todoapp.model.room.Task
import com.noatnoat.todoapp.viewmodel.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class EditTaskFragment : DialogFragment() {
    private lateinit var binding: DialogEditTaskBinding
    private val viewModel: ViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    fun show(task: Task) {
        binding.task = task

        binding.btnUpdateTask.setOnClickListener {
            val updatedTask = task.copy(
                title = binding.taskTitle.text.toString(),
                description = binding.taskDescription.text.toString()
            )
            viewModel.update(updatedTask)
            dismiss()
        }

        val taskDueDate = binding.root.findViewById<TextView>(R.id.taskDueDate)
        taskDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    taskDueDate.setText(SimpleDateFormat("dd/MM/yyyy").format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        dialog?.show()
    }
}
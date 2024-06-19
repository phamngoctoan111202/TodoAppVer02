package com.noatnoat.todoapp.view.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.noatnoat.todoapp.R
import com.noatnoat.todoapp.ad.InterstitialAdManager
import com.noatnoat.todoapp.databinding.FragmentNewTaskBinding
import com.noatnoat.todoapp.enums.Priority
import com.noatnoat.todoapp.model.room.Task
import com.noatnoat.todoapp.viewmodel.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class NewTaskFragment : Fragment() {
    private lateinit var binding: FragmentNewTaskBinding
    private var selectedDueDate: Long? = null
    private val viewModel: ViewModel by viewModels()
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewTaskBinding.inflate(inflater, container, false)

        binding.dueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDueDate = calendar.timeInMillis
                    binding.dueDate.text = SimpleDateFormat("dd/MM/yyyy").format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.btnAddTask.setOnClickListener {
            if (binding.taskTitle.text.toString().isBlank() || binding.taskDescription.text.toString().isBlank() || selectedDueDate == null) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            } else {
                val newTask = Task(
                    title = binding.taskTitle.text.toString(),
                    description = binding.taskDescription.text.toString(),
                    priority = Priority.valueOf(binding.priority.text.toString()),
                    isRepeating = binding.taskIsRepeating.isChecked,
                    createdDate = System.currentTimeMillis(),
                    dueDate = selectedDueDate
                )
                viewModel.insert(newTask)
                parentFragmentManager.popBackStack()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInterstitialAdManager()
    }

    private fun setupInterstitialAdManager() {
        interstitialAdManager = InterstitialAdManager(requireActivity())
        interstitialAdManager.loadAd()
        if (interstitialAdManager.isAdLoaded) {
            interstitialAdManager.showAd()
        }
    }

    override fun onPause() {
        super.onPause()
    }

}
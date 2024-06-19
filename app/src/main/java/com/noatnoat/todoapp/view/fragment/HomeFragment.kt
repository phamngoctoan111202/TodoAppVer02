package com.noatnoat.todoapp.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.noatnoat.todoapp.R
import com.noatnoat.todoapp.databinding.FragmentHomeBinding
import com.noatnoat.todoapp.model.room.Task
import com.noatnoat.todoapp.view.adapter.TaskAdapter
import com.noatnoat.todoapp.viewmodel.ViewModel
import android.app.DatePickerDialog
import android.app.Dialog
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.noatnoat.todoapp.ad.AdConfig
import com.noatnoat.todoapp.ad.AdManager
import com.noatnoat.todoapp.ad.BannerAdManager
import com.noatnoat.todoapp.ad.InterstitialAdManager
import com.noatnoat.todoapp.databinding.DialogEditTaskBinding
import com.noatnoat.todoapp.enums.Priority
import com.noatnoat.todoapp.enums.TaskStatus
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.noatnoat.todoapp.ad.NativeAdManager
import com.noatnoat.todoapp.ad.RewardAdListener
import com.noatnoat.todoapp.ad.RewardedAdManager

@AndroidEntryPoint
class HomeFragment : Fragment(), TaskAdapter.OnTaskClickListener, RewardAdListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var taskAdapter: TaskAdapter
    private val viewModel: ViewModel by viewModels()
    private lateinit var bannerAdManager: BannerAdManager
    private lateinit var nativeAdManager: NativeAdManager
    private lateinit var adContainer: ViewGroup
    private lateinit var adManager: AdManager
    private lateinit var rewardedAdManager: RewardedAdManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        taskAdapter = TaskAdapter(listOf(), this) // Initialize with empty list
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.adapter = taskAdapter

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.tasks = tasks
            taskAdapter.notifyDataSetChanged()

            if (tasks.isEmpty()) {
                binding.emptyTasksImageView.visibility = View.VISIBLE
                binding.emptyTasksTextView.visibility = View.VISIBLE
            } else {
                binding.emptyTasksImageView.visibility = View.GONE
                binding.emptyTasksTextView.visibility = View.GONE
            }
        }


        binding.fabAddTask.setOnClickListener {
            rewardedAdManager.showRewardedAd()

        }



        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bannerAdManager = BannerAdManager(requireActivity())
        adContainer = view.findViewById(R.id.ad_container) // Replace with your actual ad container
        nativeAdManager = NativeAdManager(requireActivity())
        bannerAdManager.loadAd(binding.adView)
        nativeAdManager = NativeAdManager(requireActivity())
        nativeAdManager.loadAd(binding.adContainer)
        adManager = AdManager(requireActivity(), this)
        adManager.loadRewardedAd()
        rewardedAdManager = RewardedAdManager(requireActivity(), this)
        rewardedAdManager.loadRewardedAd(AdConfig.REWARDED_AD_ID) // Replace with your actual ad unit id
    }




    override fun onTaskClick(task: Task) {
        val dialog = Dialog(requireActivity(), R.style.FullWidth_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val inflater = LayoutInflater.from(requireActivity())
        val binding: DialogEditTaskBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_edit_task, null, false)
        dialog.setContentView(binding.root)

        binding.task = task
        dialog.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT

        val layoutParams = dialog.window!!.attributes
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = layoutParams

        val dueDate = task.dueDate
        if (dueDate != null) {
            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val dueDateString = sdf.format(Date(dueDate))
            binding.taskDueDate.text = dueDateString
        }

        val taskStatus = binding.root.findViewById<TextView>(R.id.taskStatus)
        taskStatus.setOnClickListener {
            task.status = when (task.status) {
                TaskStatus.START -> TaskStatus.PROGRESS
                TaskStatus.PROGRESS -> TaskStatus.START
                TaskStatus.COMPLETED -> TaskStatus.START
            }
            taskStatus.text = task.status.toString()
        }

        val taskPriority = binding.root.findViewById<TextView>(R.id.taskPriority)
        taskPriority.setOnClickListener {
            task.priority = when (task.priority) {
                Priority.HIGH -> Priority.MEDIUM
                Priority.MEDIUM -> Priority.LOW
                Priority.LOW -> Priority.HIGH
            }
            taskPriority.text = task.priority.toString()
        }


        binding.btnUpdateTask.setOnClickListener {
            val updatedTask = task.copy(
                title = binding.taskTitle.text.toString(),
                description = binding.taskDescription.text.toString(),
                status = task.status,
                dueDate = task.dueDate,
                priority = task.priority
            )
            viewModel.update(updatedTask)
            dialog.dismiss()
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.show()

        val taskDueDate = binding.root.findViewById<TextView>(R.id.taskDueDate)
        taskDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                R.style.DatePickerDialogTheme,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    task.dueDate = calendar.timeInMillis
                    taskDueDate.text = SimpleDateFormat("dd/MM/yy").format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    override fun onDeleteClick(task: Task) {
        viewModel.delete(task)
    }

    override fun onAdRewarded() {
        val newTaskFragment = NewTaskFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, newTaskFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onAdFailedToShow() {
    }
}
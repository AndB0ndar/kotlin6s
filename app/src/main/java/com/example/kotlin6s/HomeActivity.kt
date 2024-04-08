package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin6s.adapter.QueueListAdapter
import com.example.kotlin6s.databinding.ActivityHomeBinding
import com.example.kotlin6s.model.GroupResponse
import com.example.kotlin6s.model.ScheduleItem
import com.example.kotlin6s.viewmodule.HomeViewModel
import java.util.Calendar


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var groupViewModel: HomeViewModel

    private lateinit var queueAdapter: QueueListAdapter
    private val queueList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        groupViewModel = ViewModelProvider(this)[HomeViewModel::class.java]


        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.buttonClear.visibility = View.GONE
                    queueAdapter.filter("")
                } else {
                    binding.buttonClear.visibility = View.VISIBLE
                    queueAdapter.filter(s.toString().trim())
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.buttonClear.setOnClickListener {
            binding.editTextSearch.text.clear()
        }
        binding.buttonRetry.setOnClickListener {
            fetchQueueData()
        }

        binding.recyclerViewQueueList.layoutManager = LinearLayoutManager(this)
        queueAdapter = QueueListAdapter(queueList)
        binding.recyclerViewQueueList.adapter = queueAdapter
        queueAdapter.setOnClickListener(object : QueueListAdapter.OnClickListener {
            override fun onClick(position: Int) {
                navigateToQueue()
            }
        })

        fetchQueueData()

        binding.buttonAddQueue.setOnClickListener {
            navigateToAddQueue()
        }
        binding.imageViewProfile.setOnClickListener {
            navigateToProfile()
        }
    }

    private fun fetchThisWeekLessons(groupResponseList: List<GroupResponse?>): List<ScheduleItem> {
        val thisWeekLessons = mutableListOf<ScheduleItem>()
        val isEvenWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) % 2 == 0

        Log.d("HomeActivity", "isEvenWeek $isEvenWeek")

        for (groupResponse in groupResponseList) {
            groupResponse?.let {
                for (daySchedule in it.schedule) {
                    val lessons = if (isEvenWeek) daySchedule.even else daySchedule.odd
                    for (lessonList in lessons) {
                        for (lesson in lessonList) {
                            if (lesson.weeks.isNullOrEmpty() || lesson.weeks.contains("1")) {
                                thisWeekLessons.add(lesson)
                            }
                        }
                    }
                }
            }
        }

        return thisWeekLessons
    }

    private fun fetchQueueData() {
        Log.d("HomeActivity", "Response")
        groupViewModel.getGroupData("ИКБ-06-21").observe(this, Observer { groupResponse ->
            groupResponse?.let {
                queueList.clear()
                Log.d("HomeActivity", "START")
                val thisWeekLessons = fetchThisWeekLessons(groupResponse)
                for (lesson in thisWeekLessons) {
                    if (!queueList.contains(lesson.name)) {
                        Log.d("HomeActivity", lesson.name)
                        queueList.add(lesson.name)
                    }
                }
                Log.d("HomeActivity", queueList.size.toString())

                queueList.add("example item")

                queueAdapter.notifyDataSetChanged()
                queueAdapter.filter("")
                if (queueList.isEmpty())
                    binding.textViewError.visibility = View.VISIBLE
                else
                    binding.textViewError.visibility = View.GONE
            }
        })
        groupViewModel.getError().observe(this, Observer { error ->
            if (error.isNullOrEmpty()) {
                Log.d("HomeActivity", "groupResponse ERROR")
                binding.recyclerViewQueueList.visibility = View.GONE
                binding.layoutError.visibility = View.VISIBLE
            }
        })
    }

    private fun navigateToAddQueue() {
        val intent = Intent(this, AddQueueActivity::class.java)
        Log.d("HomeActivity", "Go Add Queue")
        startActivity(intent)
    }

    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)
        Log.d("HomeActivity", "Go Profile")
        startActivity(intent)
    }

    private fun navigateToQueue() {
        val intent = Intent(this, QueueActivity::class.java)
        Log.d("HomeActivity", "Go Queue")
        startActivity(intent)
    }

    companion object {
        const val EDIT_TEXT_STATE_KEY = "EDIT_TEXT_STATE_KEY"
        const val SEARCH_DEF = ""
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT_STATE_KEY, binding.editTextSearch.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val editTextState = savedInstanceState.getString(EDIT_TEXT_STATE_KEY, SEARCH_DEF)
        binding.editTextSearch.setText(editTextState)
    }
}
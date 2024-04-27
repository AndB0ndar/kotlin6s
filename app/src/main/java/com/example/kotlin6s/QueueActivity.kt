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
import com.example.kotlin6s.adapter.QueueAdapter
import com.example.kotlin6s.adapter.QueueListAdapter
import com.example.kotlin6s.adapter.SearchHistoryAdapter
import com.example.kotlin6s.databinding.ActivityQueueBinding
import com.example.kotlin6s.model.GroupResponse
import com.example.kotlin6s.model.ScheduleItem
import com.example.kotlin6s.viewmodule.HomeViewModel
import com.example.kotlin6s.viewmodule.QueueViewModel
import java.util.Calendar

class QueueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQueueBinding
    private lateinit var viewModel: QueueViewModel

    private lateinit var queueAdapter: QueueAdapter
    private val queueList: MutableList<String> = mutableListOf()
    private lateinit var groupName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[QueueViewModel::class.java]

        groupName = intent.getStringExtra("group").toString()
//        fetchQueueData(groupName)
        fetchQueueData("ИКБО-06-21")

        //
        // RecyclerView
        //
        binding.recyclerViewQueueList.layoutManager = LinearLayoutManager(this)
        queueAdapter = QueueAdapter(queueList)
        binding.recyclerViewQueueList.adapter = queueAdapter

        //
        // search
        //
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNullOrEmpty()) {
                    binding.buttonClear.visibility = View.GONE
                    queueAdapter.clear()
                } else {
                    binding.buttonClear.visibility = View.VISIBLE
                    queueAdapter.filter(query)
                }
                queueAdapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.buttonClear.setOnClickListener {
            binding.editTextSearch.text.clear()
            queueAdapter.clear()
        }


        binding.imageBack.setOnClickListener {
            navigateToHome()
        }
        binding.buttonAdd.setOnClickListener {
        }

        binding.buttonRemove.setOnClickListener {
        }
    }

    private fun fetchThisWeekLessons(groupResponseList: List<GroupResponse?>): List<ScheduleItem> {
        val thisWeekLessons = mutableListOf<ScheduleItem>()
        val isEvenWeek = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) % 2 == 0

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

    private fun fetchQueueData(groupName: String) {
        viewModel.getGroupData(groupName).observe(this, Observer { groupResponse ->
            groupResponse?.let {
                queueList.clear()
                val thisWeekLessons = fetchThisWeekLessons(groupResponse)
                for (lesson in thisWeekLessons) {
                    if (!queueList.contains(lesson.name)) {
                        queueList.add(lesson.name)
                    }
                }
                queueList.add("example item")

                queueAdapter.notifyDataSetChanged()
                queueAdapter.clear()
            }
        })
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        Log.d("QueueActivity", "Go Home")
        intent.putExtra("group", groupName)
        startActivity(intent)
    }
}
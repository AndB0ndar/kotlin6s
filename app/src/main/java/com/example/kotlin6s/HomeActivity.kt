package com.example.kotlin6s

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin6s.adapter.QueueListAdapter
import com.example.kotlin6s.adapter.SearchHistoryAdapter
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


    private var searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable = Runnable { searchRequest() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        groupViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)


        //
        // search history
        //
        val searchHistory = getSearchHistory()
        if (searchHistory.isNotEmpty()) {
            binding.recyclerViewSearchHistory.layoutManager = LinearLayoutManager(this)
            val historyAdapter = SearchHistoryAdapter(searchHistory)
            binding.recyclerViewSearchHistory.adapter = historyAdapter
            historyAdapter.setOnClickListener(object : SearchHistoryAdapter.OnClickListener {
                override fun onClick(query: String) {
                    binding.editTextSearch.setText(query)
                }
            })
        }
        binding.buttonClearHistory.setOnClickListener {
            clearSearchHistory()
            binding.historyLayout.visibility = View.GONE
        }

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
        binding.editTextSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (searchHistory.isNotEmpty()) {
                    binding.historyLayout.visibility = View.VISIBLE
                }
            } else {
                binding.historyLayout.visibility = View.GONE
            }
        }
        binding.buttonClear.setOnClickListener {
            binding.editTextSearch.text.clear()
        }

        //
        // placeholder
        //
        binding.editTextRequest.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.buttonRetry.setOnClickListener {
            searchRequest()
            //fetchQueueData("ИКБО-06-21")
        }

        //
        // RecycleView
        //
        binding.recyclerViewQueueList.layoutManager = LinearLayoutManager(this)
        queueAdapter = QueueListAdapter(queueList)
        binding.recyclerViewQueueList.adapter = queueAdapter
        queueAdapter.setOnClickListener(object : QueueListAdapter.OnClickListener {
            override fun onClick(position: String?) {
                navigateToQueue(position!!)
                addSearchQuery(position)
            }
        })

        //
        // navigation
        //
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
        binding.layoutRequest.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        groupViewModel.getGroupData(groupName).observe(this, Observer { groupResponse ->
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
                if (queueList.isEmpty()) {
                    binding.textViewError.visibility = View.VISIBLE
                    binding.recyclerViewQueueList.visibility = View.GONE
                } else {
                    binding.textViewError.visibility = View.GONE
                    binding.layoutRequest.visibility = View.GONE
                    binding.recyclerViewQueueList.visibility = View.VISIBLE
                    binding.editTextSearch.visibility = View.VISIBLE
                }
            }
        })

        binding.progressBar.visibility = View.GONE

        groupViewModel.getError().observe(this, Observer { error ->
            if (error.isNullOrEmpty()) {
                binding.recyclerViewQueueList.visibility = View.GONE
                binding.editTextSearch.visibility = View.GONE
                binding.layoutRequest.visibility = View.VISIBLE
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

    private fun navigateToQueue(groupName: String) {
        Log.d("HomeActivity", "Go Queue")
        val intent = Intent(this, QueueActivity::class.java)
        intent.putExtra("group", groupName)
        startActivity(intent)
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

    fun addSearchQuery(query: String?) {
        var searchHistory: MutableList<String?> = getSearchHistory()

        searchHistory.remove(query)
        searchHistory.add(0, query)

        if (searchHistory.size > MAX_HISTORY_SIZE) {
            searchHistory = searchHistory.subList(0, MAX_HISTORY_SIZE)
        }

        val sharedPrefs = getSharedPreferences(HISTORY_PREFERENCES, Context.MODE_PRIVATE)
        val editor = sharedPrefs?.edit()
        editor?.putStringSet(SEARCH_HISTORY_KEY, HashSet(searchHistory))
        editor?.apply()
    }

    fun getSearchHistory(): MutableList<String?> {
        val preferences = getSharedPreferences(HISTORY_PREFERENCES, Context.MODE_PRIVATE)
        val historySet = preferences.getStringSet(SEARCH_HISTORY_KEY, HashSet<String>())

        return ArrayList(historySet)
    }

    fun clearSearchHistory() {
        val preferences = getSharedPreferences(HISTORY_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(SEARCH_HISTORY_KEY)
        editor.apply()
    }

    private fun searchRequest() {
        val query = binding.editTextRequest.text.toString().trim()
        fetchQueueData(query)
    }
    private fun searchDebounce() {
        searchHandler.removeCallbacks(searchRunnable)
        searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        const val EDIT_TEXT_STATE_KEY = "EDIT_TEXT_STATE_KEY"
        const val SEARCH_DEF = ""

        private const val HISTORY_PREFERENCES = "history_preferences"
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10

        private const val SEARCH_DEBOUNCE_DELAY = 3000L
    }
}
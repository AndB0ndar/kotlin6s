package com.example.kotlin6s

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin6s.adapter.QueueListAdapter
import com.example.kotlin6s.adapter.SearchHistoryAdapter
import com.example.kotlin6s.databinding.ActivityHomeBinding
import com.example.kotlin6s.model.api.Queue
import com.example.kotlin6s.service.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private lateinit var queueAdapter: QueueListAdapter
    private var queueList: MutableList<Queue> = mutableListOf()
    private var currentFilter: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //
        // search history
        //
        val searchHistory = getSearchHistory()
        if (searchHistory.isNotEmpty()) {
            binding.recyclerViewSearchHistory.layoutManager = LinearLayoutManager(this)
            val historyAdapter = SearchHistoryAdapter(searchHistory)
            binding.recyclerViewSearchHistory.adapter = historyAdapter
            historyAdapter.setOnClickListener(object : SearchHistoryAdapter.OnClickListener {
                override fun onClick(position: String) {
                    binding.editTextSearch.setText(position)
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
                currentFilter = query
                if (query.isEmpty()) {
                    binding.buttonClear.visibility = View.GONE
                } else {
                    binding.buttonClear.visibility = View.VISIBLE
                }
                queueAdapter.filter(query)
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
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
            queueAdapter.clear()
        }


        //
        // placeholder
        //
        binding.buttonRetry.setOnClickListener {
            fetchQueues()
        }

        //
        // RecycleView
        //
        binding.recyclerViewQueueList.layoutManager = LinearLayoutManager(this)
        queueAdapter = QueueListAdapter(queueList, AuthManager.getToken())
        binding.recyclerViewQueueList.adapter = queueAdapter
        queueAdapter.setOnClickListener(object : QueueListAdapter.OnItemClickListener {
            override fun onItemClick(queue: Queue) {
                navigateToQueue(queue)
                addSearchQuery(queue.queueName)
            }

            override fun onButtonDelete(queue: Queue, position: Int) {
                AuthManager.getToken()?.let { token ->
                    if (queue.creatorToken != token) {
                        Toast.makeText(this@HomeActivity, "You are not authorized to delete this queue", Toast.LENGTH_SHORT).show()
                        return
                    }
                    CoroutineScope(Dispatchers.IO).launch {
                        val queueDeleted = deleteQueue(queue.id)
                        if (queueDeleted) {
                            withContext(Dispatchers.Main) {
                                queueList.removeAt(position)
                                queueAdapter.notifyItemRemoved(position)
                            }
                        }
                    }
                } ?: run {
                    Toast.makeText(this@HomeActivity, "Token not found", Toast.LENGTH_SHORT).show()
                }
            }
        })

        //
        // connection
        //
        binding.buttonConnectQueue.setOnClickListener {
            val queueIdText = binding.editTextQueueId.text.toString()
            val queueId = queueIdText.toIntOrNull()

            if (queueId != null) {
                AuthManager.getToken()?.let { token ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val connected = connectQueue(queueId)
                        if (connected) {
                            fetchQueues()
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@HomeActivity, "Connected to Queue: $queueId", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } ?: run {
                    Toast.makeText(this@HomeActivity, "Token not found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@HomeActivity, "Invalid Queue ID", Toast.LENGTH_SHORT).show()
            }
        }

        //
        // navigation
        //
        binding.buttonAddQueue.setOnClickListener {
            navigateToAddQueue()
        }
        binding.imageViewProfile.setOnClickListener {
            navigateToProfile()
        }

        AuthManager.getToken()?.let {
            fetchQueues()
        }
    }

    private fun fetchQueues() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerViewQueueList.visibility = View.GONE
                binding.layoutRequest.visibility = View.GONE
            }

            try {
                val queues = RetrofitInstance.api.getAllQueues().toMutableList()
                withContext(Dispatchers.Main) {
                    queueList.clear()
                    queueList.addAll(queues)
                    queueAdapter.filter(currentFilter)

                    binding.progressBar.visibility = View.GONE
                    binding.recyclerViewQueueList.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.errorMessage.text = "Request error: ${e.localizedMessage}"
                    binding.layoutRequest.visibility = View.VISIBLE
                    binding.recyclerViewQueueList.visibility = View.GONE
                }
            }
        }
    }

    private suspend fun deleteQueue(queueId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.deleteQueue(queueId).execute()
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("HomeActivity", "Error delete: ${e.message}")
                Toast.makeText(this@HomeActivity, "Error delete: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        return@withContext false
    }

    private suspend fun connectQueue(queueId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.connectQueue(queueId).execute()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@withContext true
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@HomeActivity, "Failed to connect to Queue", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@HomeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        return@withContext false
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

    private fun getSearchHistory(): MutableList<String?> {
        val preferences = getSharedPreferences(HISTORY_PREFERENCES, Context.MODE_PRIVATE)
        val historySet = preferences.getStringSet(SEARCH_HISTORY_KEY, HashSet<String>())
        return ArrayList(historySet!!)
    }

    private fun clearSearchHistory() {
        val preferences = getSharedPreferences(HISTORY_PREFERENCES, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.remove(SEARCH_HISTORY_KEY)
        editor.apply()
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

    companion object {
        const val EDIT_TEXT_STATE_KEY = "EDIT_TEXT_STATE_KEY"
        const val SEARCH_DEF = ""

        private const val HISTORY_PREFERENCES = "history_preferences"
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
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

    private fun navigateToQueue(queue: Queue) {
        Log.d("HomeActivity", "Go Queue")
        val intent = Intent(this, QueueActivity::class.java)
        intent.putExtra("queueId", queue.id)
        startActivity(intent)
    }
}
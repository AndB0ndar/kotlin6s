package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin6s.adapter.QueueItemAdapter
import com.example.kotlin6s.databinding.ActivityQueueBinding
import com.example.kotlin6s.model.api.QueueItemResponse
import com.example.kotlin6s.service.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QueueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQueueBinding

    private lateinit var queueAdapter: QueueItemAdapter
    private var queueItemList: MutableList<QueueItemResponse> = mutableListOf()
    private var currentFilter: String = ""
    private var queueId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        queueId = intent.getIntExtra("queueId", -1)
        AuthManager.getToken()?.let {
            fetchQueues()
        }

        //
        // RecyclerView
        //
        binding.recyclerViewQueueList.layoutManager = LinearLayoutManager(this)
        queueAdapter = QueueItemAdapter(queueItemList)
        binding.recyclerViewQueueList.adapter = queueAdapter

        //
        // search
        //
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
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
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.buttonClear.setOnClickListener {
            binding.editTextSearch.text.clear()
            queueAdapter.clear()
        }


        binding.imageBack.setOnClickListener {
            navigateToHome()
        }


        binding.buttonAdd.setOnClickListener {
            RetrofitInstance.api.addQueueItem(queueId).enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it >= 0) {
                                fetchQueues()
                            } else {
                                Toast.makeText(this@QueueActivity, "Add failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@QueueActivity, "Response failed", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    Toast.makeText(this@QueueActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        binding.buttonRemove.setOnClickListener {
            RetrofitInstance.api.deleteQueueItem(queueId).enqueue(object : Callback<Boolean> {
                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            if (it) {
                                fetchQueues()
                            } else {
                                Toast.makeText(this@QueueActivity, "Remove failed: $it", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@QueueActivity, "Response failed", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Boolean>, t: Throwable) {
                    Toast.makeText(this@QueueActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun fetchQueues() {
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.recyclerViewQueueList.visibility = View.GONE
            }
            try {
                val queueItems = RetrofitInstance.api.getQueueById(queueId).toMutableList()
                withContext(Dispatchers.Main) {
                    queueItemList.clear()
                    queueItemList.addAll(queueItems)
                    queueAdapter.filter(currentFilter)
                    Log.d("QueueActivity", queueItemList.toString())
                    binding.recyclerViewQueueList.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    binding.recyclerViewQueueList.visibility = View.GONE
                }
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        Log.d("QueueActivity", "Go Home")
        startActivity(intent)
    }
}
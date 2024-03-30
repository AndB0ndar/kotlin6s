package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin6s.adapter.QueueListAdapter
import com.example.kotlin6s.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private lateinit var queueAdapter: QueueListAdapter
    private val queueList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.buttonClear.visibility = View.INVISIBLE
                } else {
                    binding.buttonClear.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.buttonClear.setOnClickListener {
            binding.editTextSearch.text.clear()
        }

        binding.recyclerViewQueueList.layoutManager = LinearLayoutManager(this)
        queueAdapter = QueueListAdapter(queueList)
        binding.recyclerViewQueueList.adapter = queueAdapter

        queueAdapter.setOnClickListener(object : QueueListAdapter.OnClickListener {
            override fun onClick(position: Int) {
                navigateToQueue()
            }
        })

        populateQueueList()

        binding.buttonAddQueue.setOnClickListener {
            navigateToAddQueue()
        }

        binding.imageViewProfile.setOnClickListener {
            navigateToProfile()
        }
    }

    private fun populateQueueList() {
        queueList.add("Очередь 1")
        queueList.add("Очередь 2")
        queueList.add("Очередь 3")

        queueAdapter.notifyDataSetChanged()
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
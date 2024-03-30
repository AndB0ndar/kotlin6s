package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlin6s.adapter.QueueAdapter
import com.example.kotlin6s.adapter.QueueListAdapter
import com.example.kotlin6s.databinding.ActivityQueueBinding

class QueueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQueueBinding

    private lateinit var queueAdapter: QueueAdapter
    private val queueList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewQueueList.layoutManager = LinearLayoutManager(this)
        queueAdapter = QueueAdapter(queueList)
        binding.recyclerViewQueueList.adapter = queueAdapter
        populateQueueList()

        binding.imageBack.setOnClickListener {
            navigateToHome()
        }

        binding.buttonAdd.setOnClickListener {
        }

        binding.buttonRemove.setOnClickListener {
        }
    }

    private fun populateQueueList() {
        queueList.add("Пользователь 1")
        queueList.add("Пользователь 2")
        queueList.add("Пользователь 3")

        queueAdapter.notifyDataSetChanged()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        Log.d("QueueActivity", "Go Home")
        startActivity(intent)
    }
}
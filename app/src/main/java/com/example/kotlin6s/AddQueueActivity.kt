package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlin6s.databinding.ActivityAddQueueBinding
import com.example.kotlin6s.databinding.ActivityHomeBinding
import kotlin.math.log

class AddQueueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddQueueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQueueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSave.setOnClickListener {
            val queueName = binding.editTextGroup.text.toString()
            val subject = binding.editTextEmail.text.toString()
            val info = binding.textViewInfo.text.toString()
        }

        binding.imageBack.setOnClickListener {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        Log.d("AddQueueActivity", "Go Home")
        startActivity(intent)
    }
}
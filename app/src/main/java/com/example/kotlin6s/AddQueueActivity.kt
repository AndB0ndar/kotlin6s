package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin6s.databinding.ActivityAddQueueBinding
import com.example.kotlin6s.model.api.UserGroup
import com.example.kotlin6s.model.api.Queue
import com.example.kotlin6s.model.api.QueueReceiveRemote
import com.example.kotlin6s.service.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddQueueActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddQueueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddQueueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSave.setOnClickListener {
            val queueName = binding.editTextGroup.text.toString()
            val description = binding.textViewInfo.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val queueCreated = createQueue(queueName, description)
                if (queueCreated) {
                    withContext(Dispatchers.Main) {
                        navigateToHome()
                    }
                }
            }
        }

        binding.imageBack.setOnClickListener {
            navigateToHome()
        }
    }

    private suspend fun createQueue(name: String, description: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val userGroupResponse = RetrofitInstance.api.getMyGroup().execute()
            if (userGroupResponse.isSuccessful) {
                userGroupResponse.body()?.let { group ->
                    val createQueueRequest = QueueReceiveRemote(name, group.groupName, description)
                    val createQueueResponse = RetrofitInstance.api.createQueue(createQueueRequest).execute()
                    if (createQueueResponse.isSuccessful) {
                        createQueueResponse.body()?.let {
                            Log.d("AddQueueActivity", it.toString())
                            withContext(Dispatchers.Main) {
                                if (it >= 0) {
                                    Toast.makeText(this@AddQueueActivity, "Queue created: $it", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@AddQueueActivity, "Creation failed", Toast.LENGTH_LONG).show()
                                }
                            }
                            return@withContext it >= 0
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@AddQueueActivity, "Response failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddQueueActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        return@withContext false
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        Log.d("AddQueueActivity", "Go Home")
        startActivity(intent)
    }
}
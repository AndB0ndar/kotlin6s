package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlin6s.databinding.ActivityAddQueueBinding
import com.example.kotlin6s.databinding.ActivityProfileBinding
import com.example.kotlin6s.model.api.Profile
import com.example.kotlin6s.model.api.UserGroup
import com.example.kotlin6s.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        RetrofitInstance.api.getProfile().enqueue(object : Callback<Profile> {
            override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        binding.editTextFirstName.text = Editable.Factory.getInstance().newEditable(user.firstName)
                        binding.editTextLastName.text = Editable.Factory.getInstance().newEditable(user.lastName)
                        RetrofitInstance.api.getMyGroup().enqueue(object : Callback<UserGroup> {
                            override fun onResponse(call: Call<UserGroup>, response: Response<UserGroup>) {
                                if (response.isSuccessful) {
                                    response.body()?.let {
                                        binding.editTextGroup.text = Editable.Factory.getInstance().newEditable(it.groupName)
                                    }
                                }
                            }
                            override fun onFailure(call: Call<UserGroup>, t: Throwable) {
                            }
                        })
                    }
                }
            }
            override fun onFailure(call: Call<Profile>, t: Throwable) {
            }
        })

        binding.buttonSignUp.setOnClickListener {
            val firstName = binding.editTextFirstName.text.toString()
            val lastName = binding.editTextLastName.text.toString()
            val groupName = binding.editTextGroup.text.toString()
        }

        binding.imageBack.setOnClickListener {
            navigateToHome()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        Log.d("ProfileActivity", "Go Home")
        startActivity(intent)
    }
}
package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin6s.databinding.ActivityRegisterBinding
import com.example.kotlin6s.model.api.TokenResponse
import com.example.kotlin6s.model.api.RegisterResponse
import com.example.kotlin6s.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val login = binding.loginEditText.text.toString()
            val firstName = binding.firstNameEditText.text.toString()
            val lastName = binding.lastNameEditText.text.toString()
            val group = binding.groupEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val registerReceive = RegisterResponse(login, firstName, lastName, group, password)
            RetrofitInstance.api.register(registerReceive).enqueue(object : Callback<TokenResponse> {
                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            AuthManager.saveToken(it.token)
                            Toast.makeText(this@RegisterActivity, "Token: ${it.token}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
        binding.imageBack.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        Log.d("RegisterActivity", "Go Log In")
        startActivity(intent)
    }
}
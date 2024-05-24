package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin6s.databinding.ActivityLoginBinding
import com.example.kotlin6s.model.api.LoginResponse
import com.example.kotlin6s.model.api.TokenResponse
import com.example.kotlin6s.service.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val login = binding.loginEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val loginReceive = LoginResponse(login, password)
            RetrofitInstance.api.login(loginReceive).enqueue(object : Callback<TokenResponse> {
                override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            AuthManager.saveToken(it.token)
                            navigateToHome()
                            // Toast.makeText(this@LoginActivity, "Token: ${it.token}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        binding.buttonSignUp.setOnClickListener {
            navigateToSignUpActivity()
        }
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        Log.d("LoginActivity", "Go SignUp")
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        Log.d("LoginActivity", "Go Home")
        startActivity(intent)
    }
}
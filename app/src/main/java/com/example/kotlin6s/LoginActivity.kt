package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlin6s.databinding.ActivityHomeBinding
import com.example.kotlin6s.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
//    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        viewModel = LoginViewModel()

        binding.buttonLogin.setOnClickListener {
            /*
            viewModel.onLoginButtonClick(
                binding.editTextUsername.text.toString(),
                binding.editTextPassword.text.toString()
            )
            */
            navigateToHome()
        }

        binding.buttonSignUp.setOnClickListener {
//            viewModel.onSignUpButtonClick()
            navigateToSignUpActivity()
        }
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        Log.d("LoginActivity", "Go SignUp")
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        Log.d("LoginActivity", "Go Home")
        startActivity(intent)
    }
}
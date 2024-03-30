package com.example.kotlin6s

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.kotlin6s.databinding.ActivityHomeBinding
import com.example.kotlin6s.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignUp.setOnClickListener {
            /*
            viewModel.onSignUpButtonClick(
                binding.editTextFirstName.text.toString(),
                binding.editTextLastName.text.toString(),
                binding.editTextGroup.text.toString(),
                binding.editTextEmail.text.toString(),
                binding.editTextPassword.text.toString(),
                binding.editTextConfirmPassword.text.toString()
            )
            */
        }

        binding.imageBack.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        Log.d("SignUpActivity", "Go Log In")
        startActivity(intent)
    }
}
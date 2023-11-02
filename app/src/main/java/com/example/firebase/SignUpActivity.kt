package com.example.firebase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.button1.setOnClickListener {
            val email = binding.SignUpEmail.text.toString().trim()
            val password = binding.SignUpPassword.text.toString()
            val confirmPassword = binding.SignUpCPassword.text.toString()

            if (validateInputs(email, password, confirmPassword)) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            showSnackBar("Account created.")
                        } else {
                            showSnackBar("Failed.")
                        }
                    }
            }
        }
    }

    private fun validateInputs(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail)
                .matches()
        ) {
            showSnackBar("Enter a valid email address")
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            showSnackBar("Password must be at least 6 characters")
            return false
        }

        if (confirmPassword != password) {
            showSnackBar("Passwords do not match")
            return false
        }

        return true
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
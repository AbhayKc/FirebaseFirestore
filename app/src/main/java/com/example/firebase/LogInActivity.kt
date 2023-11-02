package com.example.firebase

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LogInActivity : AppCompatActivity() {
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val spannable = SpannableStringBuilder()

        // Check if user already Signed-In
        if (currentUser != null) {
            val intent = Intent(this, HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.button2.setOnClickListener {
            signIn()
        }
        binding.button.setOnClickListener {
            val email = binding.LoginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString()

            if (validateInputs(email, password)) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            snackBar("Login successful")
                            val intent = Intent(this, HomePageActivity::class.java)
                            intent.putExtra("email", email)
                            startActivity(intent)
                            finish()
                        } else {
                            snackBar("Login failed")
                        }
                    }
            }
        }

        // SpannableString to customize text
        val blackText = SpannableString("Don't have an account? ")
        blackText.setSpan(ForegroundColorSpan(Color.BLACK), 0, blackText.length, 0)

        val redText = SpannableString("sign up")
        redText.setSpan(ForegroundColorSpan(Color.RED), 0, redText.length, 0)

        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LogInActivity, SignUpActivity::class.java)
                startActivity(intent)
            }
        }
        redText.setSpan(clickableSpan1, 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.append(blackText)
        spannable.append(redText)
        binding.textView6.text = spannable
        binding.textView6.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun validateInputs(
        email: String,
        password: String,
    ): Boolean {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail)
                .matches()
        ) {
            snackBar("Enter email address")
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            snackBar("Password must be at least 6 characters")
            return false
        }
        return true
    }

    private fun snackBar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this, HomePageActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
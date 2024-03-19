package com.anasfarooq.i210813

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        // Navigation
        binding.loginBtn.setOnClickListener {
            val email = binding.emailText.text.toString()
            val password = binding.passText.text.toString()

            if(email.isEmpty()) binding.emailText.error = "Email is required"
            if(password.isEmpty()) binding.passText.error = "Password is required"

            if(email.isNotEmpty() && password.isNotEmpty()) {
                MainActivity.auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        // Load user details, mentors and only navigate to HomeActivity after loading is complete
                        val sharedPreferences = getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE)
                        sharedPreferences.edit().putBoolean(MainActivity.LOGIN_STATUS_KEY, true).apply()

                        // Navigate to HomeActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
            }
        }


        binding.signupBtn.paintFlags = binding.signupBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPassBtn.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
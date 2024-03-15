package com.anasfarooq.i210813

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)
        
        binding.sendBtn.setOnClickListener {
            val email = binding.emailText.text.toString().trim()

            if(email.isEmpty()) {
                binding.emailText.error = "Email is required"
            } else {
                MainActivity.auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        // Clear the text and inform the user that an email has been sent
                        binding.emailText.text.clear()
                        Toast.makeText(this, "Check your email to reset your password.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        binding.emailText.error = it.localizedMessage
                    }
            }
        }


        binding.loginBtn.setOnClickListener {
            finish()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}
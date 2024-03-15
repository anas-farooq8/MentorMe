package com.anasfarooq.i210813

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)


        binding.sendBtn.setOnClickListener {
            val email = binding.emailText.text.toString()

            if(email.isEmpty()) binding.emailText.error = "Email is required"

            if(email.isNotEmpty()) {
                MainActivity.auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        binding.emailText.text.clear()
                        startActivity(Intent(this, ResetPasswordActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener{
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
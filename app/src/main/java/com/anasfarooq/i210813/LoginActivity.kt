package com.anasfarooq.i210813

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val signupBtn: TextView = findViewById(R.id.signupBtn)
        signupBtn.paintFlags = signupBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val forgotPassBtn: TextView = findViewById(R.id.forgotPassBtn)
        forgotPassBtn.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.anasfarooq.i210813

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class ForgotPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val loginBtn: TextView = findViewById(R.id.loginBtn)
        loginBtn.setOnClickListener {
            finish()
        }

        val backBtn: View = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}
package com.anasfarooq.i210813

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val loginBtn: TextView = findViewById(R.id.loginBtn)
        loginBtn.paintFlags = loginBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        loginBtn.setOnClickListener {
            finish()
        }
    }
}
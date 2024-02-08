package com.anasfarooq.i210813

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class VerifyPhoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val backBtn: TextView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}
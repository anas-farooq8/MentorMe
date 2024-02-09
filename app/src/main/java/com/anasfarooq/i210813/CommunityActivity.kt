package com.anasfarooq.i210813

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class CommunityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val backBtn: View = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}
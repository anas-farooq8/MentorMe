package com.anasfarooq.i210813

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class VideoCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val endCallBtn: View = findViewById(R.id.endCallBtn)
        endCallBtn.setOnClickListener {
            finish()
        }
    }
}
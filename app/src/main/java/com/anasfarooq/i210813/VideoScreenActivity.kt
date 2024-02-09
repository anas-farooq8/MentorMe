package com.anasfarooq.i210813

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class VideoScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_screen)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val cameraBtn: ImageView = findViewById(R.id.cameraBtn)
        cameraBtn.setOnClickListener {
            val intent = Intent(this, VideoScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        val closeVideo: ImageView = findViewById(R.id.closeVideo)
        closeVideo.setOnClickListener {
            finish()
        }
    }
}
package com.anasfarooq.i210813

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class PhotoScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_screen)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val videoBtn: ImageView = findViewById(R.id.videoBtn)
        videoBtn.setOnClickListener {
            val intent = Intent(this, VideoScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        val crossBtn: ImageView = findViewById(R.id.crossBtn)
        crossBtn.setOnClickListener {
            finish()
        }
    }
}
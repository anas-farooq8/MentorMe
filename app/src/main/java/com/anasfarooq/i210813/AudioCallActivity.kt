package com.anasfarooq.i210813

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class AudioCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_call)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val endCallBtn: View = findViewById(R.id.endCallBtn)
        endCallBtn.setOnClickListener {
            finish()
        }


    }
}
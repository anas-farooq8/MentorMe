package com.anasfarooq.i210813

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val bookSessionBtn: Button = findViewById(R.id.bookSessionBtn)

        bookSessionBtn.setOnClickListener {
            val intent = Intent(this, BookSessionActivity::class.java)
            startActivity(intent)
        }

        val dropReviewBtn: Button = findViewById(R.id.dropReviewBtn)

        dropReviewBtn.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            startActivity(intent)
        }

        val joinCommunityBtn: Button = findViewById(R.id.joinCommunityBtn)

        joinCommunityBtn.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

        val backBtn: View = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}
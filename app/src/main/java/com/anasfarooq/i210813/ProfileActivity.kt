package com.anasfarooq.i210813

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)
        extractIntentData()

        binding.bookSessionBtn.setOnClickListener {
            val intent = Intent(this, BookSessionActivity::class.java)
            startActivity(intent)
        }

        binding.dropReviewBtn.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            startActivity(intent)
        }

        binding.joinCommunityBtn.setOnClickListener {
            val intent = Intent(this, CommunityActivity::class.java)
            startActivity(intent)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun extractIntentData() {
        // Extract data from intent
        val name = intent.getStringExtra("name")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val imagePath = intent.getStringExtra("imagePath")

        val truncatedText = if (name!!.length > 10) name.substring(0, 10) + "…" else name
        binding.nameText.text = truncatedText

        binding.titleText.text = title
        binding.descText.text = description
        Picasso.get().load(imagePath).into(binding.profileImg)
    }
}
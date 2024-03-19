package com.anasfarooq.i210813

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.databinding.ActivityProfileBinding
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var id: String? = null
    private var name: String? = null
    private var imagePath: String? = null
    private var sessionPrice: Int? = 0
    private var availability: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)
        extractIntentData()

        binding.bookSessionBtn.setOnClickListener {
            val intent = Intent(this, BookSessionActivity::class.java).apply {
                putExtra("id", id)
                putExtra("mentorName", name)
                putExtra("imagePath", imagePath)
                putExtra("sessionPrice", sessionPrice)
                putExtra("availability", availability)
            }
            startActivity(intent)
        }

        binding.dropReviewBtn.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java).apply {
                putExtra("mentorName", name)
                putExtra("imagePath", imagePath)
            }
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
        id = intent.getStringExtra("id")
        name = intent.getStringExtra("name")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        sessionPrice = intent.getIntExtra("sessionPrice", 0)
        imagePath = intent.getStringExtra("imagePath")
        availability = intent.getStringExtra("availability")

        val truncatedText = if (name!!.length > 10) name.toString().substring(0, 10) + "…" else name
        binding.nameText.text = truncatedText

        binding.titleText.text = title
        binding.descText.text = description
        Picasso.get().load(imagePath).into(binding.profileImg)
    }
}
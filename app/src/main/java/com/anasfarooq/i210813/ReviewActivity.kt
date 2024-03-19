package com.anasfarooq.i210813

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.Models.Review
import com.anasfarooq.i210813.databinding.ActivityAddMentorBinding
import com.anasfarooq.i210813.databinding.ActivityReviewBinding
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val mentorName = intent.getStringExtra("mentorName") ?: ""
        binding.nameText.text = mentorName
        val imagePath = intent.getStringExtra("imagePath") ?: ""
        Picasso.get().load(imagePath).into(binding.profileImage)

        binding.submitBtn.setOnClickListener {
            val description = binding.reviewTxt.text.toString().trim()
            val stars = binding.ratingBar.rating

            if (description.isNotEmpty()) {
                submitReview(mentorName, description, stars)
                finish()
            } else {
                binding.reviewTxt.error = "Review is required"
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun submitReview(mentorName: String, description: String, stars: Float) {
        val userId = MainActivity.auth.currentUser?.uid ?: return

        // Create the review object
        val review = Review(userId, mentorName, description, stars)

        // Get a reference to the "reviews" node, then to this user's node, then push to create a unique review ID
        val reviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(userId).push()

        // Set the value of the new child to the review object
        reviewRef.setValue(review).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

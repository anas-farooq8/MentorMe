package com.anasfarooq.i210813

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.Models.Mentor
import com.anasfarooq.i210813.Models.Review
import com.anasfarooq.i210813.Models.UserProfile
import com.anasfarooq.i210813.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var firebasedatabase: FirebaseDatabase

        const val REQUEST_STORAGE_PERMISSION = 101
        const val PICK_IMAGE_REQUEST = 102
        const val PICK_VIDEO_REQUEST = 103

        lateinit var currentUserInfo: UserProfile
        lateinit var topMentorList: ArrayList<Mentor>
        lateinit var educationMentorList: ArrayList<Mentor>
        lateinit var personalGrowthMentorList: ArrayList<Mentor>

        lateinit var reviewList: ArrayList<Review>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firebasedatabase = FirebaseDatabase.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        currentUserInfo = UserProfile("", "", "", "", "")
        topMentorList = ArrayList()
        educationMentorList = ArrayList()
        personalGrowthMentorList = ArrayList()
        reviewList = ArrayList()
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}
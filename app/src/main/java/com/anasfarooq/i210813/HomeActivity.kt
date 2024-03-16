package com.anasfarooq.i210813

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.anasfarooq.i210813.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
/*    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.auth.signOut()
        Toast.makeText(this, "Signed Out!", Toast.LENGTH_SHORT).show()
        finish()
    }*/

    private lateinit var binding: ActivityHomeBinding

    private lateinit var topMentorAdapter: MentorAdapter
    private lateinit var educationAdapter: MentorAdapter
    private lateinit var personalGrowthAdapter: MentorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        binding.searchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.chatBtn.setOnClickListener {
            val intent = Intent(this, AllChatsActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.accountBtn.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
            finish()
        }


        binding.notificationBtn.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }


        binding.addMentorBtn.setOnClickListener {
            val intent = Intent(this, AddMentorActivity::class.java)
            startActivity(intent)
        }

        checkStoragePermission()
        loadInfo()

        topMentorAdapter = MentorAdapter(MainActivity.topMentorList, this)
        educationAdapter = MentorAdapter(MainActivity.educationMentorList, this)
        personalGrowthAdapter = MentorAdapter(MainActivity.personalGrowthMentorList, this)

        binding.topMentors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.educationMentors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.personalGrowthMentors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.topMentors.adapter = topMentorAdapter
        binding.educationMentors.adapter = educationAdapter
        binding.personalGrowthMentors.adapter = personalGrowthAdapter

    }



    @SuppressLint("NotifyDataSetChanged")
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 (API level 33) and above, use READ_MEDIA_IMAGES for more specific access
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), MainActivity.REQUEST_STORAGE_PERMISSION)
            }
        } else {
            // For older versions, check for READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MainActivity.REQUEST_STORAGE_PERMISSION)
            }
        }
    }

    private fun loadInfo() {
        val name = MainActivity.currentUserInfo.name
        val truncatedText = if (name.length > 10) name.substring(0, 10) + "…" else name
        binding.nameText.text = truncatedText
    }
}
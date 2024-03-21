package com.anasfarooq.i210813

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.anasfarooq.i210813.databinding.ActivityHomeBinding
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private lateinit var topMentorAdapter: MentorAdapter
    private lateinit var educationAdapter: MentorAdapter
    private lateinit var personalGrowthAdapter: MentorAdapter

    private lateinit var screenshotObserver: ScreenshotContentObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        val handler = Handler(Looper.getMainLooper())
        screenshotObserver = ScreenshotContentObserver(handler, this)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.searchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log the token
            Log.d(TAG, "FCM Token: $token")
            // Here you can also send the token to your server if needed
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

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMentorsData() {
        // After loading the data, notify your adapters
        topMentorAdapter.notifyDataSetChanged()
        educationAdapter.notifyDataSetChanged()
        personalGrowthAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        loadMentorsData()
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            screenshotObserver
        )
    }

    override fun onPause() {
        super.onPause()
        contentResolver.unregisterContentObserver(screenshotObserver)
    }

}
package com.anasfarooq.i210813

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.anasfarooq.i210813.databinding.ActivityMyProfileBinding
import com.squareup.picasso.Picasso

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadInfo()
        checkStoragePermission()

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)
        binding.editProfileImgBtn.setOnClickListener {
            openGallery()
        }

        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

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

        binding.bookedSessions.setOnClickListener {
            val intent = Intent(this, BookedSessionActivity::class.java)
            startActivity(intent)
        }

        binding.editBtn.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.addMentorBtn.setOnClickListener {
            val intent = Intent(this, AddMentorActivity::class.java)
            startActivity(intent)
        }

        binding.editProfile.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        reviewAdapter = ReviewAdapter(MainActivity.reviewList, this)
        binding.reviewCards.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.reviewCards.adapter = reviewAdapter

    }

    private fun loadProfileImage() {
        val imageUrl = MainActivity.currentUserInfo.imagePath ?: return

        if(imageUrl.isEmpty()) return

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.circle2)
            .into(binding.profileImg)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, MainActivity.PICK_IMAGE_REQUEST)
    }

    private fun saveImagePath(imagePath: String) {
        val userId = MainActivity.auth.currentUser?.uid ?: return
        val databaseReference = MainActivity.firebasedatabase.getReference("users").child(userId)

        databaseReference.child("profileImageUrl").setValue(imagePath)
            .addOnSuccessListener {
                Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show()
                MainActivity.currentUserInfo.imagePath = imagePath
                loadProfileImage()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            imageUri?.let { uri ->
                saveImagePath(uri.toString())
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.REQUEST_STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadProfileImage()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

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
        val country = MainActivity.currentUserInfo.country

        binding.name.text = name
        binding.location.text = country

        loadProfileImage()
    }
}
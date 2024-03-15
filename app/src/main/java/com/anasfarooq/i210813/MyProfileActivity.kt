package com.anasfarooq.i210813

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anasfarooq.i210813.databinding.ActivityMyProfileBinding
import com.squareup.picasso.Picasso

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding

    companion object {
        private const val REQUEST_STORAGE_PERMISSION = 101
        private const val PICK_IMAGE_REQUEST = 102
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfileImage()

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        binding.editProfileImgBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
            } else {
                openGallery()
            }
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
    }

    private fun loadProfileImage() {
        val userId = MainActivity.auth.currentUser?.uid ?: return
        val databaseReference = MainActivity.firebasedatabase.getReference("users").child(userId)

        databaseReference.child("profileImageUrl").get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val imageUrl = dataSnapshot.value as String?
                imageUrl?.let {
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.circle2)
                        .into(binding.profileImg)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load profile image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun saveImagePath(imagePath: String) {
        val userId = MainActivity.auth.currentUser?.uid ?: return
        val databaseReference = MainActivity.firebasedatabase.getReference("users").child(userId)

        databaseReference.child("profileImageUrl").setValue(imagePath)
            .addOnSuccessListener {
                Toast.makeText(this, "Image path saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save image path", Toast.LENGTH_SHORT).show()
            }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            imageUri?.let { uri ->
                saveImagePath(uri.toString())
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}
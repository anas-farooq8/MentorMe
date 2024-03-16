package com.anasfarooq.i210813

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.anasfarooq.i210813.Models.Mentor
import com.anasfarooq.i210813.databinding.ActivityAddMentorBinding

class AddMentorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddMentorBinding
    private var isImageSelected = false
    private var isVideoSelected = false

    private var imagePath: String? = null
    private var videoPath: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMentorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)
        // Setup dropdown options
        val statusOptions = listOf("Available", "Unavailable")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, statusOptions)
        binding.availText.setAdapter(adapter)

        // Disable editable text and show dropdown on click
        binding.availText.apply {
            setOnClickListener {
                requestFocus()
                showDropDown()
            }
            // Disable keyboard input
            keyListener = null
            // Optional: handle touch events to show dropdown as well
            setOnTouchListener { v, event ->
                showDropDown()
                true
            }
        }

        // Make the dropdown icon clickable to show options
        binding.availText.setOnClickListener {
            binding.availText.showDropDown()
        }


        binding.uploadPhoto.setOnClickListener {
            isImageSelected = true
            isVideoSelected = false
            checkStoragePermission(forVideo = false)
        }

        binding.uploadVideo.setOnClickListener {
            isImageSelected = false
            isVideoSelected = true
            checkStoragePermission(forVideo = true)
        }

        binding.uploadBtn.setOnClickListener {
            val name = binding.nameText.text.toString()
            val description = binding.descriptionText.text.toString().trim()
            val availability = binding.availText.text.toString()

            if(name.isNotEmpty() && description.isNotEmpty() && availability.isNotEmpty() && (isImageSelected || isVideoSelected)) {
                val mentor = Mentor(
                    name = binding.nameText.text.toString().trim(),
                    description = binding.descriptionText.text.toString().trim(),
                    availability = binding.availText.text.toString().trim(),
                    imagePath = imagePath,
                    videoPath = videoPath
                )

                // Get a reference to the mentors node and push to create a unique key
                val databaseReference = MainActivity.firebasedatabase.getReference("mentors").push()

                // Now set the value of the new child to the mentor object
                databaseReference.setValue(mentor)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Mentor added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Failed to add mentor: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Handle error state
                if(name.isEmpty()) binding.nameText.error = "Name is required"
                if(description.isEmpty()) binding.descriptionText.error = "Description is required"
                if(availability.isEmpty()) binding.availText.error = "Availability is required"
                if(!isImageSelected && !isVideoSelected) Toast.makeText(this, "Select an image or a video", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }


    private fun openGallery(forVideo: Boolean = false) {
        val intent = if (forVideo) {
            // Intent to select a video
            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        } else {
            // Intent to select an image
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        }

        val requestCode = if (forVideo) MainActivity.PICK_VIDEO_REQUEST else MainActivity.PICK_IMAGE_REQUEST
        startActivityForResult(intent, requestCode)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val selectedMediaUri: Uri? = data.data
            when (requestCode) {
                MainActivity.PICK_IMAGE_REQUEST -> {
                    imagePath = selectedMediaUri.toString()
                    isImageSelected = true
                    isVideoSelected = false
                }
                MainActivity.PICK_VIDEO_REQUEST -> {
                    videoPath = selectedMediaUri.toString()
                    isVideoSelected = true
                    isImageSelected = false
                }
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainActivity.REQUEST_STORAGE_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkStoragePermission(forVideo: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 (API level 33) and above, use READ_MEDIA_IMAGES or READ_MEDIA_VIDEO for more specific access
            val permission = if (forVideo) Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), MainActivity.REQUEST_STORAGE_PERMISSION)
            } else {
                openGallery(forVideo)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), MainActivity.REQUEST_STORAGE_PERMISSION)
            } else {
                openGallery(forVideo)
            }
        }
    }
}
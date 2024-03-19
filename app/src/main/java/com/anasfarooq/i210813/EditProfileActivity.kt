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
import com.anasfarooq.i210813.databinding.ActivityEditProfileBinding
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        val name = binding.nameText
        val email = binding.emailText
        val phone = binding.phoneText
        val country = binding.countryText
        val city = binding.cityText

        loadInfo()

        binding.updateProfileBtn.setOnClickListener {
            val currentUser = MainActivity.auth.currentUser ?: return@setOnClickListener
            // Collect the updated information from the EditText fields
            val updatedName = name.text.toString()
            val updatedEmail = email.text.toString()
            val updatedPhone = phone.text.toString()
            val updatedCountry = country.text.toString()
            val updatedCity = city.text.toString()

            // Prepare a map with the updated user information
            val userMap = mapOf(
                "name" to updatedName,
                "email" to updatedEmail,
                "phone" to updatedPhone,
                "country" to updatedCountry,
                "city" to updatedCity
            )
            // Get a reference to the 'users' node in the database and the current user's child node
            val userRef = MainActivity.firebasedatabase.getReference("users").child(currentUser.uid)
            if(!MainActivity.isOnline(this)) {
                Toast.makeText(this, "The profile has been updated locally. Connect Internet to update Online.\"", Toast.LENGTH_SHORT).show()
                update()
            }

            // Update the child node with the new user information
            userRef.updateChildren(userMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    update()
                } else {
                    Toast.makeText(this, "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun loadProfileImage() {
        val imageUrl = MainActivity.currentUserInfo.imagePath ?: return

        if(imageUrl.isEmpty()) return

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.circle2)
            .into(binding.profileImage)
    }

    private fun loadInfo() {
        val name = MainActivity.currentUserInfo.name
        val email = MainActivity.currentUserInfo.email
        val phone = MainActivity.currentUserInfo.phone
        val country = MainActivity.currentUserInfo.country
        val city = MainActivity.currentUserInfo.city

        binding.nameText.setText(name)
        binding.emailText.setText(email)
        binding.phoneText.setText(phone)
        binding.countryText.setText(country)
        binding.cityText.setText(city)

        loadProfileImage()
    }

    private fun update() {
        MainActivity.currentUserInfo.name = binding.nameText.text.toString()
        MainActivity.currentUserInfo.email = binding.emailText.text.toString()
        MainActivity.currentUserInfo.phone = binding.phoneText.text.toString()
        MainActivity.currentUserInfo.country = binding.countryText.text.toString()
        MainActivity.currentUserInfo.city = binding.cityText.text.toString()
    }

}

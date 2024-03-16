package com.anasfarooq.i210813

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.databinding.ActivityEditProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = binding.nameText
        val email = binding.emailText
        val phone = binding.phoneText
        val country = binding.countryText
        val city = binding.cityText

        loadProfileImage()

        val currentUser = MainActivity.auth.currentUser
        if (currentUser != null) {
            val databaseReference = MainActivity.firebasedatabase.getReference("users")
            databaseReference.child(currentUser.uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        name.setText(snapshot.child("name").value as? String ?: "")
                        email.setText(snapshot.child("email").value as? String ?: "")
                        phone.setText(snapshot.child("phone").value as? String ?: "")
                        country.setText(snapshot.child("country").value as? String ?: "")
                        city.setText(snapshot.child("city").value as? String ?: "")
                    } else {
                        Toast.makeText(applicationContext, "User details not found.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Failed to read user details.", Toast.LENGTH_LONG).show()
                }
            })
        }

        binding.updateProfileBtn.setOnClickListener {
            if (currentUser != null) {
                // Collect the updated information from the EditText fields
                val updatedName = name.text.toString()
                val updatedEmail = email.text.toString()
                val updatedPhone = phone.text.toString()
                val updatedCountry = country.text.toString()
                val updatedCity = city.text.toString()

                // Prepare a map with the updated user information
                val userUpdateMap = hashMapOf<String, Any>(
                    "name" to updatedName,
                    "email" to updatedEmail,
                    "phone" to updatedPhone,
                    "country" to updatedCountry,
                    "city" to updatedCity
                )

                // Get a reference to the 'users' node in the database and the current user's child node
                val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.uid)

                // Update the child node with the new user information
                userRef.updateChildren(userUpdateMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
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
                        .into(binding.profileImage)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load profile image.", Toast.LENGTH_SHORT).show()
        }
    }

}

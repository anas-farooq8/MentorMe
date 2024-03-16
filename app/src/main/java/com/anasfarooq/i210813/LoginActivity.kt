package com.anasfarooq.i210813

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.Models.Mentor
import com.anasfarooq.i210813.Models.MentorType
import com.anasfarooq.i210813.databinding.ActivityLoginBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        // Navigation
        binding.loginBtn.setOnClickListener {
            val email = binding.emailText.text.toString()
            val password = binding.passText.text.toString()

            if(email.isEmpty()) binding.emailText.error = "Email is required"
            if(password.isEmpty()) binding.passText.error = "Password is required"

            if(email.isNotEmpty() && password.isNotEmpty()) {
                MainActivity.auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        // Load user details and only navigate to HomeActivity after loading is complete
                        loadUserDetails {
                            loadMentors{
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                            }
                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
            }
        }


        binding.signupBtn.paintFlags = binding.signupBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.forgotPassBtn.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUserDetails(onDetailsLoaded: () -> Unit) {
        val currentUser = MainActivity.auth.currentUser
        if (currentUser != null) {
            val databaseReference = MainActivity.firebasedatabase.getReference("users")
            databaseReference.child(currentUser.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        MainActivity.currentUserInfo.name = snapshot.child("name").value as? String ?: ""
                        MainActivity.currentUserInfo.email = snapshot.child("email").value as? String ?: ""
                        MainActivity.currentUserInfo.phone = snapshot.child("phone").value as? String ?: ""
                        MainActivity.currentUserInfo.country = snapshot.child("country").value as? String ?: ""
                        MainActivity.currentUserInfo.city = snapshot.child("city").value as? String ?: ""
                        MainActivity.currentUserInfo.imagePath = snapshot.child("profileImageUrl").value as? String ?: ""
                        // After loading details, invoke the callback
                        onDetailsLoaded()
                    } else {
                        Toast.makeText(applicationContext, "User details not found.", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "Failed to read user details.", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun loadMentors(onDetailsLoaded: () -> Unit) {
        val databaseReference = MainActivity.firebasedatabase.getReference("mentors")
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (mentorSnapshot in snapshot.children) {
                    val mentor = mentorSnapshot.getValue(Mentor::class.java)
                    mentor?.let {
                        MainActivity.topMentorList.add(it)
                        when (it.type) {
                            MentorType.Education -> MainActivity.educationMentorList.add(it)
                            MentorType.PersonalGrowth -> MainActivity.personalGrowthMentorList.add(it)
                            else -> ""
                        }
                    }
                }
                onDetailsLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to load mentors: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
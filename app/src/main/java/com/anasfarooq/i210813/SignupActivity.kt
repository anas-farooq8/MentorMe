package com.anasfarooq.i210813

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.databinding.ActivitySignupBinding
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        binding.signupBtn.setOnClickListener {
            val name = binding.nameText.text.toString()
            val email = binding.emailText.text.toString()
            val phone = binding.phoneText.text.toString()
            val country = binding.countryText.text.toString()
            val city = binding.cityText.text.toString()
            val password = binding.passText.text.toString()

            if(email.isEmpty()) binding.emailText.error = "Email is required"
            if(password.isEmpty()) binding.passText.error = "Password is required"
            if(name.isEmpty()) binding.nameText.error = "Name is required"
            if(phone.isEmpty()) binding.phoneText.error = "Phone is required"
            if(country.isEmpty()) binding.countryText.error = "Country is required"
            if(city.isEmpty()) binding.cityText.error = "City is required"

            if(email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() &&
                phone.isNotEmpty() && country.isNotEmpty() && city.isNotEmpty()){
                MainActivity.auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {authResult->
                        val userId = authResult.user?.uid
                        if(userId != null) {
                            val userMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "phone" to phone,
                                "country" to country,
                                "city" to city
                            )

                            MainActivity.databaseReference =
                                FirebaseDatabase.getInstance().getReference("users")
                            MainActivity.databaseReference.child(userId!!).setValue(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this,
                                        "Account created successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT)
                                        .show()
                                }
                        } else {
                            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.loginBtn.paintFlags = binding.loginBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.loginBtn.setOnClickListener {
            finish()
        }
    }
}
package com.anasfarooq.i210813

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        binding.resetBtn.setOnClickListener {
            val newPass = binding.newPass.text.toString()
            val confirmPass = binding.confirmNewPass.text.toString()

            if(newPass.isEmpty()) binding.newPass.error = "Password is required"
            if(confirmPass.isEmpty()) binding.confirmNewPass.error = "Password is required"
            if(newPass != confirmPass) binding.confirmNewPass.error = "Passwords do not match"

            if(newPass.isNotEmpty() && confirmPass.isNotEmpty() && newPass == confirmPass) {
                MainActivity.auth.currentUser?.updatePassword(newPass)
                    ?.addOnSuccessListener {
                        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    ?.addOnFailureListener{
                        binding.newPass.error = it.localizedMessage
                    }
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}
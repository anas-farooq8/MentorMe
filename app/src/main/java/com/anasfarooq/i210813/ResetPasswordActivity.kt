package com.anasfarooq.i210813

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        binding.resetBtn.setOnClickListener {
            finish()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}
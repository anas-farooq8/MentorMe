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
import com.anasfarooq.i210813.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.sendBtn.setOnClickListener {
            val email = binding.emailText.text.toString().trim()

            if(email.isEmpty()) {
                binding.emailText.error = "Email is required"
            } else {
                MainActivity.auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        // Clear the text and inform the user that an email has been sent
                        binding.emailText.text.clear()
                        Toast.makeText(this, "Check your email to reset your password.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    .addOnFailureListener {
                        binding.emailText.error = it.localizedMessage
                    }
            }
        }


        binding.loginBtn.setOnClickListener {
            finish()
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}
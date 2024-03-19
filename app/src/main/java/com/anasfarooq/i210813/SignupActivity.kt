package com.anasfarooq.i210813

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.signupBtn.setOnClickListener {
            val name = binding.nameText.text.toString()
            val email = binding.emailText.text.toString()
            val phone = binding.phoneText.text.toString()
            val country = binding.countryText.text.toString()
            val city = binding.cityText.text.toString()
            val password = binding.passText.text.toString()

            if (email.isEmpty()) binding.emailText.error = "Email is required"
            if (password.isEmpty()) binding.passText.error = "Password is required"
            if (name.isEmpty()) binding.nameText.error = "Name is required"
            if (phone.isEmpty()) binding.phoneText.error = "Phone is required"
            if (country.isEmpty()) binding.countryText.error = "Country is required"
            if (city.isEmpty()) binding.cityText.error = "City is required"

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() &&
                phone.isNotEmpty() && country.isNotEmpty() && city.isNotEmpty()
            ) {
                val intent = Intent(this, VerifyPhoneActivity::class.java).apply {
                    putExtra("name", name)
                    putExtra("email", email)
                    putExtra("phone", phone)
                    putExtra("country", country)
                    putExtra("city", city)
                    putExtra("password", password)
                }
                finish()
                startActivity(intent)
            }
        }

        binding.loginBtn.paintFlags = binding.loginBtn.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.loginBtn.setOnClickListener {
            finish()
        }
    }
}
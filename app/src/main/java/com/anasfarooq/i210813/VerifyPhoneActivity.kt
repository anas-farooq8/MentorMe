package com.anasfarooq.i210813

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.Models.UserProfile
import com.anasfarooq.i210813.databinding.ActivityVerifyPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class VerifyPhoneActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyPhoneBinding
    private lateinit var verificationId: String
    private var otpCode = StringBuilder()
    private val MAX_OTP_LENGTH: Int = 6

    private var secondsElapsed = 0
    private val handler = Handler(Looper.getMainLooper())

    private val updateTask = object : Runnable {
        override fun run() {
            // Calculate minutes and seconds from secondsElapsed
            val minutes = TimeUnit.SECONDS.toMinutes(secondsElapsed.toLong())
            val seconds = secondsElapsed % 60 // Use 60 for accurate seconds calculation
            val timeString = String.format("%02d:%02d", minutes, seconds)

            // Update the TextView with the new time
            binding.time.text = timeString

            // Incrementing the number of seconds elapsed
            secondsElapsed++

            // Reset the timer after 30 seconds and show a Toast
            if (secondsElapsed > 30) {
                secondsElapsed = 0 // Reset the counter

                // Show a Toast message indicating the OTP has been sent again
                Toast.makeText(applicationContext, "OTP sent again", Toast.LENGTH_SHORT).show()
            }

            // Post the task again with a delay of 1000 milliseconds (1 second)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityVerifyPhoneBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        val phone = intent.getStringExtra("phone") ?: ""
        binding.phoneNo.text = phone

        binding.backBtn.setOnClickListener {
            finish()
        }

        // Start verification on activity start
        startPhoneNumberVerification(phone)
        setupButtonListeners()

        // Initialize the timer
        handler.postDelayed(updateTask, 1000)

        binding.verifyBtn.setOnClickListener {
            verifyPhoneNumberWithCode(verificationId, otpCode.toString())
        }
    }

    private fun appendOtpDigit(digit: String) {
        if (otpCode.length < MAX_OTP_LENGTH) {
            otpCode.append(digit)
            updateOtpDisplay()
        }
    }

    private fun removeLastOtpDigit() {
        if (otpCode.isNotEmpty()) {
            otpCode.deleteCharAt(otpCode.length - 1)
            updateOtpDisplay()
        }
    }

    private fun updateOtpDisplay() {
        val circles = listOf(
            binding.circle1, binding.circle2, binding.circle3,
            binding.circle4, binding.circle5, binding.circle6
        )

        circles.forEachIndexed { index, view ->
            if (index < otpCode.length) {
                // Change the background of the circle to indicate it contains a digit
                view.setBackgroundResource(R.drawable.circle2)
            } else {
                // Reset to the default background to indicate it's empty
                view.setBackgroundResource(R.drawable.verify_circle)
            }
        }
    }

    private fun setupButtonListeners() {
        val numericButtons = listOf(
            binding.button0, binding.button1, binding.button2,
            binding.button3, binding.button4, binding.button5,
            binding.button6, binding.button7, binding.button8,
            binding.button9, binding.buttonDelete
        )

        numericButtons.forEach { button ->
            button.setOnClickListener { appendOtpDigit((it as AppCompatButton).text.toString()) }
        }

        binding.buttonDelete.setOnClickListener { removeLastOtpDigit() }
    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            30, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks // OnVerificationStateChangedCallbacks
        )
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@VerifyPhoneActivity, "Failed to Verify", Toast.LENGTH_SHORT).show()
            finish()
            e.printStackTrace()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@VerifyPhoneActivity.verificationId = verificationId
        }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        if (verificationId != null && code.isNotEmpty()) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(this, "Verification ID or code is invalid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d("VerifyPhoneActivity", "Verification failed")

        MainActivity.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val name = intent.getStringExtra("name") ?: ""
                    val email = intent.getStringExtra("email") ?: ""
                    val phone = intent.getStringExtra("phone") ?: ""
                    val country = intent.getStringExtra("country") ?: ""
                    val city = intent.getStringExtra("city") ?: ""
                    val password = intent.getStringExtra("password") ?: ""

                    MainActivity.auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener { authResult ->
                            val userId = authResult.user?.uid
                            if (userId != null) {
                                val user = UserProfile(userId, name, email, phone, country, city)

                                val databaseReference = MainActivity.firebasedatabase.getReference("users")
                                databaseReference.child(userId!!).setValue(user)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                                    }
                            }
                            else {
                                Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Sign in failed, handle the error
                    task.exception?.let { exception ->
                        if (exception is FirebaseException) {
                            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(updateTask)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTask)
    }

}

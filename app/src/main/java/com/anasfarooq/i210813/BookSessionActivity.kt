package com.anasfarooq.i210813

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.Models.Booking
import com.anasfarooq.i210813.databinding.ActivityBookSessionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class BookSessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookSessionBinding
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var availability: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityBookSessionBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("mentorName")
        val imagePath = intent.getStringExtra("imagePath")
        val sessionPrice = intent.getIntExtra("sessionPrice", 0)
        availability = intent.getStringExtra("availability")

        binding.nameText.text = name
        binding.sessionPrice.text = "$sessionPrice/S"
        Picasso.get().load(imagePath).into(binding.profileImage)


        val buttons = arrayOf(binding.button1, binding.button2, binding.button3, binding.button4)
        buttons.forEach { button ->
            button.setOnClickListener { view ->
                resetTimeButtonBackgrounds()
                val selectedButton = view as AppCompatButton
                selectedButton.setBackgroundResource(R.drawable.appointment_time_btn_selected)
                selectedTime = selectedButton.text.toString()
            }
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // month is 0-based, so add 1 for the actual month
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        }

        binding.bookAppointment.setOnClickListener {
            if (selectedDate.isNullOrEmpty() || selectedTime.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a date and time", Toast.LENGTH_SHORT).show()
            } else {
                val currentUser = MainActivity.auth.currentUser
                if (currentUser != null && id != null) {
                    checkAndBookMentor(currentUser.uid, id!!, name!!)
                }
            }
        }

    }
    private fun resetTimeButtonBackgrounds() {
        val defaultBackground = R.drawable.appointment_time_btn
        val buttons = arrayOf(binding.button1, binding.button2, binding.button3, binding.button4)
        buttons.forEach { button ->
            button.setBackgroundResource(defaultBackground)
        }
    }

    private fun checkAndBookMentor(userId: String, mentorId: String, mentorName: String) {
        if(availability == "Unavailable") {
            Toast.makeText(applicationContext, "Mentor is unavailable", Toast.LENGTH_SHORT).show()
            return
        }
        val bookingsRef = MainActivity.firebasedatabase.reference.child("bookings").child(userId)
        bookingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var alreadyBooked = false
                for (bookingSnapshot in snapshot.children) {
                    val booking = bookingSnapshot.getValue(Booking::class.java)
                    if (booking?.mentorId == mentorId) {
                        alreadyBooked = true
                        break
                    }
                }
                if (!alreadyBooked) {
                    createBooking(userId, mentorId, mentorName)
                } else {
                    Toast.makeText(applicationContext, "You have already booked this mentor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed to check bookings", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createBooking(userId: String, mentorId: String, mentorName: String) {
        val newBookingRef = MainActivity.firebasedatabase.reference.child("bookings").child(userId).push()
        val bookingId = newBookingRef.key ?: return
        val booking = Booking(bookingId, userId, mentorId, selectedDate!!, selectedTime!!)

        if(!MainActivity.isOnline(this))
            Toast.makeText(this, "The Booking has been added locally. Connect Internet to update Online.", Toast.LENGTH_SHORT).show()

        newBookingRef.setValue(booking)
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Mentor $mentorName booked successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(applicationContext, "Booking failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
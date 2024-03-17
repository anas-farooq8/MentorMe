package com.anasfarooq.i210813

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.anasfarooq.i210813.databinding.ActivityBookedSessionBinding

class BookedSessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookedSessionBinding
    private lateinit var bookedSessionAdapter: BookedSessionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookedSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        binding.backBtn.setOnClickListener {
            finish()
        }


        bookedSessionAdapter = BookedSessionAdapter(MainActivity.bookings, this)
        binding.bookedSessions.layoutManager = LinearLayoutManager(this)
        binding.bookedSessions.adapter = bookedSessionAdapter



    }
}
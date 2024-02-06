package com.anasfarooq.i210813

import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast

class BookSessionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_session)

        //window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // month is 0-based, so add 1 for the actual month
            val selectedDate = "$dayOfMonth/${month + 1}/$year"

            // Check if the selected date is the current date and perform an action
            val currentDate = Calendar.getInstance()
        }

    }
}
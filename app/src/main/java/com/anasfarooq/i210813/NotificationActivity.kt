package com.anasfarooq.i210813

import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val notification1: TextView = findViewById(R.id.notification1)
        notification1.paintFlags = notification1.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val notification2: TextView = findViewById(R.id.notification2)
        notification2.paintFlags = notification2.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val notification3: TextView = findViewById(R.id.notification3)
        notification3.paintFlags = notification3.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val notification4: TextView = findViewById(R.id.notification4)
        notification4.paintFlags = notification4.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        val notification5: TextView = findViewById(R.id.notification5)
        notification5.paintFlags = notification5.paintFlags or Paint.UNDERLINE_TEXT_FLAG

    }

}
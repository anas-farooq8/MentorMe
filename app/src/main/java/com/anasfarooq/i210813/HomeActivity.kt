package com.anasfarooq.i210813

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.anasfarooq.i210813.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    override fun onBackPressed() {
        super.onBackPressed()
        MainActivity.auth.signOut()
        Toast.makeText(this, "Signed Out!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

        val searchBtn: ImageView = findViewById(R.id.searchBtn)
        searchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        val chatBtn: ImageView = findViewById(R.id.chatBtn)
        chatBtn.setOnClickListener {
            val intent = Intent(this, AllChatsActivity::class.java)
            startActivity(intent)
            finish()
        }

        val accountBtn: ImageView = findViewById(R.id.accountBtn)
        accountBtn.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        val notificationBtn: ImageView = findViewById(R.id.notificationBtn)
        notificationBtn.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        val addMentorBtn: ImageView = findViewById(R.id.addMentorBtn)
        addMentorBtn.setOnClickListener {
            val intent = Intent(this, AddMentorActivity::class.java)
            startActivity(intent)
        }

        val mentor1: CardView = findViewById(R.id.mentor1)
        mentor1.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }




        /*        val accountBtn: ImageView = findViewById(R.id.accountBtn)
                accountBtn.setOnClickListener {
                    val intent = Intent(this, ::class.java)
                    startActivity(intent)
                    finish()
                }*/


    }

}
package com.anasfarooq.i210813

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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

/*        val accountBtn: ImageView = findViewById(R.id.accountBtn)
        accountBtn.setOnClickListener {
            val intent = Intent(this, ::class.java)
            startActivity(intent)
            finish()
        }*/


    }
}
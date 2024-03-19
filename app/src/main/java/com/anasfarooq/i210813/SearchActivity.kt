package com.anasfarooq.i210813

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.Models.MentorType
import com.anasfarooq.i210813.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        binding.homeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.chatBtn.setOnClickListener {
            val intent = Intent(this, AllChatsActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.accountBtn.setOnClickListener {
            val intent = Intent(this, MyProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.searchField.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                // Perform the search operation
                val searchText = textView.text.toString().toString()
                proceedToSearchResult(searchText)
                true
            } else {
                false
            }
        }

        binding.addMentorBtn.setOnClickListener {
            val intent = Intent(this, AddMentorActivity::class.java)
            startActivity(intent)
        }

        binding.entrepreneurship.setOnClickListener {
            proceedToSearchResult(mentorType = MentorType.Entrepreneurship)
        }

        binding.education.setOnClickListener {
            proceedToSearchResult(mentorType = MentorType.Education)
        }

        binding.personalGrowth.setOnClickListener {
            proceedToSearchResult(mentorType = MentorType.PersonalGrowth)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

    }
    private fun proceedToSearchResult(searchText: String? = null, mentorType: MentorType? = null) {
        val intent = Intent(this, SearchResultActivity::class.java)
        searchText?.let {
            intent.putExtra("searchQuery", it)
            intent.putExtra("searchType", "text")
            binding.searchField.setText("")
        }
        mentorType?.let {
            intent.putExtra("searchType", "mentorType")
            intent.putExtra("mentorType", it.name)
        }
        startActivity(intent)
    }

}
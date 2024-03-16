package com.anasfarooq.i210813

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anasfarooq.i210813.Models.MentorType
import com.anasfarooq.i210813.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)

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
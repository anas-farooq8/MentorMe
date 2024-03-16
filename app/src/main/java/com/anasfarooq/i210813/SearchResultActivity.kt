package com.anasfarooq.i210813

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.anasfarooq.i210813.Models.Mentor
import com.anasfarooq.i210813.Models.MentorType
import com.anasfarooq.i210813.databinding.ActivitySearchResultBinding

class SearchResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultBinding
    private lateinit var resultMentorList: ArrayList<Mentor>
    private lateinit var resultMentorAdapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // window.setFlags(android.R.attr.windowFullscreen, android.R.attr.windowFullscreen)
        binding.backBtn.setOnClickListener {
            finish()
        }

        resultMentorList = ArrayList()
        resultMentorAdapter = SearchResultAdapter(resultMentorList, this)

        binding.searchResult.layoutManager = LinearLayoutManager(this)
        binding.searchResult.adapter = resultMentorAdapter

        performSearch()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterMentorsByType(mentorType: MentorType) {
        val filteredList = MainActivity.topMentorList.filter { it.type == mentorType }
        resultMentorList.clear()
        resultMentorList.addAll(filteredList)
        resultMentorAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun filterMentorsByName(searchQuery: String) {
        val filteredList = MainActivity.topMentorList.filter { mentor -> mentor.name.contains(searchQuery, ignoreCase = true) }
        resultMentorList.clear()
        resultMentorList.addAll(filteredList)
        resultMentorAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun performSearch() {

        when (intent.getStringExtra("searchType")) {
            "text" -> {
                val searchQuery = intent.getStringExtra("searchQuery") ?: return
                filterMentorsByName(searchQuery)
            }
            "mentorType" -> {
                val mentorTypeStr = intent.getStringExtra("mentorType") ?: return
                val mentorType = MentorType.valueOf(mentorTypeStr)
                filterMentorsByType(mentorType)
            }
        }
    }
}
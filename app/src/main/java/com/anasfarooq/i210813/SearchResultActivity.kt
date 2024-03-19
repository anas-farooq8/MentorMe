package com.anasfarooq.i210813

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
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
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivitySearchResultBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

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
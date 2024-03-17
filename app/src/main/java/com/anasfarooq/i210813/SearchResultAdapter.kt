package com.anasfarooq.i210813

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anasfarooq.i210813.Models.Mentor
import com.squareup.picasso.Picasso

class SearchResultAdapter(private val mentorList: ArrayList<Mentor>, private val context: SearchResultActivity): RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.activity_search_result_card, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun getItemCount(): Int = mentorList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val mentor = mentorList[position]

        holder.name.text = mentor.name
        holder.title.text = mentor.title
        holder.sessionPrice.text = "${mentor.sessionPrice}/S"
        holder.availability.text = mentor.availability
        Picasso.get().load(mentor.imagePath).into(holder.profileImg)

        // Single click listener to open Mentor Profile
        holder.profileImg.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra("id", mentor.id)
                putExtra("name", mentor.name)
                putExtra("title", mentor.title)
                putExtra("description", mentor.description)
                putExtra("imagePath", mentor.imagePath)
            }
            context.startActivity(intent)
        }
    }

    // View Holder Class
    class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.name)
        var profileImg: ImageView = itemView.findViewById(R.id.profileImg)
        var title: TextView = itemView.findViewById(R.id.title)
        var availability: TextView = itemView.findViewById(R.id.availability)
        var sessionPrice: TextView = itemView.findViewById(R.id.sessionPrice)
    }
}
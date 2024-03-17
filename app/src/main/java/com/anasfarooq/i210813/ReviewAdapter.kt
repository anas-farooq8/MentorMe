package com.anasfarooq.i210813

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anasfarooq.i210813.Models.Review

class ReviewAdapter(private val reviewList: ArrayList<Review>, private val context: MyProfileActivity): RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.activity_review_card, parent, false)
        return ReviewViewHolder(view)
    }

    override fun getItemCount(): Int = reviewList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewList[position]

        holder.name.text = review.mentorName
        holder.review.text = review.description
        holder.ratingbar.rating = review.stars
    }

    // View Holder Class
    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.nameText)
        var ratingbar: RatingBar = itemView.findViewById(R.id.ratingBar)
        var review: TextView = itemView.findViewById(R.id.descText)
    }
}
package com.anasfarooq.i210813

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.anasfarooq.i210813.Models.Mentor
import com.squareup.picasso.Picasso

class MentorAdapter(private var list: ArrayList<Mentor>, private var context: Context): RecyclerView.Adapter<MentorAdapter.MentorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.activity_mentor_card, parent, false)
        return MentorViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MentorViewHolder, position: Int) {
        val mentor = list[position]

        holder.name.text = mentor.name
        holder.title.text = mentor.title
        holder.sessionPrice.text = "${mentor.sessionPrice}/S"
        holder.availability.text = mentor.availability
        Picasso.get().load(mentor.imagePath).into(holder.profileImg)

        // Single click listener to open Mentor Profile
        holder.mentorRow.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra("id", mentor.id)
                putExtra("name", mentor.name)
                putExtra("title", mentor.title)
                putExtra("description", mentor.description)
                putExtra("imagePath", mentor.imagePath)
                putExtra("sessionPrice", mentor.sessionPrice)
            }
            context.startActivity(intent)
        }
    }

    // View Holder Class
    class MentorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mentorRow: CardView = itemView.findViewById(R.id.mentorRow)
        var name: TextView = itemView.findViewById(R.id.name)
        var profileImg: ImageView = itemView.findViewById(R.id.profileImg)
        var title: TextView = itemView.findViewById(R.id.title)
        var availability: TextView = itemView.findViewById(R.id.availability)
        var sessionPrice: TextView = itemView.findViewById(R.id.sessionPrice)
    }
}

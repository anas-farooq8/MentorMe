package com.anasfarooq.i210813

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anasfarooq.i210813.Models.Booking
import com.squareup.picasso.Picasso

class BookedSessionAdapter(private val bookedSessionList: ArrayList<Booking>, private val context: BookedSessionActivity): RecyclerView.Adapter<BookedSessionAdapter.BookedSessionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookedSessionViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.activity_booked_session_card, parent, false)
        return BookedSessionViewHolder(view)
    }

    override fun getItemCount(): Int = bookedSessionList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BookedSessionViewHolder, position: Int) {
        val bookedSession = bookedSessionList[position]

        val mentorId = bookedSession.mentorId
        val mentor = MainActivity.topMentorList.find { it.id == mentorId }

        holder.name.text = mentor?.name
        holder.title.text = mentor?.title
        Picasso.get().load(mentor?.imagePath).into(holder.profileImage)
        holder.date.text = bookedSession.date
        holder.time.text = bookedSession.time

        holder.itemView.setOnLongClickListener {
            deleteBooking(bookedSession.id, position)
            true // Indicate that the click was handled
        }
    }

    // View Holder Class
    class BookedSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView = itemView.findViewById(R.id.nameText)
        var title: TextView = itemView.findViewById(R.id.titleText)
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        var date: TextView = itemView.findViewById(R.id.dateText)
        var time: TextView = itemView.findViewById(R.id.timeText)
    }

    // Method to delete the booking from Firebase
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteBooking(bookingId: String, position: Int) {
        val currentUserUID = MainActivity.auth.currentUser?.uid
        val databaseReference = MainActivity.firebasedatabase.getReference("bookings//$currentUserUID")
        databaseReference.child(bookingId).removeValue()
            .addOnSuccessListener {
                notifyDataSetChanged()
                Toast.makeText(context, "Booking deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to delete booking: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
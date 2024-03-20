package com.anasfarooq.i210813

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anasfarooq.i210813.Models.UserProfile
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(private val context: Context, private val userList: ArrayList<UserProfile>): RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_users,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.textUsername.text = user.name

        // Check if imagePath is null or empty
        if (!user.imagePath.isNullOrEmpty()) {
            Picasso.get().load(user.imagePath).into(holder.userImage)
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("userId",user.id)
            intent.putExtra("name", user.name)
            context.startActivity(intent)
        }
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        val textUsername: TextView = view.findViewById(R.id.nameTextView1)
        val userImage: CircleImageView = view.findViewById(R.id.circleImageView20)
    }
}

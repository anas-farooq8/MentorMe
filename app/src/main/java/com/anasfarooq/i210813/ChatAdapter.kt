package com.anasfarooq.i210813

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anasfarooq.i210813.Models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val MSG_TYPE_LEFT = 0
    private val MSG_TYPE_RIGHT = 1
    private var isImage = false
    private var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MSG_TYPE_RIGHT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_right, parent, false)
            isImage = false
            ViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.msg_left, parent, false)
            isImage = true
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chatList[position]
        //val currentTime = Calendar.getInstance().time
        //holder.time.text = chat.time
        holder.msg.text = chat.message

        /* holder.chatview.setOnClickListener{
             val position = holder.adapterPosition
             val del = chatList[position]
             //val msgs: String = del.message
             // Remove item from RecyclerView
             chatList.removeAt(position)
             notifyItemRemoved(position)

             val databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
             databaseReference.child(del.chatId).removeValue()

             //holder.msgTyper.setText("edit msg")
             //holder.msgTyper.setText(del.message)
         }*/

        holder.chatView.setOnLongClickListener() {
            //holder.msg.text = "Long Clicked"
            val position = holder.adapterPosition
            val itemToDelete = chatList[position]

            // Remove item from RecyclerView
            //chatList.removeAt(position)
            //notifyItemRemoved(position)
            chatList[position].message = "This message has been deleted"


            val databaseReference = MainActivity.firebasedatabase.getReference("Chats")
            databaseReference.child(itemToDelete.chatId).setValue(chatList[position])
                .addOnSuccessListener {
                    Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show()
                }
            true
        }

        holder.time.text = chat.time

    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val msg: TextView = view.findViewById(R.id.textView33)
        val chatView: TextView = view.findViewById(R.id.textView33)
        val time: TextView = view.findViewById(R.id.time1)
        //val msgTyper: EditText = view.findViewById(R.id.msgTyper)
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        return if (chatList[position].senderId == firebaseUser!!.uid) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}

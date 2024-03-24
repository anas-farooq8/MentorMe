package com.anasfarooq.i210813

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anasfarooq.i210813.Models.Chat
import com.anasfarooq.i210813.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var firebaseUser: FirebaseUser? = null
    private var reference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()
    private var imagePath: String? = ""
    private var userId: String? = ""

    private var getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imagePath = result.data?.getStringExtra("imagePath")
            if (imagePath != null) {
                sendMessage(firebaseUser!!.uid, userId.toString(), imagePath, "image", "no")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityChatBinding.inflate(layoutInflater)
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

        binding.searchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.cameraBtn.setOnClickListener {
            val intent = Intent(this, PhotoScreenActivity::class.java)
            getResult.launch(intent)
        }

        userId = intent.getStringExtra("userId")
        val name = intent.getStringExtra("name") ?: ""
        imagePath = intent.getStringExtra("imagePath")

        val truncatedText = if (name.length > 10) name.substring(0, 10) + "…" else name
        binding.textView13.text = truncatedText

        binding.callBtn.setOnClickListener {
            val intent = Intent(this, AudioCallActivity::class.java).apply {
                putExtra("name", truncatedText)
                putExtra("picture", imagePath)
            }
            startActivity(intent)
        }

        binding.videoCallBtn.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            startActivity(intent)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }


        firebaseUser = MainActivity.auth.currentUser
        reference = MainActivity.firebasedatabase.getReference("users").child(userId!!)


        // Listen for changes in the global layout
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rect = Rect()
                rootView.getWindowVisibleDisplayFrame(rect)
                val screenHeight = rootView.height

                // Determine the size of the screen that is not visible (potentially covered by the keyboard)
                val keypadHeight = screenHeight - rect.bottom

                if (keypadHeight > screenHeight * 0.15) { // Keyboard is showing
                    adjustCardViewMargin(240)
                    // Scroll to the bottom of the chat list after the data is loaded
                    if (chatList.isNotEmpty()) {
                        binding.userRV.scrollToPosition(chatList.size - 1)
                    }
                } else { // Keyboard is hidden
                    adjustCardViewMargin(0)
                }
            }
        })

        val sendBtn = binding.iconAfter3
        sendBtn.setOnClickListener{
            val message:String = binding.msgTyper.text.toString()
            val msg = binding.msgTyper

            if(message.isEmpty()){
                Toast.makeText(applicationContext, "Message is empty", Toast.LENGTH_SHORT).show()
                msg.setText("")
                return@setOnClickListener
            }

            else{
                sendMessage(firebaseUser!!.uid, userId.toString(), message, "message", "no")
                // sendNotification(userId,message)
                msg.setText("")
            }

        }


        binding.galleryBtn.setOnClickListener {
            checkStoragePermission()
            openGalleryForImageAndVideo()
        }

        binding.voiceBtn.setOnClickListener{

        }

        val chatRecyclerView = findViewById<RecyclerView>(R.id.userRV)
        chatRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        readMessage(firebaseUser!!.uid, userId.toString())
        val chapAdapter = ChatAdapter(this, chatList, onMessageDoubleTap = { message ->
            binding.msgTyper.setText(message) // Assuming msgTyper is your EditText's ID
        })
        chatRecyclerView.adapter = chapAdapter
        if(chatList.isNotEmpty()){
            chatRecyclerView.scrollToPosition(chatList.size - 1)
        }

    }

    private fun adjustCardViewMargin(bottomMarginDp: Int) {
        val layoutParams = binding.cardView2.layoutParams as ViewGroup.MarginLayoutParams
        val bottomMarginInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, bottomMarginDp.toFloat(), resources.displayMetrics).toInt()
        layoutParams.bottomMargin = bottomMarginInPixels
        binding.cardView2.layoutParams = layoutParams
    }

    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun sendMessage(sender: String, receiver: String, originalMessage: String, type: String, EditableHai: String) {
        val chatsRef = MainActivity.firebasedatabase.getReference("Chats")

        if (type == "image" || type == "video") {
            // URI of the image or video to upload
            val fileUri = Uri.parse(originalMessage)
            val mediaType = if (type == "image") "images" else "videos"
            val fileName = UUID.randomUUID().toString() // Unique file name
            val storagePath = "chats/$mediaType/$fileName"
            val storageReference = FirebaseStorage.getInstance().getReference(storagePath)

            // Start the file upload
            storageReference.putFile(fileUri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Use the uploaded file URL as the message
                    val uploadedFileUrl = task.result.toString()
                    createOrUpdateChat(sender, receiver, uploadedFileUrl, type, EditableHai, chatsRef)
                } else {
                    Toast.makeText(this, "Upload failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // If it's not an image or video, directly use the original message
            createOrUpdateChat(sender, receiver, originalMessage, type, EditableHai, chatsRef)
        }
    }

    private fun createOrUpdateChat(sender: String, receiver: String, message: String, type: String, EditableHai: String, chatsRef: DatabaseReference) {
        chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var editableChatFound = false
                for (chatSnapshot in snapshot.children) {
                    val chat = chatSnapshot.getValue(Chat::class.java)
                    if (chat != null && chat.EditableHai == "yes" && chat.senderId == sender && chat.receiverId == receiver) {
                        val updatedChatMap: MutableMap<String, Any> = hashMapOf(
                            "message" to message,
                            "EditableHai" to "no",
                            "time" to "Edited: ${getCurrentTime()}"
                        )
                        chatsRef.child(chat.chatId).updateChildren(updatedChatMap)
                        editableChatFound = true
                        break
                    }
                }
                if (!editableChatFound) {
                    val chatId = chatsRef.push().key!!
                    val newChatMap = hashMapOf(
                        "senderId" to sender,
                        "receiverId" to receiver,
                        "message" to message,
                        "chatId" to chatId,
                        "time" to getCurrentTime(),
                        "type" to type,
                        "EditableHai" to EditableHai
                    )
                    chatsRef.child(chatId).setValue(newChatMap)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ChatActivity, "Failed to send message: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference = MainActivity.firebasedatabase.getReference("Chats")

        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for(dataSnapShot: DataSnapshot in snapshot.children){
                    val chat = dataSnapShot.getValue(Chat::class.java)
                    if((chat!!.senderId == senderId && chat.receiverId == receiverId) ||
                        (chat.senderId == receiverId && chat.receiverId == senderId)) {

                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList, onMessageDoubleTap = { message ->
                    binding.msgTyper.setText(message) // Assuming msgTyper is your EditText's ID
                    var reference: DatabaseReference = MainActivity.firebasedatabase.getReference("Chats")
                })
                val chatRecyclerView = binding.userRV
                chatRecyclerView.adapter = chatAdapter

                // Scroll to the bottom of the chat list after the data is loaded
                if (chatList.isNotEmpty()) {
                    chatRecyclerView.scrollToPosition(chatList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MainActivity.PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedMediaUri: Uri? = data.data
            val mimeType = contentResolver.getType(selectedMediaUri!!)

            if (mimeType != null) {
                if (mimeType.startsWith("image")) {
                    // Handle image
                    sendMessage(firebaseUser!!.uid, userId.toString(), selectedMediaUri.toString(), "image", "no")
                } else if (mimeType.startsWith("video")) {
                    // Handle video
                    sendMessage(firebaseUser!!.uid, userId.toString(), selectedMediaUri.toString(), "video", "no")
                }
            }
        }
    }

    private fun openGalleryForImageAndVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        }
        startActivityForResult(intent, MainActivity.PICK_IMAGE_REQUEST)
    }


    private fun checkStoragePermission() {
        val requestCode = MainActivity.REQUEST_STORAGE_PERMISSION
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 (API level 33) and above, request more specific access permissions
            val missingPermissions = mutableListOf<String>()

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.READ_MEDIA_VIDEO)
            }

            if (missingPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), requestCode)
            }
        } else {
            // For older versions, check for READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
            }
        }
    }


/*    private fun sendNotification(userId: String, message: String) {
        val databaseReference: DatabaseReference = MainActivity.firebasedatabase.getReference("users").child(userId)
        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserProfile::class.java)
                val name = user!!.name
                val token = "e-qVAOhKSJeLiIB3MKFfM7:APA91bHRZu8_RkpaOHkwvoHqBBnjUX-nw9w5Zuw-v8C-7ywpdFNIp9h8OBlrLeHsQx_Vm7g_qKKjL3FNoSh71TirKYTFje3Kov3GhRW9kl8NDnyUxFuNH4Mx-GFWiPq2ijEhFa7xwYbs"
                val senderId = MainActivity.auth.currentUser!!.uid
                val data = hashMapOf(
                    "title" to name,
                    "body" to message,
                    "senderId" to senderId,
                    "receiverId" to userId
                )

                FirebaseMessaging.getInstance().send(
                    RemoteMessage.Builder(token)
                        .setData(data)
                        .build()
                )
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })

    }*/
}
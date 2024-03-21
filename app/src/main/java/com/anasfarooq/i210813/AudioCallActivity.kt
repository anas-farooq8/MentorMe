package com.anasfarooq.i210813

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.databinding.ActivityAudioCallBinding
import com.squareup.picasso.Picasso
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import java.util.concurrent.TimeUnit

class AudioCallActivity : AppCompatActivity() {

    // Fill the App ID of your project generated on Agora Console.
    private val APP_ID = "55e389227193478e93a7bdef83a4d1a3"
    // Fill the channel name.
    private val CHANNEL = "VideoAndAudioCall"
    // Fill the temp token generated on Agora Console.
    private val TOKEN = "007eJxTYFjGeffq1SsiudPepG7pMnlbrqPZ1eAdNuHt5ys+Hh4L/79XYDA1TTW2sDQyMje0NDYxt0i1NE40T0pJTbMwTjRJMUw0XvT6Z2pDICPD57P9rIwMEAjiCzKEZaak5jvmpTiWpmTmOyfm5DAwAAAXVygZ"
    private var mRtcEngine: RtcEngine? = null
    var localUid: Int = 0 // UID of the local user
    var remoteUids = HashSet<Int>()

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("YES", "Remote user joined: $uid")
            // Handle the event when a remote user joins the channel
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Log.d("YES", "Joined channel: $channel, uid: $uid")
            // Handle the event when the local user successfully joins the channel
            localUid = uid
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("YES", "Remote user offline: $uid, reason: $reason")
            remoteUids.remove(uid)
        }

        override fun onError(err: Int) {
            when (err) {
                ErrorCode.ERR_TOKEN_EXPIRED -> Log.e("YES", "Your token has expired")
                ErrorCode.ERR_INVALID_TOKEN -> Log.e("YES", "Your token is invalid")
                else -> Log.e("YES", "Error code: $err")
            }
        }
    }

    private var secondsElapsed = 0
    private val handler = Handler(Looper.getMainLooper())

    private val updateTask = object : Runnable {
        override fun run() {
            // Calculate minutes and seconds from secondsElapsed
            val minutes = TimeUnit.SECONDS.toMinutes(secondsElapsed.toLong())
            val seconds = secondsElapsed % 60 // Use 60 for accurate seconds calculation
            val timeString = String.format("%02d:%02d", minutes, seconds)

            // Update the TextView with the new time
            binding.time.text = timeString

            // Incrementing the number of seconds elapsed
            secondsElapsed++

            // Post the task again with a delay of 1000 milliseconds (1 second)
            handler.postDelayed(this, 1000)
        }
    }


    private lateinit var binding: ActivityAudioCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityAudioCallBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }


        binding.endCallBtn.setOnClickListener {
            finish()
        }

        binding.Name.text = intent.getStringExtra("name") ?: ""
        val imagePath = intent.getStringExtra("picture")

        // Check if imagePath is null or empty
        if (!imagePath.isNullOrEmpty()) {
            Picasso.get().load(imagePath).into(binding.picture)
        }
        // Initialize the timer
        handler.postDelayed(updateTask, 1000)

        // Request microphone permission before initializing and joining the channel
        if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO, MainActivity.PERMISSION_REQ_ID_RECORD_AUDIO)) {
            // If permission is already granted, initialize and join the channel
            initializeAndJoinChannel()
        } else {
            // If permission is not granted, requestMicrophonePermission will handle it
            requestMicrophonePermission()
        }
    }

    private fun initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
        } catch (e: Exception) {
            Log.e("YES", "Failed to create RtcEngine: ${e.message}")
        }
        val options = ChannelMediaOptions()
        // For a Video/Voice call, set the channel profile as COMMUNICATION.
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        // Set the client role to broadcaster or audience
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, localUid, options)
        Log.d("YES", "Call started")
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MainActivity.PERMISSION_REQ_ID_RECORD_AUDIO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted.
                    initializeAndJoinChannel()
                } else {
                    // Permission not granted.
                    Toast.makeText(this, "Microphone permission is needed for audio calls", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    private fun requestMicrophonePermission() {
        checkSelfPermission(android.Manifest.permission.RECORD_AUDIO, MainActivity.PERMISSION_REQ_ID_RECORD_AUDIO)
    }

    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            return false
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        // End the call or release any resources related to the call
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
        handler.removeCallbacks(updateTask)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTask)
    }
}
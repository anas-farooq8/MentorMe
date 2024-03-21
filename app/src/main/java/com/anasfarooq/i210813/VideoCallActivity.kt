package com.anasfarooq.i210813

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.databinding.ActivityVideoCallBinding
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.video.VideoCanvas


class VideoCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoCallBinding
    private var mRtcEngine: RtcEngine? = null

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                setupRemoteVideo(uid)
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                // Handle user offline
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityVideoCallBinding.inflate(layoutInflater)
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

        try {
            mRtcEngine = RtcEngine.create(baseContext, "55e389227193478e93a7bdef83a4d1a3", mRtcEventHandler)
        } catch (e: Exception) {
            throw RuntimeException("Check Agora initialization: " + e.message)
        }

        setupVideoConfig()
        joinChannel()

    }

    private fun setupVideoConfig() {
        // Enable the video module
        mRtcEngine?.enableVideo()

        // Set up the local video feed
        val localVideoFrame = RtcEngine.CreateRendererView(baseContext)
        localVideoFrame.setZOrderMediaOverlay(true)
        binding.localVideoViewContainer.addView(localVideoFrame)
        mRtcEngine?.setupLocalVideo(VideoCanvas(localVideoFrame, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }
    private fun setupLocalVideo() {
        val localVideoFrame = RtcEngine.CreateRendererView(baseContext).apply { setZOrderMediaOverlay(true) }
        binding.remoteVideoViewContainer.addView(localVideoFrame) // Assuming this is your container
        mRtcEngine?.setupLocalVideo(VideoCanvas(localVideoFrame, VideoCanvas.RENDER_MODE_FIT, 0))
    }

    private fun joinChannel() {
        // Join a channel with a token and channel name.
        // Pass token as null if you have not enabled App Certificate.
        mRtcEngine?.joinChannel("007eJxTYEhyCIrz4E58tW/yjpz78al3QzW/VhR45MkpLLRtY1Hw+KPAYGqaamxhaWRkbmhpbGJukWppnGielJKaZmGcaJJimGis5Pw7tSGQkWHnbVcmRgYIBPFZGJITc3IYGAC2uh2a", "call", "Extra Optional Data", 0)
    }

    private fun setupRemoteVideo(uid: Int) {
        if (binding.remoteVideoViewContainer.childCount >= 1) {
            return
        }
        val remoteVideoFrame = RtcEngine.CreateRendererView(baseContext)
        binding.remoteVideoViewContainer.addView(remoteVideoFrame)
        mRtcEngine?.setupRemoteVideo(VideoCanvas(remoteVideoFrame, VideoCanvas.RENDER_MODE_FIT, uid))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mRtcEngine != null) {
            RtcEngine.destroy()
            mRtcEngine = null
        }
    }
}
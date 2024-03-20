package com.anasfarooq.i210813

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.anasfarooq.i210813.databinding.ActivityPhotoScreenBinding

class PhotoScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoScreenBinding
    companion object {
        const val CAMERA_REQUEST_CODE = 101
    }

    private lateinit var textureView: TextureView
    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSession: CameraCaptureSession? = null
    private var previewSize: Size? = null
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        binding = ActivityPhotoScreenBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_I210813)
        setContentView(binding.root)
        // for immersive mode
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        textureView = binding.cameraPreview
        textureView.surfaceTextureListener = surfaceTextureListener

        binding.videoBtn.setOnClickListener {
            val intent = Intent(this, VideoScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.crossBtn.setOnClickListener {
            finish()
        }

        binding.clickPhotoBtn.setOnClickListener {
            takePicture()
        }

        checkCameraPermission()
    }

    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
        override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
            setupCamera(width, height)
            openCamera()
        }

        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}

        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true

        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            setupCamera(textureView.width, textureView.height)
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupCamera(textureView.width, textureView.height)
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCamera(width: Int, height: Int) {
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (id in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(id)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id
                    // Directly use the texture view dimensions
                    previewSize = Size(width, height)
                    return
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun openCamera() {
        try {
            cameraId?.let { id ->
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraManager.openCamera(id, object : CameraDevice.StateCallback() {
                        override fun onOpened(camera: CameraDevice) {
                            cameraDevice = camera
                            startPreview()
                        }

                        override fun onDisconnected(camera: CameraDevice) {
                            camera.close()
                        }

                        override fun onError(camera: CameraDevice, error: Int) {
                            camera.close()
                            cameraDevice = null
                        }
                    }, null)
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun startPreview() {
        val surfaceTexture = textureView.surfaceTexture
        surfaceTexture?.let {
            it.setDefaultBufferSize(textureView.width, textureView.height)
            val surface = Surface(it)

            try {
                val previewRequestBuilder = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)?.apply {
                    addTarget(surface)
                }

                cameraDevice?.createCaptureSession(
                    listOf(surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            cameraCaptureSession = session
                            previewRequestBuilder?.let { requestBuilder ->
                                session.setRepeatingRequest(requestBuilder.build(), null, null)
                            }
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                            Toast.makeText(this@PhotoScreenActivity, "Failed to start camera preview", Toast.LENGTH_SHORT).show()
                        }
                    },
                    null
                )
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private fun takePicture() {
        cameraDevice?.let { device ->
            // Prepare the capture request
            val captureRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE).apply {
                val surfaceTexture = textureView.surfaceTexture
                val surface = Surface(surfaceTexture)
                addTarget(surface)
            }

            // Capture callback handling capture events
            val captureCallback = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
                    super.onCaptureCompleted(session, request, result)

                    val image = binding.image
                    image.setImageBitmap(textureView.bitmap)

                    // saving the image.
                    val imageToStore = textureView.bitmap
                    imagePath = MediaStore.Images.Media.insertImage(contentResolver, imageToStore, "Title", "Description")
                    Toast.makeText(this@PhotoScreenActivity, "Image saved", Toast.LENGTH_SHORT).show()

                    // Prepare data intent
                    val resultIntent = Intent().apply {
                        // Put the string imagePath as a result
                        putExtra("imagePath", imagePath)
                    }
                    // Set result with RESULT_OK signal and the data intent
                    setResult(Activity.RESULT_OK, resultIntent)

                    // Finish the activity
                    finish()
                }
            }

            // Execute the capture request.
            cameraCaptureSession?.apply {
                stopRepeating() // Stop the current preview
                capture(captureRequestBuilder.build(), captureCallback, null)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        cameraCaptureSession?.close()
        cameraCaptureSession = null
        cameraDevice?.close()
        cameraDevice = null
    }

}

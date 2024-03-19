package com.anasfarooq.i210813

import android.Manifest
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
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PhotoScreenActivity : AppCompatActivity() {
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
        setContentView(R.layout.activity_photo_screen)

        textureView = findViewById(R.id.cameraPreview)
        textureView.surfaceTextureListener = surfaceTextureListener

        val videoBtn: ImageView = findViewById(R.id.videoBtn)
        videoBtn.setOnClickListener {
            val intent = Intent(this, VideoScreenActivity::class.java)
            startActivity(intent)
            finish()
        }

        val crossBtn: ImageView = findViewById(R.id.crossBtn)
        crossBtn.setOnClickListener {
            finish()
        }

        val clickPhotoBtn: ImageView = findViewById(R.id.clickPhotoBtn)
        clickPhotoBtn.setOnClickListener {
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

                    val image = findViewById<ImageView>(R.id.image)
                    image.setImageBitmap(textureView.bitmap)

                    // saving the image.
                    val imageToStore = textureView.bitmap
                    imagePath = MediaStore.Images.Media.insertImage(contentResolver, imageToStore, "Title", "Description")
                    Toast.makeText(this@PhotoScreenActivity, "Image saved", Toast.LENGTH_SHORT).show()
                    Toast.makeText(this@PhotoScreenActivity, "Image path ${imagePath}", Toast.LENGTH_SHORT).show()
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

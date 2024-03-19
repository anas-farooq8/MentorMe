package com.anasfarooq.i210813

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.widget.Toast

class ScreenshotContentObserver(
    handler: Handler,
    private val context: Context
) : ContentObserver(handler) {

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        if (uri != null) {
            checkForScreenshot(uri)
        }
    }

    private fun checkForScreenshot(uri: Uri) {
        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val fileNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                val fileName = cursor.getString(fileNameIndex)

                // This is a simple check, and you might need a more robust one.
                if (fileName.contains("screenshot", ignoreCase = true)) {
                    Toast.makeText(context, "Screenshot taken: $fileName", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}

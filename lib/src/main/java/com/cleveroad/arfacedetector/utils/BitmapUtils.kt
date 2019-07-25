package com.cleveroad.arfacedetector.utils

import android.content.Context
import android.graphics.*
import android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
import android.util.Log
import androidx.core.content.ContextCompat
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.common.FrameMetadata
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Utils functions for bitmap conversions.
 */
object BitmapUtils {

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? =
        ContextCompat.getDrawable(context, drawableId)?.run {
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            setBounds(0, 0, canvas.width, canvas.height)
            draw(canvas)
            return bitmap
        }

    // Convert NV21 format byte buffer to bitmap.
    fun getBitmap(data: ByteBuffer, metadata: FrameMetadata): Bitmap? {
        data.rewind()
        val imageInBuffer = ByteArray(data.limit())
        data.get(imageInBuffer, 0, imageInBuffer.size)
        try {
            YuvImage(imageInBuffer, ImageFormat.NV21, metadata.width, metadata.height, null).let { image ->
                val stream = ByteArrayOutputStream()
                image.compressToJpeg(Rect(0, 0, metadata.width, metadata.height), 80, stream)
                val bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
                stream.close()
                return rotateBitmap(bitmap, metadata.rotation, metadata.cameraFacing)
            }
        } catch (e: Exception) {
            Log.e("VisionProcessorBase", "Error: " + e.message)
        }

        return null
    }

    // Rotates a bitmap if it is converted from a bytebuffer.
    private fun rotateBitmap(bitmap: Bitmap, rotation: Int, facing: Int): Bitmap {
        val matrix = Matrix()
        var rotationDegree = 0
        when (rotation) {
            FirebaseVisionImageMetadata.ROTATION_90 -> rotationDegree = 90
            FirebaseVisionImageMetadata.ROTATION_180 -> rotationDegree = 180
            FirebaseVisionImageMetadata.ROTATION_270 -> rotationDegree = 270
        }

        // Rotate the image back to straight.}
        matrix.postRotate(rotationDegree.toFloat())
        if (facing == CAMERA_FACING_FRONT) {
            // Mirror the image along X axis for front-facing camera image.
            matrix.postScale(-1.0f, 1.0f)
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun rotateBitmap(source: Bitmap, facing: Int, angleY: Float, angleZ: Float): Bitmap {
        var resultAngleY = angleY
        var resultAngleZ = angleZ
        val matrix = Matrix()
        val camera = Camera()
        camera.save()
        if (facing == CAMERA_FACING_FRONT) {
            resultAngleY *= -1f
            resultAngleZ *= -1f
        }
        camera.rotateZ(resultAngleZ)
        camera.rotateY(resultAngleY)
        camera.getMatrix(matrix)
        camera.restore()

        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}
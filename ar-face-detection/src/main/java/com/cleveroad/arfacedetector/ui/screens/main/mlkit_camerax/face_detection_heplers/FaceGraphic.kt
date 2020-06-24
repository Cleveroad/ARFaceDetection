package com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.face_detection_heplers

import android.graphics.*
import com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.common.CameraXBitmapUtils
import com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.common.GraphicOverlay
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark.*
import kotlin.math.abs

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic(
        overlay: GraphicOverlay,
        private val firebaseVisionFace: FirebaseVisionFace,
        private val overlayBitmap: Bitmap?,
        private val cameraFacing: Int
) : GraphicOverlay.Graphic(overlay) {

    companion object {
        private const val FACE_DECREASING_COEFFICIENT = 300f
        private const val MOUTH_DIFFERENCE_COEFFICIENT = 1.8f
        private const val ANGLE_COEFFICIENT_X = 30
        private const val ANGLE_COEFFICIENT_Y = 50
    }

    override fun draw(canvas: Canvas) {
        drawImage(canvas, firebaseVisionFace)
    }

    private fun drawImage(canvas: Canvas, face: FirebaseVisionFace) {
        val leftEye = face.getLandmark(LEFT_EYE)?.position ?: return
        val rightEye = face.getLandmark(RIGHT_EYE)?.position ?: return
        val mouthBottom = face.getLandmark(MOUTH_BOTTOM)?.position ?: return

        val faceWidth = face.boundingBox.width()
        val faceHeight = face.boundingBox.height()

        overlayBitmap?.let {
            CameraXBitmapUtils.rotateBitmap(it, cameraFacing, face.headEulerAngleY, face.headEulerAngleZ)
        }?.let {
            // Calculate sizes for bitmap with text label using
            // head sizes (closer or further head from camera)
            val imageEdgeSizeBasedOnFaceSizeX = scaleX(it.width * faceWidth / FACE_DECREASING_COEFFICIENT)
            val imageEdgeSizeBasedOnFaceSizeY = scaleY(it.height * faceHeight / FACE_DECREASING_COEFFICIENT)

            val newBitmap = changeBitmapMatrix(
                    it,
                    imageEdgeSizeBasedOnFaceSizeX,
                    imageEdgeSizeBasedOnFaceSizeY
            )

            // Average not translated eyes coordinate on the OX axis
            val avgEyesX = (leftEye.x + rightEye.x) / 2
            // Average not translated eyes coordinate on the OY axis
            val avgEyesY = (leftEye.y + rightEye.y) / 2

            // Bitmap center coordinate on the OX axis
            val bitmapPositionX = translateX(avgEyesX)
            // Bitmap center coordinate on the OY axis
            val bitmapPositionY = translateY(avgEyesY) - MOUTH_DIFFERENCE_COEFFICIENT * (translateY(mouthBottom.y - avgEyesY))

            // Calculating of positions for bitmap with text label
            var left = (bitmapPositionX - imageEdgeSizeBasedOnFaceSizeX).toInt() // start OX axis
            var top = (bitmapPositionY - imageEdgeSizeBasedOnFaceSizeY).toInt() // start OY axis
            var right = (bitmapPositionX + imageEdgeSizeBasedOnFaceSizeX).toInt() // end OX axis
            var bottom = (bitmapPositionY + imageEdgeSizeBasedOnFaceSizeY).toInt() // end OY axis

            // Recalculating of new positions for bitmap using head tilt via OZ axis
            left = (left + newBitmap.width * face.headEulerAngleZ / ANGLE_COEFFICIENT_X).toInt()
            right = (right + newBitmap.width * face.headEulerAngleZ / ANGLE_COEFFICIENT_X).toInt()
            top = (top + newBitmap.height * abs(face.headEulerAngleZ) / ANGLE_COEFFICIENT_Y).toInt()
            bottom = (bottom + newBitmap.height * abs(face.headEulerAngleZ) / ANGLE_COEFFICIENT_Y).toInt()

            canvas.drawBitmap(newBitmap, null, Rect(left, top, right, bottom), null)
        }
    }

    private fun changeBitmapMatrix(bitmap: Bitmap, x: Float, y: Float) =
            Matrix()
                    .apply { postScale(x / bitmap.width, y / bitmap.height) }
                    .let { matrix ->
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    }
}
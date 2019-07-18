package com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers

import android.graphics.*
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.BitmapUtils.rotateBitmap
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.GraphicOverlay
import com.google.ar.sceneform.math.Vector3
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark.*

/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic(
    overlay: GraphicOverlay,
    private val firebaseVisionFace: FirebaseVisionFace?,
    private val overlayBitmap: Bitmap?
) :
    GraphicOverlay.Graphic(overlay) {

    /**
     * Draws the face annotations for position on the supplied canvas.
     */

    private val facePositionPaint = Paint().apply {
        color = Color.WHITE
    }

    override fun draw(canvas: Canvas) {
        val face = firebaseVisionFace ?: return

        test(canvas, face)
    }

    private fun test(canvas: Canvas, face: FirebaseVisionFace) {


//        val centerX = translateX(face.boundingBox.centerX().toFloat())
//
//        val yOffset = scaleY(face.boundingBox.height() / 2.0f)
//        val top = translateY(face.boundingBox.centerY().toFloat()) - yOffset
//        val bottom = translateY(face.boundingBox.centerY().toFloat()) + yOffset
//y' = y*cos(a) + x*sin(a), x' = - y*sin(a) + x*cos(a)
//        val first = Vector3(0F, top, 0F)
//        val angleFirst = Quaternion.axisAngle(first, face.headEulerAngleZ)
//        val result = Quaternion.rotateVector(angleFirst, first)
//        val x = result.x + centerX
//        val y = result.y

        val leftEyePosition = face.getLandmark(LEFT_EYE)?.position ?: return
        val rightEyePosition = face.getLandmark(RIGHT_EYE)?.position ?: return
        val noseBasePosition = face.getLandmark(NOSE_BASE)?.position ?: return
        val centerEyes =
            Vector3(
                (leftEyePosition.x + rightEyePosition.x) / 2,
                (leftEyePosition.y + rightEyePosition.y) / 2, 0F
            )
        val direction = Vector3(centerEyes.x - noseBasePosition.x, centerEyes.y - noseBasePosition.y, 0F).normalized()
        val faceHeight = face.boundingBox.height() / 2
        direction.x *= faceHeight
        direction.y *= faceHeight

        val x = translateX(centerEyes.x + direction.x)
        val y = translateY(centerEyes.y + direction.y)

        overlayBitmap?.let {
            rotateBitmap(it, face.headEulerAngleY * -1, face.headEulerAngleZ * -1)
        }?.let {
            val imageEdgeSizeBasedOnFaceSizeX = it.width
            val imageEdgeSizeBasedOnFaceSizeY = it.height

            val left = (x - imageEdgeSizeBasedOnFaceSizeX).toInt()
            val top = (y - imageEdgeSizeBasedOnFaceSizeY).toInt()
            val right = (x + imageEdgeSizeBasedOnFaceSizeX).toInt()
            val bottom = (y + imageEdgeSizeBasedOnFaceSizeY).toInt()

            canvas.drawBitmap(it, null, Rect(left, top, right, bottom), null)

        }
    }
}

package com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers

import android.graphics.*
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
        val leftEarPosition = face.getLandmark(LEFT_EAR)?.position ?: return
        val rightEarPosition = face.getLandmark(RIGHT_EAR)?.position ?: return
        val noseBasePosition = face.getLandmark(NOSE_BASE)?.position ?: return
        val centerEars =
            Vector3(
                (leftEarPosition.x + rightEarPosition.x) / 2,
                (leftEarPosition.y + rightEarPosition.y) / 2, 0F
            )
        val direction = Vector3(centerEars.x - noseBasePosition.x, centerEars.y - noseBasePosition.y, 0F).normalized()
        val faceHeight = face.boundingBox.height()/2
        direction.x *= faceHeight
        direction.y *= faceHeight
        val x = translateX(centerEars.x + direction.x)
        val y = translateY(centerEars.y + direction.y)
        overlayBitmap?.let {
            //            rotateBitmap(it, face.headEulerAngleZ)
            it
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

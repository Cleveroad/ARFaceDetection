package com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers

import android.graphics.*
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.BitmapUtils.rotateBitmap
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.GraphicOverlay
import com.cleveroad.bootstrap.kotlin_ext.safeLet
import com.google.ar.sceneform.math.Vector3
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour.NOSE_BRIDGE

/** Graphic instance for rendering face contours graphic overlay view.  */
class FaceContourGraphic(
    overlay: GraphicOverlay,
    private val firebaseVisionFace: FirebaseVisionFace?,
    private val overlayBitmap: Bitmap?,
    private val cameraFacing: Int
) : GraphicOverlay.Graphic(overlay) {

    private val facePositionPaint: Paint

    init {
        val selectedColor = Color.WHITE

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor
    }

    /** Draws the face annotations for position on the supplied canvas.  */
    override fun draw(canvas: Canvas) {
        val face = firebaseVisionFace ?: return
        test(canvas, face)
    }

    private fun test(canvas: Canvas, face: FirebaseVisionFace) {
        face.getContour(NOSE_BRIDGE)?.points?.run {
            sortByDescending { it.y }
            safeLet(firstOrNull(), lastOrNull()) { noseTop, noseBottom ->
                val faceHeight = face.boundingBox.height()
                val direction = Vector3(noseTop.x - noseBottom.x, noseTop.y - noseBottom.y, 0F).normalized()
                direction.x *= faceHeight
                direction.y *= faceHeight
                val x = translateX(noseTop.x + direction.x * -1)
                val y = translateY(noseTop.y + direction.y * -1)
                overlayBitmap?.let {
                    rotateBitmap(it, cameraFacing, face.headEulerAngleY, face.headEulerAngleZ)
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
    }
}
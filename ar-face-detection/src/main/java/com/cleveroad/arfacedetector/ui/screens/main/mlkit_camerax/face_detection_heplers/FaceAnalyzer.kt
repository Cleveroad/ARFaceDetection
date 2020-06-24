package com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.face_detection_heplers

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.common.FrameMetadata
import com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.common.GraphicOverlay
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

class FaceAnalyzer(
        private val graphicOverlay: GraphicOverlay,
        private val facing: Int,
        overlayBitmap: Bitmap
) : ImageAnalysis.Analyzer {

    private val faceDetectionProcessor = FaceDetectionProcessor(overlayBitmap)

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {
        image.image?.let {
            faceDetectionProcessor.process(
                    it,
                    image,
                    getFrameMetadata(image.imageInfo.rotationDegrees),
                    graphicOverlay,
                    facing
            )
        }
    }

    private fun getFrameMetadata(rotationDegrees: Int) = FrameMetadata.Builder()
            .setRotation(rotationDegreesToFirebaseRotation(rotationDegrees))
            .setCameraFacing(facing)
            .build()

    private fun rotationDegreesToFirebaseRotation(rotationDegrees: Int) = when (rotationDegrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw IllegalArgumentException("Rotation $rotationDegrees not supported")
    }
}
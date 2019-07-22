package com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers

import androidx.camera.core.CameraX.LensFacing
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.FrameMetadata
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.GraphicOverlay
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

interface FaceAnalyzerListener {

}

class FaceAnalyzer(
    private val listener: FaceAnalyzerListener? = null,
    private val previewWidth: Int,
    private val previewHeight: Int,
    private val graphicOverlay: GraphicOverlay,
    private val facing: LensFacing
) : ImageAnalysis.Analyzer {

    private val faceDetectionProcessor =
        FaceDetectionProcessor()

    override fun analyze(image: ImageProxy?, rotationDegrees: Int) {
        image?.image?.let {
            faceDetectionProcessor.process(
                it,
                FrameMetadata.Builder()
                    .setWidth(previewWidth)
                    .setHeight(previewHeight)
                    .setRotation(rotationDegreesToFirebaseRotation(rotationDegrees))
                    .setCameraFacing(facing)
                    .build(),
                graphicOverlay
            )
        }
    }

    private fun rotationDegreesToFirebaseRotation(rotationDegrees: Int) = when (rotationDegrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw IllegalArgumentException("Rotation $rotationDegrees not supported")
    }
}
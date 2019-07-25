package com.cleveroad.arfacedetector.ui.screens.main.mlkit.face_detection_heplers

import android.graphics.Bitmap
import android.util.Log
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.common.CameraImageGraphic
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.common.FrameMetadata
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.common.GraphicOverlay
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.common.VisionProcessorBase
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.NO_CONTOURS
import java.io.IOException


class FaceDetectionProcessor(private val overlayBitmap: Bitmap) : VisionProcessorBase<List<FirebaseVisionFace>>() {
    companion object {

        private val TAG = "FaceDetectionProcessor"

        private const val MIN_FACE_SIZE = 0.4F
    }

    private val detector: FirebaseVisionFaceDetector

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setMinFaceSize(MIN_FACE_SIZE)
                .setContourMode(NO_CONTOURS)
                .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionFace>> {
        return detector.detectInImage(image)
    }

    override fun onSuccess(originalCameraImage: Bitmap,
                           results: List<FirebaseVisionFace>,
                           frameMetadata: FrameMetadata,
                           graphicOverlay: GraphicOverlay) {
        graphicOverlay.clear()
        val imageGraphic = CameraImageGraphic(graphicOverlay, originalCameraImage)
        graphicOverlay.add(imageGraphic)
        for (i in results.indices) {
            val face = results[i]
            val faceGraphic = FaceGraphic(graphicOverlay, face, overlayBitmap, frameMetadata.cameraFacing)
            graphicOverlay.add(faceGraphic)
        }
        graphicOverlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }
}
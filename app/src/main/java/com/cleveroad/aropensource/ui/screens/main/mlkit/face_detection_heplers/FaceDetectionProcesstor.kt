package com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers

import android.graphics.Bitmap
import android.util.Log
import com.cleveroad.aropensource.ARApp
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.BitmapUtils
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.FrameMetadata
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.GraphicOverlay
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.VisionProcessorBase
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.NO_CONTOURS
import java.io.IOException

/** Face Detector Demo.  */
class FaceDetectionProcessor : VisionProcessorBase<List<FirebaseVisionFace>>() {

    private val detector: FirebaseVisionFaceDetector

    private val overlayBitmap: Bitmap

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.NO_CLASSIFICATIONS)
            .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .enableTracking()
            .setMinFaceSize(0.4F)
            .setContourMode(NO_CONTOURS)
            .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)

        overlayBitmap = BitmapUtils.getBitmapFromVectorDrawable(ARApp.instance, R.drawable.ic_joincleveroad_medium)
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

    override fun onSuccess(
        results: List<FirebaseVisionFace>,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        graphicOverlay.clear()
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

    companion object {

        private val TAG = "FaceDetectionProcessor"
    }
}
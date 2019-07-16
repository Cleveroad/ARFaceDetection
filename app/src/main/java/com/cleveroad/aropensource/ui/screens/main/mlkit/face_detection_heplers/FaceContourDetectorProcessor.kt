package com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers

import android.graphics.Bitmap
import android.util.Log
import com.cleveroad.aropensource.ARApp
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.BitmapUtils.getBitmapFromVectorDrawable
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.CameraImageGraphic
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.FrameMetadata
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.GraphicOverlay
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.VisionProcessorBase
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.NO_LANDMARKS
import java.io.IOException

/**
 * Face Contour Demo.
 */
class FaceContourDetectorProcessor : VisionProcessorBase<List<FirebaseVisionFace>>() {

    private val detector: FirebaseVisionFaceDetector

    private val overlayBitmap: Bitmap

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
            .setLandmarkMode(NO_LANDMARKS)
            .build()

        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        overlayBitmap = getBitmapFromVectorDrawable(ARApp.instance, R.drawable.ic_joincleveroad_medium)
    }

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Contour Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<List<FirebaseVisionFace>> {
        return detector.detectInImage(image)
    }

    override fun onSuccess(
        originalCameraImage: Bitmap?,
        results: List<FirebaseVisionFace>,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        graphicOverlay.clear()

        originalCameraImage?.let {
            val imageGraphic = CameraImageGraphic(graphicOverlay, it)
            graphicOverlay.add(imageGraphic)
        }

        results.forEach {
            val faceGraphic = FaceContourGraphic(graphicOverlay, it, overlayBitmap)
            graphicOverlay.add(faceGraphic)
        }

        graphicOverlay.postInvalidate()
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    companion object {
        private const val TAG = "FaceContourDetectorProc"
    }
}
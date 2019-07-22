package com.cleveroad.aropensource.ui.screens.main.mlkit.common

import android.media.Image
import androidx.annotation.GuardedBy
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(FirebaseVisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
abstract class VisionProcessorBase<T> : VisionImageProcessor {
    // To keep the images and metadata in process.
    @GuardedBy("this")
    private var processingImage: Image? = null

    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null

    @Synchronized
    override fun process(image: Image, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {
        processingImage = image
        processingMetaData = frameMetadata
        processLatestImage(graphicOverlay)
    }

    @Synchronized
    private fun processLatestImage(graphicOverlay: GraphicOverlay) {
        if (processingImage != null && processingMetaData != null) {
            processImage(processingImage!!, processingMetaData!!, graphicOverlay)
        }
    }

    private fun processImage(image: Image, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {
        detectInVisionImage(
            FirebaseVisionImage.fromMediaImage(image, frameMetadata.rotation),
            frameMetadata,
            graphicOverlay
        )
    }

    private fun detectInVisionImage(
        image: FirebaseVisionImage,
        metadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        detectInImage(image)
            .addOnSuccessListener { results ->
                onSuccess(results, metadata, graphicOverlay)
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun stop() {}

    protected abstract fun detectInImage(image: FirebaseVisionImage): Task<T>

    /**
     * Callback that executes with a successful detection result.
     *
     * @param originalCameraImage hold the original image from camera, used to draw the background
     * image.
     */
    protected abstract fun onSuccess(
        results: T,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    )

    protected abstract fun onFailure(e: Exception)
}

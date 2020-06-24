package com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.common

import android.media.Image
import androidx.annotation.GuardedBy
import androidx.camera.core.ImageProxy
import com.cleveroad.arfacedetector.ui.base.safeLet
import com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.executor.ScopedExecutor
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.ml.vision.common.FirebaseVisionImage

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(FirebaseVisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
abstract class VisionProcessorBase<T> : VisionImageProcessor {

    private val obj = Any()

    // To keep the images and metadata in process.
    @GuardedBy("this")
    private var processingImage: Image? = null

    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null

    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    @Synchronized
    override fun process(
            image: Image,
            imageProxy: ImageProxy,
            frameMetadata: FrameMetadata,
            graphicOverlay: GraphicOverlay,
            facing: Int
    ) {
        processingImage = image
        processingMetaData = frameMetadata

        if (!isShutdown) processLatestImage(graphicOverlay, imageProxy, facing)
    }

    @Synchronized
    private fun processLatestImage(
            graphicOverlay: GraphicOverlay,
            imageProxy: ImageProxy,
            facing: Int
    ) {
        if (isShutdown) {
            imageProxy.close()
            return
        }

        synchronized(obj) {
            safeLet(processingImage, processingMetaData) { image, metadata ->
                processImage(image, metadata, graphicOverlay, imageProxy, facing)
            }
        }
    }

    private fun processImage(
            image: Image,
            frameMetadata: FrameMetadata,
            graphicOverlay: GraphicOverlay,
            imageProxy: ImageProxy,
            facing: Int
    ) {
        detectInVisionImage(
                FirebaseVisionImage.fromMediaImage(image, frameMetadata.rotation),
                frameMetadata,
                graphicOverlay,
                imageProxy,
                facing
        )
    }

    private fun detectInVisionImage(
            image: FirebaseVisionImage,
            metadata: FrameMetadata,
            graphicOverlay: GraphicOverlay,
            imageProxy: ImageProxy,
            facing: Int
    ) {
        detectInImage(image)
                .addOnSuccessListener(executor, OnSuccessListener { results ->
                    onSuccess(results, metadata, graphicOverlay, facing)
                })
                .addOnFailureListener(executor, OnFailureListener { e -> onFailure(e) })
                .addOnCompleteListener(executor, OnCompleteListener { imageProxy.close() })
    }

    private var isShutdown = false

    override fun stop() {
        executor.shutdown()
        isShutdown = true
    }

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
            graphicOverlay: GraphicOverlay,
            facing: Int
    )

    protected abstract fun onFailure(e: Exception)
}
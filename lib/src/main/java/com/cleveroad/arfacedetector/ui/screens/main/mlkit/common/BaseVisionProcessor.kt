package com.cleveroad.arfacedetector.ui.screens.main.mlkit.common

import android.graphics.Bitmap
import androidx.annotation.GuardedBy
import com.cleveroad.arfacedetector.ui.base.safeLet
import com.cleveroad.arfacedetector.utils.BitmapUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.nio.ByteBuffer

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(FirebaseVisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
abstract class BaseVisionProcessor<T> : VisionImageProcessor {

    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private var latestImage: ByteBuffer? = null

    @GuardedBy("this")
    private var latestImageMetaData: FrameMetadata? = null

    // To keep the images and metadata in process.
    @GuardedBy("this")
    private var processingImage: ByteBuffer? = null

    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null

    @Synchronized
    override fun process(data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {
        latestImage = data
        latestImageMetaData = frameMetadata
        if (processingImage == null && processingMetaData == null) {
            processLatestImage(graphicOverlay)
        }
    }

    @Synchronized
    private fun processLatestImage(graphicOverlay: GraphicOverlay) {
        processingImage = latestImage
        processingMetaData = latestImageMetaData
        latestImage = null
        latestImageMetaData = null
        safeLet(processingImage, processingMetaData) { processingImage, processingMetaData ->
            processImage(processingImage, processingMetaData, graphicOverlay)
        }
    }

    private fun processImage(data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {
        val metadata = FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setWidth(frameMetadata.width)
                .setHeight(frameMetadata.height)
                .setRotation(frameMetadata.rotation)
                .build()

        BitmapUtils.getBitmap(data, frameMetadata)?.let {
            detectInVisionImage(it,
                    FirebaseVisionImage.fromByteBuffer(data, metadata),
                    frameMetadata,
                    graphicOverlay)
        }
    }

    private fun detectInVisionImage(originalCameraImage: Bitmap,
                                    image: FirebaseVisionImage,
                                    metadata: FrameMetadata,
                                    graphicOverlay: GraphicOverlay) {
        detectInImage(image)
                .addOnSuccessListener { results ->
                    onSuccess(originalCameraImage, results, metadata, graphicOverlay)
                    processLatestImage(graphicOverlay)
                }
                .addOnFailureListener { e -> onFailure(e) }
    }

    override fun stop() = Unit

    protected abstract fun detectInImage(image: FirebaseVisionImage): Task<T>

    /**
     * Callback that executes with a successful detection result.
     *
     * @param originalCameraImage hold the original image from camera, used to draw the background
     * image.
     */
    protected abstract fun onSuccess(originalCameraImage: Bitmap,
                                     results: T,
                                     frameMetadata: FrameMetadata,
                                     graphicOverlay: GraphicOverlay)

    protected abstract fun onFailure(e: Exception)
}

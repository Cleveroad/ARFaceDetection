package com.cleveroad.arfacedetector.ui.screens.main.mlkit.common

import com.google.firebase.ml.common.FirebaseMLException

import java.nio.ByteBuffer

/**
 * An inferface to process the images with different ML Kit detectors and custom image models.
 */
interface VisionImageProcessor {

    /**
     * Processes the images with the underlying machine learning models.
     */
    @Throws(FirebaseMLException::class)
    fun process(data: ByteBuffer, frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay)

    /**
     * Stops the underlying machine learning model and release resources.
     */
    fun stop()
}
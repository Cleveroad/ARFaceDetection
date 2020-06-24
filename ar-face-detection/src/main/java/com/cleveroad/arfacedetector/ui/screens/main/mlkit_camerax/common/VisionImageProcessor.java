package com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.common;

import android.media.Image;

import androidx.camera.core.ImageProxy;

import com.google.firebase.ml.common.FirebaseMLException;

/**
 * An inferface to process the images with different ML Kit detectors and custom image models.
 */
public interface VisionImageProcessor {

    /**
     * Processes the images with the underlying machine learning models.
     */
    void process(
            Image image,
            ImageProxy imageProxy,
            FrameMetadata frameMetadata,
            GraphicOverlay graphicOverlay,
            int facing
    ) throws FirebaseMLException;

    /**
     * Stops the underlying machine learning model and release resources.
     */
    void stop();
}
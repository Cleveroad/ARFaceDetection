package com.cleveroad.aropensource.ui.screens.main.mlkit.common;

import com.google.firebase.ml.common.FirebaseMLException;

import java.nio.ByteBuffer;

/**
 * An inferface to process the images with different ML Kit detectors and custom image models.
 */
public interface VisionImageProcessor {

    /**
     * Processes the images with the underlying machine learning models.
     */
    void process(ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay)
            throws FirebaseMLException;

    /**
     * Stops the underlying machine learning model and release resources.
     */
    void stop();
}
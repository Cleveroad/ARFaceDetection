package com.cleveroad.aropensource.ui.screens.main.mlkit.common;

import android.media.Image;
import com.google.firebase.ml.common.FirebaseMLException;

/**
 * An inferface to process the images with different ML Kit detectors and custom image models.
 */
public interface VisionImageProcessor {

    /**
     * Processes the images with the underlying machine learning models.
     */
    void process(Image image, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay)
            throws FirebaseMLException;

    /**
     * Stops the underlying machine learning model and release resources.
     */
    void stop();
}
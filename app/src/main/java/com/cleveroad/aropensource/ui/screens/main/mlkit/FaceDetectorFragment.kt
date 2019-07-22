package com.cleveroad.aropensource.ui.screens.main.mlkit

import android.Manifest.permission.CAMERA
import android.content.Context
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Rational
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.camera.core.*
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleFragment
import com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers.FaceAnalyzer
import com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers.FaceAnalyzerListener
import com.cleveroad.aropensource.utils.AutoFitPreviewBuilder
import com.cleveroad.bootstrap.kotlin_ext.hide
import kotlinx.android.synthetic.main.ml_kit_face_detector_fragment.*


class FaceDetectorFragment : BaseLifecycleFragment<FaceDetectorVM>(), CompoundButton.OnCheckedChangeListener {

    companion object {
        fun newInstance() =
            FaceDetectorFragment().apply {
                arguments = Bundle()
            }
    }

    override val viewModelClass = FaceDetectorVM::class.java

    override val layoutId = R.layout.ml_kit_face_detector_fragment

    private var preview: Preview? = null

    private var displayId = -1

    private var lensFacing = CameraX.LensFacing.FRONT

    /** Internal reference of the [DisplayManager] */
    private var displayManager: DisplayManager? = null

    private var imageAnalyzer: ImageAnalysis? = null

    /**
     * We need a display listener for orientation changes that do not trigger a configuration
     * change, for example if we choose to override config change in manifest or for 180-degree
     * orientation changes.
     */
    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayAdded(displayId: Int) = Unit
        override fun onDisplayRemoved(displayId: Int) = Unit
        override fun onDisplayChanged(displayId: Int) = view?.let { view ->
            if (displayId == this@FaceDetectorFragment.displayId) {
                preview?.setTargetRotation(view.display.rotation)
                imageAnalyzer?.setTargetRotation(view.display.rotation)
            }
        } ?: Unit
    }

    override fun getScreenTitle() = NO_TITLE

    override fun hasToolbar() = false

    override fun getToolbarId() = NO_TOOLBAR

    override fun observeLiveData() = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tbFacingSwitch.run {
            setOnCheckedChangeListener(this@FaceDetectorFragment)
            // Hide the toggle button if there is only 1 camera
            if (Camera.getNumberOfCameras() == 1) hide()
        }

        // Every time the orientation of device changes, recompute layout
        displayManager = viewFinder.context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
        displayManager?.registerDisplayListener(displayListener, null)
        requestPermission(CAMERA) {
            viewFinder.post {
                // Keep track of the display in which this view is attached
                displayId = viewFinder.display.displayId
                startCamera()
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
//        cameraSource?.setFacing(if (isChecked) CAMERA_FACING_FRONT else CAMERA_FACING_BACK)
//        firePreview?.stop()
//        startCameraSource()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        displayManager?.unregisterDisplayListener(displayListener)
    }

    private fun startCamera() {
        // Create configuration object for the viewfinder use case

        // Get screen metrics used to setup camera for full screen resolution
        val metrics = DisplayMetrics().also { viewFinder.display.getRealMetrics(it) }
        val screenAspectRatio = Rational(metrics.widthPixels, metrics.heightPixels)

        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            // We request aspect ratio but no resolution to let CameraX optimize our use cases
            setTargetAspectRatio(screenAspectRatio)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        // Use the auto-fit preview builder to automatically handle size and orientation changes
        preview = AutoFitPreviewBuilder.build(previewConfig, viewFinder)

        // Every time the viewfinder is updated, recompute layout
        preview?.setOnPreviewOutputUpdateListener {
            // To update the SurfaceTexture, we have to remove it and re-add it
            (viewFinder.parent as? ViewGroup)?.run {
                removeView(viewFinder)
                addView(viewFinder, 0)
            }
            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Setup image analysis pipeline that computes average pixel luminance in real time
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            setLensFacing(lensFacing)
            // Use a worker thread for image analysis to prevent preview glitches
            val analyzerThread = HandlerThread("FaceAnalyzer").apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            // In our analysis, we care more about the latest image than analyzing *every* image
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            // Set initial target rotation, we will have to call this again if rotation changes
            // during the lifecycle of this use case
            setTargetRotation(viewFinder.display.rotation)
        }.build()
        imageAnalyzer = ImageAnalysis(analyzerConfig).apply {
            analyzer = FaceAnalyzer(
                object : FaceAnalyzerListener {

                },
                viewFinder.width,
                viewFinder.height,
                fireFaceOverlay,
                lensFacing
            )
        }

        // Apply declared configs to CameraX using the same lifecycle owner
        CameraX.bindToLifecycle(
            viewLifecycleOwner, preview, imageAnalyzer
        )
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }
}
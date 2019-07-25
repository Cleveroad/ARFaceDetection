package com.cleveroad.aropensource.ui.screens.main.mlkit

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Process.THREAD_PRIORITY_URGENT_AUDIO
import android.util.Rational
import android.util.Size
import android.view.View
import android.widget.CompoundButton
import androidx.camera.core.*
import androidx.camera.core.CameraX.LensFacing.BACK
import androidx.camera.core.CameraX.LensFacing.FRONT
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.ui.base.BaseLifecycleFragment
import com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers.FaceAnalyzer
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

    private var lensFacing = FRONT

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

        override fun onDisplayChanged(displayId: Int) {
            view?.let { view ->
                if (displayId == this@FaceDetectorFragment.displayId) {
                    imageAnalyzer?.setTargetRotation(view.display.rotation)
                }
            }
        }
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
                bindCameraUseCases()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        lensFacing = when (lensFacing) {
            FRONT -> BACK
            else -> FRONT
        }
        // Only bind use cases if we can query a camera with this orientation
        CameraX.getCameraWithLensFacing(lensFacing)
        bindCameraUseCases()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        displayManager?.unregisterDisplayListener(displayListener)
    }

    @SuppressLint("RestrictedApi")
    private fun bindCameraUseCases() {
        CameraX.unbindAll()
        val aspectRatio = Rational(viewFinder.width, viewFinder.height)
        val screenSize = Size(viewFinder.width, viewFinder.height)

        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(aspectRatio)
            setTargetResolution(screenSize)
            setLensFacing(lensFacing)
            setTargetRotation(viewFinder.display.rotation)
        }.build()

        preview = AutoFitPreviewBuilder.build(previewConfig, viewFinder)

        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            val analyzerThread = HandlerThread("FaceAnalyzer", THREAD_PRIORITY_URGENT_AUDIO).apply { start() }
            setCallbackHandler(Handler(analyzerThread.looper))
            setLensFacing(lensFacing)
            setTargetAspectRatio(aspectRatio)
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
            setTargetRotation(viewFinder.display.rotation)
            setImageQueueDepth(1)
        }.build()
        imageAnalyzer = ImageAnalysis(analyzerConfig).apply {
            fireFaceOverlay.run {
                setCameraInfo(480, 640, lensFacing)
            }
            analyzer = FaceAnalyzer(fireFaceOverlay, lensFacing)
        }

        CameraX.bindToLifecycle(viewLifecycleOwner, preview, imageAnalyzer)

    }
}
package com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Size
import android.view.View
import android.widget.CompoundButton
import androidx.annotation.DrawableRes
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.cleveroad.arfacedetector.R
import com.cleveroad.arfacedetector.ui.base.BaseLifecycleFragment
import com.cleveroad.arfacedetector.ui.base.safeLet
import com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.common.CameraXBitmapUtils
import com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.executor.ScopedExecutor
import com.cleveroad.arfacedetector.ui.screens.main.mlkit_camerax.face_detection_heplers.FaceAnalyzer
import com.google.android.gms.tasks.TaskExecutors
import kotlinx.android.synthetic.main.fragment_ml_kit_face_detector_camerax.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class FaceDetectorCameraXFragment : BaseLifecycleFragment(), CompoundButton.OnCheckedChangeListener {

    companion object {
        private const val RES_ID_EXTRA = "res_id"

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        private const val DEFAULT_SIZE_WIDTH_4_3 = 960
        private const val DEFAULT_SIZE_HEIGHT_4_3 = 1280

        private const val DEFAULT_SIZE_WIDTH_16_9 = 720
        private const val DEFAULT_SIZE_HEIGHT_16_9 = 1280

        fun newInstance(@DrawableRes resId: Int) = FaceDetectorCameraXFragment().apply {
            arguments = Bundle().apply {
                putInt(RES_ID_EXTRA, resId)
            }
        }
    }

    override val layoutId = R.layout.fragment_ml_kit_face_detector_camerax

    private val cameraExecutor = ScopedExecutor(TaskExecutors.MAIN_THREAD)
    private val defaultSize4x3 = Size(DEFAULT_SIZE_WIDTH_4_3, DEFAULT_SIZE_HEIGHT_4_3)
    private val defaultSize16x9 = Size(DEFAULT_SIZE_WIDTH_16_9, DEFAULT_SIZE_HEIGHT_16_9)

    /** Internal reference of the [DisplayManager] */
    private var displayManager: DisplayManager? = null
    private var preview: Preview? = null
    private var displayId = -1
    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private var imageAnalyzer: ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var resId: Int? = null

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
                if (displayId == this@FaceDetectorCameraXFragment.displayId) {
                    imageAnalyzer?.setTargetRotation(view.display.rotation)
                }
            }
        }
    }

    override fun getScreenTitle() = NO_TITLE
    override fun hasToolbar() = false
    override fun getToolbarId() = NO_TOOLBAR

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        displayManager?.unregisterDisplayListener(displayListener)
    }

    @SuppressLint("RestrictedApi")
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        preview?.clear()
        lensFacing = when (lensFacing) {
            CameraSelector.LENS_FACING_FRONT -> CameraSelector.LENS_FACING_BACK
            else -> CameraSelector.LENS_FACING_FRONT
        }
        // Only bind use cases if we can query a camera with this orientation
        bindCameraUseCases()
    }

    private fun setupUi() {
        arguments?.getInt(RES_ID_EXTRA)?.let { resId = it }

        tbFacingSwitch.setOnCheckedChangeListener(this@FaceDetectorCameraXFragment)

        // Every time the orientation of device changes, recompute layout
        displayManager = viewFinder.context.getSystemService(Context.DISPLAY_SERVICE) as? DisplayManager
        displayManager?.registerDisplayListener(displayListener, null)
        requestPermission(CAMERA) {
            viewFinder.post {
                // Keep track of the display in which this view is attached
                displayId = viewFinder.display.displayId
                setUpCamera()
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Select lensFacing depending on the available cameras
            lensFacing = when {
                hasFrontCamera() -> CameraSelector.LENS_FACING_FRONT
                hasBackCamera() -> CameraSelector.LENS_FACING_BACK
                else -> throw IllegalStateException("Back and front camera are unavailable")
            }

            // Enable or disable switching between cameras
            updateCameraSwitchButton()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun getResolutionUsingAspectRatio() = DisplayMetrics()
            .also { viewFinder.display.getRealMetrics(it) }
            .let { metrics -> aspectRatio(metrics.widthPixels, metrics.heightPixels) }

    private fun bindCameraUseCases() {
        val resolution = getResolutionUsingAspectRatio()
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        preview = Preview.Builder()
                .apply {
                    setTargetResolution(resolution)
                    setTargetRotation(viewFinder.display.rotation)
                }.build()

        imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(resolution)
                .setTargetRotation(viewFinder.display.rotation)
                .build()
                .apply {
                    fireFaceOverlay.run overlay@{
                        setCameraInfo(resolution.width, resolution.height, lensFacing)

                        resId?.let { res ->
                            CameraXBitmapUtils.getBitmapFromVectorDrawable(context, res)
                        }?.let {
                            setAnalyzer(cameraExecutor, FaceAnalyzer(this@overlay, lensFacing, it))
                        }
                    }
                }

        safeLet(preview, imageAnalyzer) { prev, imAnalyzer ->
            cameraProvider?.apply {
                unbindAll()
                bindToLifecycle(viewLifecycleOwner, cameraSelector, prev, imAnalyzer)
            }

            preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
        }
    }

    /** Enabled or disabled a button to switch cameras depending on the available cameras */
    private fun updateCameraSwitchButton() {
        tbFacingSwitch.isEnabled = hasBackCamera() && hasFrontCamera()
    }

    /** Returns true if the device has an available back camera. False otherwise */
    private fun hasBackCamera() = cameraProvider?.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
            ?: false

    /** Returns true if the device has an available front camera. False otherwise */
    private fun hasFrontCamera() = cameraProvider?.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)
            ?: false

    /** Return calculated aspect ratio [AspectRatio.Ratio] */
    private fun aspectRatio(width: Int, height: Int) =
            (max(width, height).toDouble() / min(width, height)).let { previewRatio ->
                if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
                    defaultSize4x3
                } else {
                    defaultSize16x9
                }
            }
}
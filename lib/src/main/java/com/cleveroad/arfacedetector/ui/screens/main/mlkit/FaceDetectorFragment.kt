package com.cleveroad.arfacedetector.ui.screens.main.mlkit

import android.Manifest.permission.CAMERA
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.CompoundButton
import androidx.annotation.DrawableRes
import com.cleveroad.arfacedetector.R
import com.cleveroad.arfacedetector.ui.base.BaseLifecycleFragment
import com.cleveroad.arfacedetector.ui.base.safeLet
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.common.CameraSource
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.common.CameraSource.CAMERA_FACING_BACK
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.common.CameraSource.CAMERA_FACING_FRONT
import com.cleveroad.arfacedetector.ui.screens.main.mlkit.face_detection_heplers.FaceDetectionProcessor
import com.cleveroad.arfacedetector.utils.BitmapUtils
import com.google.firebase.ml.common.FirebaseMLException
import kotlinx.android.synthetic.main.ml_kit_face_detector_fragment.*
import java.io.IOException


class FaceDetectorFragment : BaseLifecycleFragment(), CompoundButton.OnCheckedChangeListener {

    companion object {
        private val LOG_TAG = this::class.java.simpleName
        private const val RES_ID_EXTRA = "res_id"

        fun newInstance(@DrawableRes resId: Int) =
            FaceDetectorFragment().apply {
                arguments = Bundle().apply {
                    putInt(RES_ID_EXTRA, resId)
                }
            }
    }

    override val layoutId = R.layout.ml_kit_face_detector_fragment

    private var cameraSource: CameraSource? = null

    override fun getScreenTitle() = NO_TITLE

    override fun hasToolbar() = false

    override fun getToolbarId() = NO_TOOLBAR

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tbFacingSwitch.run {
            setOnCheckedChangeListener(this@FaceDetectorFragment)
            // Hide the toggle button if there is only 1 camera
            if (Camera.getNumberOfCameras() == 1) visibility = GONE
        }
        requestPermission(CAMERA) {
            createCameraSource()
            startCameraSource()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        cameraSource?.setFacing(if (isChecked) CAMERA_FACING_FRONT else CAMERA_FACING_BACK)
        firePreview?.stop()
        startCameraSource()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /** Stops the camera.  */
    override fun onPause() {
        firePreview?.stop()
        super.onPause()
    }

    override fun onDestroy() {
        cameraSource?.release()
        super.onDestroy()
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        cameraSource?.let {
            try {
                firePreview ?: Log.d(LOG_TAG, "resume: Preview is null")
                fireFaceOverlay ?: Log.d(LOG_TAG, "resume: graphOverlay is null")
                safeLet(firePreview, fireFaceOverlay) { firePreview, fireFaceOverlay ->
                    firePreview.start(cameraSource, fireFaceOverlay)
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Unable to start camera source.", e)
                cameraSource?.release()
                cameraSource = null
            }
        }
    }

    private fun createCameraSource() {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(activity, fireFaceOverlay)
        }
        safeLet(context, arguments?.getInt(RES_ID_EXTRA)) { context, resId ->
            BitmapUtils.getBitmapFromVectorDrawable(context, resId)?.let {
                try {
                    cameraSource?.setMachineLearningFrameProcessor(FaceDetectionProcessor(it))
                } catch (e: FirebaseMLException) {
                    Log.e(LOG_TAG, "can not create camera source: face contour", e)
                }
            }
        }
    }
}
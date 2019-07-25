package com.cleveroad.aropensource.ui.screens.main.mlkit

import android.Manifest.permission.CAMERA
import android.hardware.Camera
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.extensions.printLog
import com.cleveroad.aropensource.extensions.printLogE
import com.cleveroad.aropensource.ui.base.BaseLifecycleFragment
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.CameraSource
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.CameraSource.CAMERA_FACING_BACK
import com.cleveroad.aropensource.ui.screens.main.mlkit.common.CameraSource.CAMERA_FACING_FRONT
import com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers.FaceContourDetectorProcessor
import com.cleveroad.aropensource.ui.screens.main.mlkit.face_detection_heplers.FaceDetectionProcessor
import com.cleveroad.bootstrap.kotlin_ext.hide
import com.cleveroad.bootstrap.kotlin_ext.safeLet
import com.google.firebase.ml.common.FirebaseMLException
import kotlinx.android.synthetic.main.ml_kit_face_detector_fragment.*
import java.io.IOException


class FaceDetectorFragment : BaseLifecycleFragment<FaceDetectorVM>(), CompoundButton.OnCheckedChangeListener {

    companion object {
        fun newInstance() =
            FaceDetectorFragment().apply {
                arguments = Bundle()
            }
    }

    override val viewModelClass = FaceDetectorVM::class.java

    override val layoutId = R.layout.ml_kit_face_detector_fragment

    private var cameraSource: CameraSource? = null

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
                firePreview ?: printLog("resume: Preview is null")

                fireFaceOverlay ?: printLog("resume: graphOverlay is null")

                safeLet(firePreview, fireFaceOverlay) { firePreview, fireFaceOverlay ->
                    firePreview.start(cameraSource, fireFaceOverlay)
                }
            } catch (e: IOException) {
                e.printLogE("Unable to start camera source.")
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
        try {
            cameraSource?.setMachineLearningFrameProcessor(FaceDetectionProcessor())
//            cameraSource?.setMachineLearningFrameProcessor(FaceContourDetectorProcessor())
        } catch (e: FirebaseMLException) {
            e.printLogE("can not create camera source: face contour")
        }
    }
}
package com.cleveroad.arfacedetector.utils

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.google.ar.core.ArCoreApk

object ArUtils {

    private const val MIN_OPENGL_VERSION = 3.0

    private val LOG_TAG = this::class.java.simpleName

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * Finishes the activity if Sceneform can not run
     */
    fun checkIsSupportedDeviceOrFinish(context: Context): Boolean {
        if (ArCoreApk.getInstance().checkAvailability(context) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Log.e(LOG_TAG, "Augmented Faces requires ARCore.")
            return false
        }
        val openGlVersionString =
                (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
                        ?.deviceConfigurationInfo
                        ?.glEsVersion
        openGlVersionString?.toDoubleOrNull()?.let {
            if (it < MIN_OPENGL_VERSION) {
                Log.e(LOG_TAG, "Sceneform requires OpenGL ES 3.0 later")
                return false
            }
        }
        return true
    }
}
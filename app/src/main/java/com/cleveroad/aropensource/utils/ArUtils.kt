package com.cleveroad.aropensource.utils

import android.app.ActivityManager
import android.content.Context
import android.widget.Toast
import com.cleveroad.aropensource.MIN_OPENGL_VERSION
import com.cleveroad.aropensource.extensions.printLogE
import com.google.ar.core.ArCoreApk

object ArUtils {
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
            printLogE("Augmented Faces requires ARCore.")
            Toast.makeText(context, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            return false
        }
        val openGlVersionString =
            (context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
                ?.deviceConfigurationInfo
                ?.glEsVersion
        openGlVersionString?.toDoubleOrNull()?.let {
            if (it < MIN_OPENGL_VERSION) {
                printLogE("Sceneform requires OpenGL ES 3.0 later")
                Toast.makeText(context, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }
}
package com.cleveroad.aropensource.ui.screens.main.face_detector

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.extensions.printLogE
import com.cleveroad.aropensource.ui.base.BaseLifecycleFragment
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode

class AugmentedFacesFragment : BaseLifecycleFragment<AugmentedFacesVM>() {

    companion object {
        fun newInstance() = AugmentedFacesFragment().apply {
            arguments = Bundle()
        }

        private val MIN_OPENGL_VERSION = 3.0
    }

    override val viewModelClass = AugmentedFacesVM::class.java

    override val layoutId = R.layout.fragment_augmented_faces

    private var arFragment: FaceDetectorFragment? = null

    private var faceRegionsRenderable: ModelRenderable? = null

    private var faceMeshTexture: Texture? = null

    private val faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    override fun getScreenTitle() = NO_TITLE

    override fun hasToolbar() = false

    override fun getToolbarId() = NO_TOOLBAR

    override fun observeLiveData() = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!checkIsSupportedDeviceOrFinish()) return
        arFragment = childFragmentManager.findFragmentById(R.id.face_fragment) as? FaceDetectorFragment

        // Load the face regions renderable.
        // This is a skinned model that renders 3D objects mapped to the regions of the augmented face.
        ModelRenderable.builder()
            .setSource(requireContext(), R.raw.fox_face)
            .build()
            .thenAccept { modelRenderable ->
                faceRegionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        // Load the face mesh texture.
        Texture.builder()
            .setSource(requireContext(), R.drawable.fox_face_mesh_texture)
            .build()
            .thenAccept { texture -> faceMeshTexture = texture }

        val sceneView = arFragment?.arSceneView

        // This is important to make sure that the camera stream renders first so that
        // the face mesh occlusion works correctly.
        sceneView?.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        val scene = sceneView?.scene

        scene?.addOnUpdateListener { frameTime: FrameTime ->
            if (faceRegionsRenderable == null || faceMeshTexture == null) {
                return@addOnUpdateListener
            }

            // Make new AugmentedFaceNodes for any new faces.
            sceneView.session?.getAllTrackables(AugmentedFace::class.java)?.forEach { face ->
                if (!faceNodeMap.containsKey(face)) {
                    val faceNode = AugmentedFaceNode(face)
                    faceNode.setParent(scene)
                    faceNode.faceRegionsRenderable = faceRegionsRenderable
                    faceNode.faceMeshTexture = faceMeshTexture
                    faceNodeMap[face] = faceNode
                }
            }

            // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
            val iter = faceNodeMap.entries.iterator()
            while (iter.hasNext()) {
                val entry = iter.next()
                val face = entry.key
                if (face.trackingState == TrackingState.STOPPED) {
                    val faceNode = entry.value
                    faceNode.setParent(null)
                    iter.remove()
                }
            }
        }
    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     *
     * Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     *
     * Finishes the activity if Sceneform can not run
     */
    private fun checkIsSupportedDeviceOrFinish(): Boolean {
        if (ArCoreApk.getInstance().checkAvailability(requireContext()) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            printLogE("Augmented Faces requires ARCore.")
            Toast.makeText(requireContext(), "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            return false
        }
        val openGlVersionString = (activity?.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
            ?.deviceConfigurationInfo
            ?.glEsVersion
        openGlVersionString?.toDoubleOrNull()?.let {
            if (it < MIN_OPENGL_VERSION) {
                printLogE("Sceneform requires OpenGL ES 3.0 later")
                Toast.makeText(requireContext(), "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }
}
package com.packagename.ui.base.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.cleveroad.bootstrap.kotlin_core.ui.BaseDialogFragment
import com.packagename.ui.base.FragmentArgumentDelegate

/**
 * This interface need implement for getting result from [BaseDialogFragment]
 */
interface DialogFragmentCallback {
    fun onDialogResult(requestCode: Int, resultCode: Int, data: Intent = Intent())
}

/**
 * Base class for implement [DialogFragment]
 */
abstract class SimpleDialogFragment : BaseDialogFragment() {

    companion object {
        private const val DEFAULT_REQUEST_CODE = -1
    }

    private var requestCode by FragmentArgumentDelegate<Int>()

    /**
     * Use this method that display the dialog in [Fragment] and call back with [requestCode]
     *
     * @param requestCode code[Int] for call back
     */
    fun <T> T.showForResult(requestCode: Int) where T : Fragment, T : DialogFragmentCallback {
        setTargetFragment(this, requestCode)
        show(fragmentManager, javaClass.simpleName)
    }

    /**
     * Use this method that display the dialog in [AppCompatActivity] and call back with [requestCode]
     *
     * @param requestCode code[Int] for call back
     */
    fun <T> T.showForResult(requestCode: Int) where T : AppCompatActivity, T : DialogFragmentCallback {
        this@SimpleDialogFragment.requestCode = requestCode
        show(supportFragmentManager, javaClass.name)
    }

    protected fun setResult(resultCode: Int, data: Intent = Intent()) {
        var callback: DialogFragmentCallback? = null
        var requestCode = DEFAULT_REQUEST_CODE

        val fragment = targetFragment
        if (fragment is DialogFragmentCallback) {
            callback = fragment
            requestCode = targetRequestCode
        } else if (fragment == null) {
            callback = (activity as? DialogFragmentCallback)?.also {
                requestCode = this.requestCode ?: DEFAULT_REQUEST_CODE
            }
        }
        callback?.takeUnless { requestCode == DEFAULT_REQUEST_CODE }
                ?.onDialogResult(requestCode, resultCode, data)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        setResult(Activity.RESULT_CANCELED)
        super.onDismiss(dialog)
    }
}
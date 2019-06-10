package com.cleveroad.aropensource.ui.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cleveroad.aropensource.utils.hideKeyboard
import com.cleveroad.bootstrap.kotlin_core.ui.BackPressable
import com.cleveroad.bootstrap.kotlin_core.ui.BackPressedCallback
import com.cleveroad.bootstrap.kotlin_ext.hide
import com.cleveroad.bootstrap.kotlin_ext.safeLet
import com.cleveroad.bootstrap.kotlin_ext.show
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.include_progress.*

abstract class BaseLifecycleActivity<T : BaseVM> : AppCompatActivity(),
    BaseView,
    BackPressedCallback,
    BackPressedNavigationCallback {

    companion object {
        private const val BACK_STACK_COUNT_ZERO = 0

        private const val BASE_SNACK_BAR_MAX_LINES = 5
    }

    private var snackbar: Snackbar? = null

    protected abstract val viewModelClass: Class<T>

    protected abstract val containerId: Int

    protected abstract val layoutId: Int

    protected abstract fun observeLiveData()

    protected val viewModel: T by lazy { ViewModelProviders.of(this).get(viewModelClass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        observeAllData()
    }

    private fun observeAllData() {
        observeLiveData()
        viewModel.isLoadingLD.observe(this, Observer<Boolean> {
            it?.let { showProgress(it) }
        })
        viewModel.errorLD.observe(this, Observer<Any> {
            it?.let { onError(it) }
        })
    }

    protected fun replaceFragment(
        fragment: Fragment,
        needToAddToBackStack: Boolean = true,
        @AnimRes inAnimRes: Int = 0,
        @AnimRes outAnimRes: Int = 0
    ) {
        hideKeyboard()
        val name = fragment.javaClass.simpleName
        supportFragmentManager.beginTransaction().apply {
            if (inAnimRes != 0 || outAnimRes != 0) setCustomAnimations(inAnimRes, outAnimRes)
            replace(containerId, fragment, name)
            if (needToAddToBackStack) addToBackStack(name)
        }.commit()
    }

    protected fun addFragment(
        fragment: Fragment,
        needToAddToBackStack: Boolean = true
    ) {
        hideKeyboard()
        val name = fragment.javaClass.simpleName
        supportFragmentManager
            .beginTransaction()
            .add(containerId, fragment, name)
            .apply { takeIf { needToAddToBackStack }?.addToBackStack(name) }
            .commit()
    }

    protected fun removeFragmentByTag(vararg tags: String) {
        hideKeyboard()
        with(supportFragmentManager) {
            for (fragmentTag in tags) {
                findFragmentByTag(fragmentTag)?.let {
                    beginTransaction().remove(it).commit()
                    popBackStackImmediate()
                }
            }
        }
    }

    override fun homePressed() {
        backPressed()
    }

    override fun showProgress() {
        progressView?.show()
    }

    override fun hideProgress() {
        progressView?.hide()
    }

    override fun showSnackbar(message: String) {
        showSnackBar(findViewById(android.R.id.content), message)
    }

    override fun showSnackbar(res: Int) {
        showSnackBar(findViewById(android.R.id.content), getString(res))
    }

    override fun showSnackBar(res: Int, actionRes: Int, callback: () -> Unit) {
        showSnackBarWithAction(findViewById(android.R.id.content), res, actionRes, callback)
    }

    override fun showSnackBar(text: CharSequence, actionRes: Int, callback: () -> Unit) {
        showSnackBarWithAction(findViewById(android.R.id.content), text, actionRes, callback)
    }

    protected fun showSnackBar(rootView: View?, text: String?) {
        safeLet(rootView, text) { view, txt ->
            snackbar = Snackbar.make(view, txt, Snackbar.LENGTH_LONG)
                .apply {
                    view.setUpSnackBarView(this)
                    show()
                }
        }
    }

    protected fun getFragmentByTag(tag: String) = supportFragmentManager.findFragmentByTag(tag)

    protected fun clearFragmentBackStack() {
        supportFragmentManager.fragments.forEach {
            clearBackStackRecursive(it)
        }
    }

    private fun clearBackStackRecursive(fragment: Fragment) {
        fragment.childFragmentManager.run {
            fragments.forEach {
                clearBackStackRecursive(it)
            }
            popBackStack(null, POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun backPressed() {
        if (!hideKeyboard()) {
            with(supportFragmentManager) {
                backStackEntryCount.takeUnless { it == BACK_STACK_COUNT_ZERO }?.let { popBackStack() }
                    ?: onBackPressed()
            }
        }
    }

    override fun onBackPressed() {
        hideKeyboard()
        supportFragmentManager.findFragmentById(containerId)?.takeIf {
            it is BackPressable && it.onBackPressed()
        } ?: super.onBackPressed()
    }

    override fun onError(error: Any) {
        hideProgress()
        when (error) {
            is String -> showSnackbar(error)
            is Int -> showSnackbar(error)
        }
    }

    private fun showSnackBarWithAction(rootView: View?, res: Int, actionRes: Int, callback: () -> Unit) {
        rootView?.let {
            snackbar = Snackbar.make(it, res, Snackbar.LENGTH_LONG).apply {
                setAction(actionRes, { _ -> callback() })
                view.setUpSnackBarView(this)
                show()
            }
        }
    }

    private fun showSnackBarWithAction(rootView: View?, text: CharSequence, actionRes: Int, callback: () -> Unit) {
        rootView?.let {
            snackbar = Snackbar.make(it, text, Snackbar.LENGTH_LONG).apply {
                setAction(actionRes, { _ -> callback() })
                view.setUpSnackBarView(this)
                show()
            }
        }
    }

    private fun View.setUpSnackBarView(snackBar: Snackbar) = with(this) {
        setOnClickListener { snackBar.dismiss() }
        with(findViewById<TextView>(com.google.android.material.R.id.snackbar_text)) {
            maxLines = BASE_SNACK_BAR_MAX_LINES
        }
    }

    override fun hideSnackBar() {
        snackbar?.let { if (it.isShown) it.dismiss() }
    }

    override fun showProgress(isShow: Boolean) {
        if (isShow) showProgress() else hideProgress()
    }

    override fun showAlert(
        message: String,
        title: String?,
        cancelable: Boolean,
        positiveRes: Int,
        positiveFun: () -> Unit,
        negativeRes: Int?,
        negativeFun: () -> Unit
    ) {
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setCancelable(cancelable)
            title?.let { setTitle(it) }
            setPositiveButton(positiveRes) { _, _ -> positiveFun() }
            negativeRes?.let { setNegativeButton(it) { _, _ -> negativeFun() } }
            show()
        }
    }

    override fun showAlert(
        message: Int,
        title: Int?,
        cancelable: Boolean,
        positiveRes: Int,
        positiveFun: () -> Unit,
        negativeRes: Int?,
        negativeFun: () -> Unit
    ) {
        AlertDialog.Builder(this).apply {
            setMessage(message)
            setCancelable(cancelable)
            title?.let { setTitle(it) }
            setPositiveButton(positiveRes) { _, _ -> positiveFun() }
            negativeRes?.let { setNegativeButton(it) { _, _ -> negativeFun() } }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.findFragmentById(containerId)?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        supportFragmentManager.findFragmentById(containerId)
            ?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
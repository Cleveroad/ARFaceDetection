package com.cleveroad.aropensource.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.cleveroad.aropensource.R

abstract class BaseLifecycleFragment : Fragment() {

    companion object {
        const val EMPTY_STRING = ""
        const val NO_TOOLBAR = -1
        const val NO_TITLE = -1
    }

    protected abstract val layoutId: Int

    protected var toolbar: Toolbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(layoutId, container, false)

    override fun onResume() {
        super.onResume()
        initToolbar()
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> onActivityResultOk(requestCode, data)
            Activity.RESULT_CANCELED -> onActivityResultCanceled(requestCode, data)
        }
    }

    @CallSuper
    protected open fun onActivityResultCanceled(requestCode: Int, data: Intent?) = Unit

    @CallSuper
    protected open fun onActivityResultOk(requestCode: Int, data: Intent?) = Unit

    /**
     * Set if need to show back navigation in toolbar
     *
     * @return True if toolbar has back navigation
     * False if toolbar without back navigation
     */
    open protected fun needToShowBackNav() = true

    protected fun backNavigationIcon(): Int? = R.drawable.ic_arrow_gray_back_24dp

    protected fun setupActionBar(actionBar: ActionBar) = actionBar.run {
        title = getStringScreenTitle()
        setDisplayHomeAsUpEnabled(needToShowBackNav())
    }

    open protected fun setScreenTitle(title: CharSequence?) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = title
    }

    /**
     * Set id of screen title
     *
     * @return Id of screen title
     */
    @StringRes
    protected abstract fun getScreenTitle(): Int

    /**
     * Set if fragment has toolbar
     *
     * @return True if fragment has toolbar
     * False if fragment without toolbar
     */
    protected abstract fun hasToolbar(): Boolean

    /**
     * Set id of toolbar
     *
     * @return Toolbar id
     */
    @IdRes
    protected abstract fun getToolbarId(): Int

    open protected fun toolbarInitialized() = Unit

    private fun initToolbar() {
        view?.apply {
            if (hasToolbar() && getToolbarId() != NO_TOOLBAR) {
                toolbar = findViewById(getToolbarId())
                initSupportActionBar()
            }
            toolbarInitialized()
        }
    }

    private fun initSupportActionBar() {
        (activity as? AppCompatActivity)?.run {
            setSupportActionBar(toolbar)
            supportActionBar?.let { setupActionBar(it) }
            initBackNavigation()
        }
    }

    private fun initBackNavigation() {
        if (needToShowBackNav()) {
            backNavigationIcon()?.let { toolbar?.setNavigationIcon(it) }
            toolbar?.setNavigationOnClickListener { activity?.onBackPressed() }
        }
    }

    /**
     * Set [String] screen title
     *
     * @return Screen title
     */
    private fun getStringScreenTitle() = if (getScreenTitle() != NO_TITLE) {
        getString(getScreenTitle())
    } else {
        EMPTY_STRING
    }
}
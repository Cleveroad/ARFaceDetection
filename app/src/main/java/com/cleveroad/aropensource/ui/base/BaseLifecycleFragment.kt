package com.cleveroad.aropensource.ui.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.cleveroad.aropensource.R
import com.cleveroad.aropensource.utils.EMPTY_STRING
import com.cleveroad.aropensource.utils.NO_FLAGS
import com.cleveroad.aropensource.utils.bindInterfaceOrThrow
import com.cleveroad.bootstrap.kotlin_ext.withNotNull
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.Disposable

abstract class BaseLifecycleFragment<T : BaseVM> : Fragment(), BaseView {

    companion object {
        const val NO_TOOLBAR = -1
        const val NO_TITLE = -1
    }

    abstract val viewModelClass: Class<T>

    private val textWatchers: Map<EditText?, TextWatcher> = mutableMapOf()

    private var rxPermission: RxPermissions? = null

    private var backPressedCallback: BackPressedNavigationCallback? = null

    protected val viewModel: T by lazy {
        ViewModelProviders.of(this).get(viewModelClass)
    }

    protected abstract fun observeLiveData()

    protected abstract val layoutId: Int

    protected var toolbar: Toolbar? = null

    private var baseView: BaseView? = null

    private var permissionDisposable: MutableList<Disposable?>? = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseView = bindInterfaceOrThrow<BaseView>(parentFragment, context)
        backPressedCallback = bindInterfaceOrThrow<BackPressedNavigationCallback>(parentFragment, context)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeAllData()
        rxPermission = RxPermissions(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(layoutId, container, false)

    override fun onResume() {
        super.onResume()
        initToolbar()
    }

    override fun onDetach() {
        baseView = null
        rxPermission = null
        backPressedCallback = null
        super.onDetach()
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

    override fun showProgress() {
        baseView?.showProgress()
    }

    override fun hideProgress() {
        baseView?.hideProgress()
    }

    override fun showSnackbar(message: String) {
        baseView?.showSnackbar(message)
    }

    override fun showSnackbar(res: Int) {
        baseView?.showSnackbar(res)
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
        baseView?.showAlert(message, title, cancelable, positiveRes, positiveFun, negativeRes, negativeFun)
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
        baseView?.showAlert(message, title, cancelable, positiveRes, positiveFun, negativeRes, negativeFun)
    }

    override fun hideSnackBar() {
        baseView?.hideSnackBar()
    }

    override fun showProgress(isShow: Boolean) {
        if (isShow) showProgress() else hideProgress()
    }

    override fun showSnackBar(res: Int, actionRes: Int, callback: () -> Unit) {
        baseView?.showSnackBar(res, actionRes, callback)
    }

    override fun showSnackBar(text: CharSequence, actionRes: Int, callback: () -> Unit) {
        baseView?.showSnackBar(text, actionRes, callback)
    }

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

    override fun onError(error: Any) {
        baseView?.onError(error)
    }

    override fun onDestroyView() {
        textWatchers.forEach { (key, value) -> key?.removeTextChangedListener(value) }
        permissionDisposable?.forEach { it?.dispose() }
        super.onDestroyView()
    }

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
        withNotNull(activity as? AppCompatActivity) {
            setSupportActionBar(toolbar)
            supportActionBar?.let { setupActionBar(it) }
            initBackNavigation()
        }
    }

    private fun initBackNavigation() {
        if (needToShowBackNav()) {
            backNavigationIcon()?.let { toolbar?.setNavigationIcon(it) }
            toolbar?.setNavigationOnClickListener { handleNavigation() }
        }
    }

    protected open fun handleNavigation() =
        if (canBackPressed()) backPressed() else backPressedCallback?.homePressed()

    open fun canBackPressed() = true

    fun backPressed() = backPressedCallback?.backPressed()

    private fun observeAllData() {
        observeLiveData()
        viewModel.isLoadingLD.observe(this, Observer<Boolean> {
            it?.let { showProgress(it) }
        })
        viewModel.errorLD.observe(this, Observer<Any> {
            it?.let { onError(it) }
        })
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

    protected fun showKeyboard(editText: EditText) =
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
            ?.showSoftInput(editText, NO_FLAGS)

    protected fun EditText.addTextWatcher(watcher: TextWatcher) = this.apply {
        textWatchers.plus(this to watcher)
        addTextChangedListener(watcher)
    }

    @SuppressWarnings("SpreadOperator")
    protected fun requestPermission(
        vararg permission: String,
        isDeniedCallback: () -> Unit = { },
        isGrantedCallback: () -> Unit
    ) {
        permissionDisposable?.add(rxPermission?.request(*permission)?.subscribe { granted ->
            if (granted) isGrantedCallback() else isDeniedCallback()
        })
    }

    /**
     * @return whether permission is granted
     */
    protected fun checkPermissions(vararg permissions: String): Boolean = permissions.run {
        forEach {
            if (ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }
}
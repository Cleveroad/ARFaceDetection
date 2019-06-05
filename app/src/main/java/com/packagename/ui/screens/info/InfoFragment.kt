package com.packagename.ui.screens.info

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.cleveroad.bootstrap.kotlin_core.ui.NO_TITLE
import com.packagename.R
import com.packagename.ui.base.BaseFragment
import com.packagename.ui.base.FragmentArgumentDelegate
import kotlinx.android.synthetic.main.fragment_info.*

class InfoFragment : BaseFragment<InfoViewModel>() {
    override val viewModelClass = InfoViewModel::class.java
    override val layoutId = R.layout.fragment_info

    companion object {
        private const val FULLY_LOADED = 100

        fun newInstance(type: TypeInfo) = InfoFragment().apply { this.type = type }
    }

    override fun getScreenTitle() = when (type) {
        TypeInfo.TERMS_OF_USE -> R.string.title_terms_of_use
        TypeInfo.PRIVACY_POLICY -> R.string.title_privacy_policy
        else -> NO_TITLE
    }

    override fun hasToolbar() = true
    override fun getToolbarId() = R.id.toolbar

    override fun observeLiveData(viewModel: InfoViewModel) = Unit

    private val chromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            if (newProgress == FULLY_LOADED) hideProgress() else showProgress()
            super.onProgressChanged(view, newProgress)
        }
    }

    private val viewClient = object : WebViewClient() {

        @SuppressWarnings("deprecation")
        override fun shouldOverrideUrlLoading(view: WebView, url: String) = handleUri(view, url)

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) =
                handleUri(view, request.url.toString())

        private fun handleUri(view: WebView, url: String) =
                run { view.loadUrl(url) }.run { true }
    }

    private var type by FragmentArgumentDelegate<TypeInfo>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
    }

    override fun handleNavigation() {
        with(wvTermsConditions) {
            hideProgress()
            if (canGoBack()) goBack() else super.handleNavigation()
        }
    }

    override fun onBackPressed() =
            with(wvTermsConditions) {
                hideProgress()
                canGoBack().apply { if (this) goBack() }
            }

    override fun onPause() {
        hideProgress()
        super.onPause()
    }

    private fun initWebView() {
        wvTermsConditions.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            webChromeClient = chromeClient
            webViewClient = viewClient
            clearHistory()
            clearFormData()
            clearCache(true)
            //TODO change url
            loadUrl("https://www.google.com/")
        }
    }

}
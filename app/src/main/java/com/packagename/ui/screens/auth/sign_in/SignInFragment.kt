package com.packagename.ui.screens.auth.sign_in

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.cleveroad.bootstrap.kotlin_core.ui.NO_TITLE
import com.cleveroad.bootstrap.kotlin_core.ui.NO_TOOLBAR
import com.cleveroad.bootstrap.kotlin_ext.setClickListeners
import com.packagename.R
import com.packagename.extensions.safeObserve
import com.packagename.extensions.showTextInputError
import com.packagename.extensions.text
import com.packagename.models.User
import com.packagename.ui.base.BaseFragment
import com.packagename.ui.listeners.HideErrorTextWatcher
import com.packagename.utils.bindInterfaceOrThrow
import com.packagename.utils.validation.ValidationField
import com.packagename.utils.validation.ValidationResponseWrapper
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : BaseFragment<SignInVM>(),
        View.OnClickListener {

    companion object {
        fun newInstance() = SignInFragment()
    }

    override val layoutId: Int = R.layout.fragment_sign_in

    override val viewModelClass = SignInVM::class.java

    private var callback: SignInCallback? = null

    private val validationObserver = Observer<ValidationResponseWrapper> {
        when (it.field) {
            ValidationField.EMAIL -> tilSignInEmail.showTextInputError(it.response)
            ValidationField.PASSWORD -> tilSignInPassword.showTextInputError(it.response)
            else -> Unit
        }
    }

    private val authorizationObserver = Observer<User> {
        showSnackBar("Signed in")
    }

    override fun getScreenTitle() = NO_TITLE

    override fun getToolbarId() = NO_TOOLBAR

    override fun hasToolbar() = false

    override fun observeLiveData(viewModel: SignInVM) {
        with(viewModel) {
            validationLD.safeObserve(this@SignInFragment, validationObserver)
            authorizationLD.safeObserve(this@SignInFragment, authorizationObserver)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = bindInterfaceOrThrow<SignInCallback>(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
            super.onViewCreated(view, savedInstanceState).also { setupUi() }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bSignIn -> viewModel.signIn(etSignInEmail.text(), etSignInPassword.text())
            R.id.tvSignUp -> callback?.openSignUp()
        }
    }

    private fun setupUi() {
        setClickListeners(bSignIn, tvSignUp)
        etSignInEmail.addTextWatcher(HideErrorTextWatcher(tilSignInEmail))
        etSignInPassword.addTextWatcher(HideErrorTextWatcher(tilSignInPassword))
    }
}
package com.packagename.ui.screens.auth.sign_up

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.cleveroad.bootstrap.kotlin_core.ui.NO_TITLE
import com.cleveroad.bootstrap.kotlin_core.ui.NO_TOOLBAR
import com.cleveroad.bootstrap.kotlin_ext.setClickListeners
import com.packagename.R
import com.packagename.extensions.addClickableText
import com.packagename.extensions.safeObserve
import com.packagename.extensions.showTextInputError
import com.packagename.extensions.text
import com.packagename.models.User
import com.packagename.ui.base.BaseFragment
import com.packagename.ui.listeners.HideErrorTextWatcher
import com.packagename.ui.screens.info.TypeInfo
import com.packagename.utils.bindInterfaceOrThrow
import com.packagename.utils.validation.ValidationField.*
import com.packagename.utils.validation.ValidationResponseWrapper
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignUpFragment : BaseFragment<SignUpVM>(),
        View.OnClickListener {

    companion object {
        fun newInstance() = SignUpFragment().apply {
            arguments = Bundle()
        }
    }

    override val layoutId = R.layout.fragment_sign_up

    override val viewModelClass = SignUpVM::class.java

    private var callback: SignUpCallback? = null

    private val validationObserver = Observer<ValidationResponseWrapper> {
        when (it.field) {
            FIRST_NAME -> tilSignUpFirstName.showTextInputError(it.response)
            LAST_NAME -> tilSignUpLastName.showTextInputError(it.response)
            EMAIL -> tilSignUpEmail.showTextInputError(it.response)
            PASSWORD -> tilSignUpPassword.showTextInputError(it.response)
            CONFIRM_PASSWORD -> tilSignUpConfPassword.showTextInputError(it.response)
        }
    }

    private val registrationObserver = Observer<User> {
        showSnackBar("Signed up")
    }

    override fun getScreenTitle() = NO_TITLE

    override fun getToolbarId() = NO_TOOLBAR

    override fun hasToolbar() = false

    override fun hasVersions() = true

    override fun observeLiveData(viewModel: SignUpVM) {
        with(viewModel) {
            validationLD.safeObserve(this@SignUpFragment, validationObserver)
            registrationLD.safeObserve(this@SignUpFragment, registrationObserver)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = bindInterfaceOrThrow<SignUpCallback>(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) =
            super.onViewCreated(view, savedInstanceState).also { setupUi() }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvSignIn -> callback?.openSignIn()
            R.id.bSignUp -> viewModel.signUp(etSignUpFirstName.text(),
                    etSignUpLastName.text(),
                    etSignUpEmail.text(),
                    etSignUpPassword.text(),
                    etSignUpConfPassword.text())
        }
    }

    private fun setupUi() {
        setClickListeners(tvSignIn, bSignUp)
        etSignUpEmail.addTextWatcher(HideErrorTextWatcher(tilSignUpEmail))
        etSignUpConfPassword.addTextWatcher(HideErrorTextWatcher(tilSignUpConfPassword))
        etSignUpFirstName.addTextWatcher(HideErrorTextWatcher(tilSignUpFirstName))
        etSignUpLastName.addTextWatcher(HideErrorTextWatcher(tilSignUpLastName))

        val clickableText = listOf(
                TypeInfo.TERMS_OF_USE to getString(R.string.title_terms_of_use),
                TypeInfo.PRIVACY_POLICY to getString(R.string.title_privacy_policy)
        )
        tvTermsOfUses.addClickableText(getString(R.string.i_accept_the), getString(R.string.and), clickableText) { callback?.openInfoScreen(it) }
    }
}

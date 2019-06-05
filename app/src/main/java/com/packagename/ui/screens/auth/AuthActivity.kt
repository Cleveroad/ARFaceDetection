package com.packagename.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.packagename.R
import com.packagename.ui.base.BaseActivity
import com.packagename.ui.screens.auth.sign_in.SignInCallback
import com.packagename.ui.screens.auth.sign_in.SignInFragment
import com.packagename.ui.screens.auth.sign_up.SignUpCallback
import com.packagename.ui.screens.auth.sign_up.SignUpFragment
import com.packagename.ui.screens.info.InfoFragment
import com.packagename.ui.screens.info.TypeInfo

class AuthActivity : BaseActivity<AuthVM>(),
        SignUpCallback,
        SignInCallback {

    companion object {

        fun start(context: Context?) {
            context?.apply ctx@{ startActivity(getIntent(this@ctx)) }
        }

        fun getIntent(context: Context?) = Intent(context, AuthActivity::class.java)
    }

    override val viewModelClass = AuthVM::class.java

    override val containerId = R.id.container

    override val layoutId = R.layout.activity_auth

    override fun hasProgressBar() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openSignUp()
    }

    override fun observeLiveData(viewModel: AuthVM) = Unit

    override fun openSignIn() {
        replaceFragment(SignInFragment.newInstance(), false)
    }

    override fun openSignUp() {
        replaceFragment(SignUpFragment.newInstance(), false)
    }

    override fun openInfoScreen(type: TypeInfo) {
        replaceFragment(InfoFragment.newInstance(type))
    }

}

package com.cleveroad.aropensource.ui.screens.auth.sign_in

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cleveroad.bootstrap.kotlin_validators.ValidatorsFactory
import com.cleveroad.aropensource.models.User
import com.cleveroad.aropensource.providers.ProviderInjector
import com.cleveroad.aropensource.ui.base.BaseVM
import com.cleveroad.aropensource.utils.validation.ValidationField
import com.cleveroad.aropensource.utils.validation.ValidationResponseWrapper
import com.cleveroad.aropensource.utils.validation.ValidationWrapper

class SignInVM(app: Application) : BaseVM(app) {

    private val emailValidator = ValidatorsFactory.getEmailValidator(app)
    private val passwordValidator = ValidatorsFactory.getPasswordValidator(app)

    val authorizationLD = MutableLiveData<User>()

    val validationLD = MutableLiveData<ValidationResponseWrapper>()

    private val accountProvider by lazy { ProviderInjector.getAccountProvider() }

    fun signIn(email: String, password: String) =
            validate(ValidationWrapper(email, password)).apply {
                takeIf { this }
                        ?.let { login(email, password) }
            }

    private fun validate(validationWrapper: ValidationWrapper): Boolean = validationWrapper.run {
        validateEmail(email) and validatePassword(password)
    }

    private fun validateEmail(email: String): Boolean =
            emailValidator.validate(email).run {
                validationLD.value = ValidationResponseWrapper(
                    this,
                    ValidationField.EMAIL
                )
                isValid
            }

    private fun validatePassword(password: String): Boolean =
            passwordValidator.validate(password).run {
                validationLD.value = ValidationResponseWrapper(
                    this,
                    ValidationField.PASSWORD
                )
                isValid
            }

    private fun login(email: String, password: String) {
        accountProvider.login(email, password)
                .doAsync(authorizationLD)
    }
}
package com.packagename.ui.screens.auth.sign_up

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cleveroad.bootstrap.kotlin_validators.MatchPasswordValidator
import com.cleveroad.bootstrap.kotlin_validators.ValidatorsFactory
import com.packagename.models.User
import com.packagename.providers.ProviderInjector
import com.packagename.ui.base.BaseVM
import com.packagename.utils.validation.ValidationField.*
import com.packagename.utils.validation.ValidationResponseWrapper
import com.packagename.utils.validation.ValidationWrapper

class SignUpVM(app: Application) : BaseVM(app) {

    private val firstNameValidator = ValidatorsFactory.getFirstNameValidator(app)
    private val lastNameValidator = ValidatorsFactory.getLastNameValidator(app)
    private val emailValidator = ValidatorsFactory.getEmailValidator(app)
    private val matchPasswordsValidator = ValidatorsFactory.getMatchPasswordValidator(app)

    private val accountProvider by lazy { ProviderInjector.getAccountProvider() }

    val validationLD = MutableLiveData<ValidationResponseWrapper>()
    val registrationLD = MutableLiveData<User>()

    fun signUp(fName: String, lName: String, email: String, password: String, confirmPassword: String) =
            validate(ValidationWrapper(email, password, confirmPassword, fName, lName)).apply {
                takeIf { this }
                        ?.let { registration(fName, lName, email, password, confirmPassword) }
            }

    private fun validate(validationWrapper: ValidationWrapper): Boolean = validationWrapper.run {
        validateFirstName(firstName) and validateLastName(lastName) and
                validateEmail(email) and validateMatchPasswords(password, confirmPassword)
    }

    private fun validateFirstName(fName: String): Boolean =
            firstNameValidator.validate(fName).run {
                validationLD.value = ValidationResponseWrapper(this, FIRST_NAME)
                isValid
            }

    private fun validateLastName(lName: String): Boolean =
            lastNameValidator.validate(lName).run {
                validationLD.value = ValidationResponseWrapper(this, LAST_NAME)
                isValid
            }

    private fun validateEmail(email: String): Boolean =
            emailValidator.validate(email).run {
                validationLD.value = ValidationResponseWrapper(this, EMAIL)
                isValid
            }

    private fun validateMatchPasswords(password: String, confirmPassword: String): Boolean =
            matchPasswordsValidator.validate(password, confirmPassword).run {
                validationLD.value = ValidationResponseWrapper(this,
                        if (invalidFieldNumber == MatchPasswordValidator.PASSWORD_ERROR_FIELD) PASSWORD else CONFIRM_PASSWORD)
                isValid
            }

    private fun registration(fName: String, lName: String, email: String, password: String, confirmPassword: String) {
        accountProvider.register(fName, lName, email, password, confirmPassword)
                .doAsync(registrationLD)
    }
}

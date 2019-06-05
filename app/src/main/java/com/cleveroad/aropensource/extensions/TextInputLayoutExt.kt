package com.cleveroad.aropensource.extensions

import com.cleveroad.bootstrap.kotlin_validators.ValidationResponse
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.showTextInputError(validationResponse: ValidationResponse?) {
    validationResponse?.let {
        isErrorEnabled = !it.isValid
        error = it.errorMessage
    }
}
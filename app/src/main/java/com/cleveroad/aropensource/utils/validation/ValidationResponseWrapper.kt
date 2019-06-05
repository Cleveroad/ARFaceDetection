package com.cleveroad.aropensource.utils.validation

import com.cleveroad.bootstrap.kotlin_validators.ValidationResponse

enum class ValidationField {
    FIRST_NAME,
    LAST_NAME,
    EMAIL,
    PASSWORD,
    CONFIRM_PASSWORD
}

data class ValidationResponseWrapper(val response: ValidationResponse,
                                     val field: ValidationField
)
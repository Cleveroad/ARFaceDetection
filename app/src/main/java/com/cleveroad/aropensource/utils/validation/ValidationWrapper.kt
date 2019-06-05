package com.cleveroad.aropensource.utils.validation

import com.cleveroad.aropensource.utils.EMPTY_STRING

data class ValidationWrapper(val email: String = EMPTY_STRING,
                             val password: String = EMPTY_STRING,
                             val confirmPassword: String = EMPTY_STRING,
                             val firstName: String = EMPTY_STRING,
                             val lastName: String = EMPTY_STRING
)
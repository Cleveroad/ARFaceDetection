package com.cleveroad.aropensource.network.exceptions

data class ValidationError(var code: Int?,
                           var key: String?,
                           var message: String?)

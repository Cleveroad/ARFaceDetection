package com.packagename.network.api.errors

import com.fasterxml.jackson.annotation.JsonProperty
import com.packagename.utils.NOTHING

data class Error(@JsonProperty("code")
                 val code: Int? = NOTHING,
                 @JsonProperty("key")
                 var key: String? = NOTHING,
                 @JsonProperty("message")
                 var message: String? = NOTHING)



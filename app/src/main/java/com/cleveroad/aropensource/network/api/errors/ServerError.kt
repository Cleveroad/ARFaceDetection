package com.cleveroad.aropensource.network.api.errors

import com.fasterxml.jackson.annotation.JsonProperty
import com.cleveroad.aropensource.utils.NOTHING


data class ServerError(@JsonProperty("__v")
                       var v: String? = NOTHING,
                       @JsonProperty("code")
                       val code: Int? = NOTHING,
                       @JsonProperty("message")
                       val message: String? = NOTHING,
                       @JsonProperty("errors")
                       var errors: List<Error>? = NOTHING
)

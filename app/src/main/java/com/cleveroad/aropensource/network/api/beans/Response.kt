package com.cleveroad.aropensource.network.api.beans

import com.fasterxml.jackson.annotation.JsonProperty


data class Response<T>(@JsonProperty("__v")
                       val v: String,
                       @JsonProperty("data")
                       val data: T,
                       @JsonProperty("pagination")
                       val pagination: Pagination?)
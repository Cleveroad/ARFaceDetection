package com.packagename.network.api.requests

import com.fasterxml.jackson.annotation.JsonProperty


data class LoginRequest(@set:JsonProperty("email")
                        var email: String,
                        @set:JsonProperty("password")
                        var password: String,
                        @set:JsonProperty("lifeTime")
                        var lifeTime: Long? = null)

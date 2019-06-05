package com.packagename.network.api.requests

import com.fasterxml.jackson.annotation.JsonProperty


data class RegisterRequest(@set:JsonProperty("email")
                           var email: String,
                           @set:JsonProperty("firstName")
                           var firstName: String,
                           @set:JsonProperty("lastName")
                           var lastName: String,
                           @set:JsonProperty("password")
                           var password: String,
                           @set:JsonProperty("confirmPassword")
                           var confirmPassword: String)
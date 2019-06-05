package com.packagename.network.api.beans

import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime


data class UserBean(@JsonProperty("id")
                    val id: Long?,
                    @JsonProperty("email")
                    val email: String?,
                    @JsonProperty("firstName")
                    val firstName: String?,
                    @JsonProperty("lastName")
                    val lastName: String?,
                    @JsonProperty("avatarUrl")
                    val avatarUrl: String?,
                    @JsonProperty("phone")
                    val phone: String?,
                    @JsonProperty("gender")
                    val gender: Int?,
                    @JsonProperty("dateOfBirth")
                    val dateOfBirth: DateTime?)
package com.packagename.network.api.beans

import com.fasterxml.jackson.annotation.JsonProperty
import org.joda.time.DateTime


data class SessionBean(@JsonProperty("accessToken")
                       val accessToken: String?,
                       @JsonProperty("refreshToken")
                       val refreshToken: String?,
                       @JsonProperty("expiresAt")
                       val expiresAt: DateTime?)
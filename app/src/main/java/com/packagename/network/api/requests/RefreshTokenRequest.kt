package com.packagename.network.api.requests

import com.fasterxml.jackson.annotation.JsonProperty

data class RefreshTokenRequest(@set:JsonProperty("refreshToken")
                               var refreshToken: String?)

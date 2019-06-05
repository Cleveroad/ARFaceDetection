package com.packagename.network.api.retrofit

import com.packagename.network.V1
import com.packagename.network.clients.ServerClient
import com.packagename.network.api.beans.Response
import com.packagename.network.api.beans.SessionBean
import com.packagename.network.api.beans.UserSessionBean
import com.packagename.network.api.requests.LoginRequest
import com.packagename.network.api.requests.RefreshTokenRequest
import com.packagename.network.api.requests.RegisterRequest
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountApi {
    // TODO change links for methods
    @POST("$V1/account")
    fun register(@Body request: RegisterRequest): Single<Response<UserSessionBean>>

    @POST("$V1/account/login")
    fun login(@Body request: LoginRequest): Single<Response<UserSessionBean>>

    @POST("$V1/account/logout")
    fun logout(): Single<Response<Unit>>

    @POST("$V1/account/refreshToken")
    fun refreshToken(@Body request: RefreshTokenRequest): Single<Response<SessionBean>>
}

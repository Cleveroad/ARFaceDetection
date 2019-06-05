package com.packagename.providers

import com.packagename.models.Session
import com.packagename.models.User
import com.packagename.models.UserModel
import com.packagename.network.NetworkModule
import com.packagename.network.api.modules.AccountModule
import com.packagename.network.api.requests.RegisterRequest
import com.packagename.providers.base.BaseOnlineProvider
import io.reactivex.Single


interface AccountProvider {

    fun register(fName: String,
                 lName: String,
                 email: String,
                 password: String,
                 confirmPassword: String): Single<User>

    fun login(email: String, password: String): Single<User>

    fun logout(): Single<Unit>

    fun refreshToken(): Single<Session>
}

object AccountProviderImpl : BaseOnlineProvider<UserModel, AccountModule>(), AccountProvider {

    override fun initNetworkModule() = NetworkModule.client.account

    override fun register(fName: String,
                          lName: String,
                          email: String,
                          password: String,
                          confirmPassword: String): Single<User> =
            networkModule.register(RegisterRequest(email, fName, lName, password, confirmPassword))

    override fun login(email: String, password: String): Single<User> =
            networkModule.login(email, password)

    override fun logout(): Single<Unit> =
            networkModule.logout()

    override fun refreshToken(): Single<Session> =
            networkModule.refreshToken()
}

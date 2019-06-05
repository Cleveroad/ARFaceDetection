package com.cleveroad.aropensource.network.api.modules

import com.cleveroad.aropensource.models.Session
import com.cleveroad.aropensource.models.User
import com.cleveroad.aropensource.network.api.retrofit.AccountApi
import com.cleveroad.aropensource.network.api.beans.UserBean
import com.cleveroad.aropensource.network.api.converters.SessionBeanConverterImpl
import com.cleveroad.aropensource.network.api.converters.UserBeanConverterImpl
import com.cleveroad.aropensource.network.api.converters.UserSessionBeanConverterImpl
import com.cleveroad.aropensource.network.api.requests.LoginRequest
import com.cleveroad.aropensource.network.api.requests.RefreshTokenRequest
import com.cleveroad.aropensource.network.api.requests.RegisterRequest
import com.cleveroad.aropensource.preferences.PreferencesProvider
import com.cleveroad.aropensource.NPApp
import io.reactivex.Single

interface AccountModule {

    fun register(request: RegisterRequest): Single<User>

    fun login(email: String, password: String): Single<User>

    fun logout(): Single<Unit>

    fun refreshToken(): Single<Session>
}

class AccountModuleImpl(api: AccountApi) :
        BaseRxModule<AccountApi, UserBean, User>(api,
            UserBeanConverterImpl()
        ), AccountModule {

    private val userSessionBeanConverter by lazy { UserSessionBeanConverterImpl() }
    private val sessionBeanConverter by lazy { SessionBeanConverterImpl() }

    override fun register(request: RegisterRequest): Single<User> =
            api.register(request)
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { it.data }
                    .map { userSessionBeanConverter.convertInToOut(it) }

    override fun login(email: String, password: String): Single<User> =
            api.login(LoginRequest(email, password))
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { it.data }
                    .map {
                        it.apply {
                            session?.let { bean ->
                                NPApp.instance.run{
                                    setSession(sessionBeanConverter.convertInToOut(bean))
                                    saveSession()
                                }
                            }
                        }
                    }
                    .map { userSessionBeanConverter.convertInToOut(it) }

    override fun logout(): Single<Unit> =
            api.logout()
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { it.data }

    override fun refreshToken(): Single<Session> =
            api.refreshToken(RefreshTokenRequest(PreferencesProvider.refreshToken))
                    .onErrorResumeNext(NetworkErrorUtils.rxParseSingleError())
                    .map { it.data }
                    .map { sessionBeanConverter.convertInToOut(it) }
}

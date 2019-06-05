package com.cleveroad.aropensource.network.clients

import com.cleveroad.bootstrap.kotlin_core.network.ApiException
import com.cleveroad.bootstrap.kotlin_core.utils.ioToMainSingle
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import com.itkacher.okhttpprofiler.OkHttpProfilerInterceptor
import com.cleveroad.BuildConfig
import com.cleveroad.aropensource.NPApp
import com.cleveroad.aropensource.models.Session
import com.cleveroad.aropensource.network.LOCALHOST
import com.cleveroad.aropensource.network.NetworkModule.mapper
import com.cleveroad.aropensource.network.api.modules.AccountModule
import com.cleveroad.aropensource.network.api.modules.AccountModuleImpl
import com.cleveroad.aropensource.network.api.retrofit.AccountApi
import com.cleveroad.aropensource.preferences.PreferencesProvider
import io.reactivex.Single
import io.reactivex.SingleTransformer
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.platform.Platform
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

class ServerClient {

    companion object {

        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val AUTHORIZATION_PREF = "Bearer"

        private const val TIMEOUT_IN_SECONDS = 30L
        private const val MAX_REPEAT_TOKEN_REFRESH_COUNT = 2
        private const val DEFAULT_RESPONSE_COUNT = 1
        private const val UNAUTHORIZED = 401
        private const val BAD_TOKEN = 400

    }

    private val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .baseUrl(createApiEndpoint())
            .client(createHttpClient())
            .build()

    val account: AccountModule by lazy {
        AccountModuleImpl(
            retrofit.create(AccountApi::class.java)
        )
    }


    private fun log() = LoggingInterceptor.Builder()
            .loggable(BuildConfig.DEBUG)
            .setLevel(Level.BASIC)
            .log(Platform.INFO)
            .request("Request>>>>")
            .response("Response<<<<")
            .build()

    private fun responseCount(response: Response): Int {
        var result = DEFAULT_RESPONSE_COUNT
        var lResponse: Response? = response
        while ({ lResponse = lResponse?.priorResponse(); lResponse }() != null) {
            result++
        }
        return result
    }

    private fun createHttpClient(): OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                    requestBuilder.method(original.method(), original.body())
                    return@addInterceptor chain.proceed(addHeader(requestBuilder))
                }.authenticator { _, response ->
                    if (PreferencesProvider.token.isNullOrEmpty()
                            || responseCount(response) >= MAX_REPEAT_TOKEN_REFRESH_COUNT
                    ) {
                        return@authenticator null
                    }
                    val session: Session = account.refreshToken()
                            .compose(unauthorizedHandler())
                            .compose(ioToMainSingle())
                            .blockingGet()
                    with(NPApp.instance) {
                        setSession(session)
                        saveSession()
                    }
                    return@authenticator addHeader(response.request().newBuilder())
                }
        if (BuildConfig.DEBUG) {
            addInterceptor(log())
            addInterceptor(OkHttpProfilerInterceptor())
        }
    }.build()

    private fun <T> unauthorizedHandler() = SingleTransformer<T, T> {
        it.onErrorResumeNext { throwable: Throwable ->
            return@onErrorResumeNext when {
                throwable is ApiException && throwable.statusCode == BAD_TOKEN -> doLogoutSingle()
                throwable !is HttpException || throwable.code() != UNAUTHORIZED -> Single.error<T>(throwable)
                else -> doLogoutSingle()
            }
        }
    }

    private fun addHeader(requestBuilder: Request.Builder): Request {
        requestBuilder.removeHeader(HEADER_AUTHORIZATION)
        if (!PreferencesProvider.token.isNullOrEmpty()) {
            requestBuilder.addHeader(HEADER_AUTHORIZATION, "$AUTHORIZATION_PREF ${PreferencesProvider.token}")
        } else if (!NPApp.instance.getSession().accessToken.isNullOrEmpty()) {
            requestBuilder.addHeader(HEADER_AUTHORIZATION, NPApp.instance.getSession().accessToken as String)
        }
        return requestBuilder.build()
    }

    private fun <T> doLogoutSingle(): Single<T> {
        NPApp.instance.onLogout()
        return Single.error<T>(IllegalAccessException("Wrong credentials"))
    }

    private fun createApiEndpoint() =
            BuildConfig.LOCALHOST_PORT.let { port ->
                takeIf { port < 0 }
                        ?.run { "${BuildConfig.ENDPOINT}/api/" }
                        ?: run { "$LOCALHOST$port/" }
            }
}

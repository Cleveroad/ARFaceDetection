package com.cleveroad.aropensource.network.api.modules


import com.cleveroad.bootstrap.kotlin_core.network.ApiException
import com.cleveroad.bootstrap.kotlin_core.network.ValidationError
import com.cleveroad.aropensource.network.NetworkModule.mapper
import com.cleveroad.aropensource.network.api.errors.ServerError
import com.cleveroad.aropensource.network.exceptions.NoNetworkException
import com.cleveroad.aropensource.network.exceptions.ServerException
import com.cleveroad.aropensource.utils.LOG
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function
import retrofit2.HttpException
import retrofit2.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*

object NetworkErrorUtils {
    private const val SERVER_ERROR_CODE = 500
    private const val SERVER_ERROR_CODE_1 = 502

    private val TAG = NetworkErrorUtils::class.java.simpleName

    fun <T> rxParseFlowableError() = Function<Throwable, Flowable<T>> {
        Flowable.error<T>(parseError(it))
    }

    fun rxParseCompletableError() = Function<Throwable, Completable> {
        Completable.error(parseError(it))
    }

    fun <T> rxParseSingleError() = Function<Throwable, Single<T>> {
        Single.error<T>(parseError(it))
    }

    private fun parseError(throwable: Throwable): Throwable? {
        return if (throwable is HttpException) {
// return this exception in case of error with 500 code
            val code = throwable.code()
            if (code == SERVER_ERROR_CODE || code == SERVER_ERROR_CODE_1) {
                LOG.e(TAG, throwable = throwable)
                return ServerException().initCause(throwable)
            }
            return parseErrorResponseBody(throwable.response())
        } else when {
            isConnectionProblem(throwable) -> NoNetworkException()
            isServerConnectionProblem(throwable) -> ServerException()
            else -> throwable
        }
    }

    private fun isServerConnectionProblem(throwable: Throwable) =
            throwable is SocketException || throwable is SocketTimeoutException

    private fun isConnectionProblem(throwable: Throwable) =
            throwable is UnknownHostException || throwable is ConnectException

    private fun parseErrorResponseBody(response: Response<*>): Exception {
        var inputStreamReader: InputStreamReader? = null
        var bufferedReader: BufferedReader? = null
        try {
            inputStreamReader = InputStreamReader(response.errorBody()?.byteStream())
            bufferedReader = BufferedReader(inputStreamReader)
            val sb = StringBuilder()
            var newLine: String? = null
            while ({ newLine = bufferedReader.readLine(); newLine }() != null) {
                sb.append(newLine)
            }

            // Try to parse ServerError.class
            val serverError: ServerError
            try {
                serverError = mapper.readValue(sb.toString(), ServerError::class.java)
            } catch (e: IOException) {
                LOG.e(TAG, "Couldn't parse error response to ServerError.class: " + e.message)
                return e
            }

            val validationErrors = ArrayList<ValidationError>()
            serverError.errors?.forEach {
                validationErrors.add(ValidationError(it.code, it.key, it.message))
            }

            return ApiException(response.code(),
                    serverError.v,
                    serverError.message,
                    validationErrors)

        } catch (e: IOException) {
            LOG.e(TAG, throwable = e)
            return e
        } finally {
            closeReader(bufferedReader)
            closeReader(inputStreamReader)
        }
    }

    private fun closeReader(reader: Reader?) {
        reader?.let {
            try {
                it.close()
            } catch (e: IOException) {
                LOG.e(TAG, throwable = e)
            }
        }
    }
}

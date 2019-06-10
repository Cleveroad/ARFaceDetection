package com.cleveroad.aropensource.utils

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Helper class for RxJava
 */
fun <T> ioToMain(observable: Observable<T>): Observable<T> = observable
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> ioToMain(single: Single<T>): Single<T> = single
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> ioToMain(flowable: Flowable<T>): Flowable<T> = flowable
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())

fun completToMain(completable: Completable): Completable = completable
    .subscribeOn(Schedulers.computation())
    .observeOn(AndroidSchedulers.mainThread())

fun <T> ioToMainSingle() = SingleTransformer<T, T> {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> ioToMainFlowable() = FlowableTransformer<T, T> { inObservable ->
    inObservable
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

fun <T> ioToMainObservable() = ObservableTransformer<T, T> {
    it
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}

object RxUtils {

    fun <T> Flowable<T>.doAsyncSeparateThread(
        successful: (t: T) -> Unit,
        error: (error: Throwable?) -> Unit
    ): Disposable =
        subscribeOn(Schedulers.io())
            .subscribe({ successful(it) }, { error(it) })

    fun <T> Single<T>.doAsyncSeparateThread(
        successful: (t: T) -> Unit,
        error: (error: Throwable?) -> Unit
    ): Disposable =
        subscribeOn(Schedulers.io())
            .subscribe({ successful(it) }, { error(it) })

    fun <T> Single<T>.doAsyncSeparateThread(
        successful: Consumer<T>,
        error: Consumer<Throwable>
    ): Disposable =
        subscribeOn(Schedulers.io())
            .subscribe(successful, error)

    fun <T> Flowable<T>.doAsync(
        successful: Consumer<T>,
        error: Consumer<Throwable>,
        loading: MediatorLiveData<Boolean>? = null,
        isShowProgress: Boolean = true
    ): Disposable =
        preSubscribe(loading, isShowProgress)
            .subscribe(successful, error)

    fun <T> Flowable<T>.doAsync(
        successful: MutableLiveData<T>,
        error: Consumer<Throwable>,
        loading: MediatorLiveData<Boolean>? = null,
        isShowProgress: Boolean = true
    ): Disposable =
        preSubscribe(loading, isShowProgress)
            .subscribe(Consumer { successful.value = it }, error)

    fun <T> Single<T>.doAsync(
        successful: Consumer<T>,
        error: Consumer<Throwable>,
        loading: MediatorLiveData<Boolean>? = null,
        isShowProgress: Boolean = true
    ): Disposable =
        preSubscribe(loading, isShowProgress)
            .subscribe(successful, error)

    fun <T> Single<T>.doAsync(
        successful: MutableLiveData<T>,
        error: Consumer<Throwable>,
        loading: MediatorLiveData<Boolean>? = null,
        isShowProgress: Boolean = true
    ): Disposable =
        preSubscribe(loading, isShowProgress)
            .subscribe(Consumer { successful.value = it }, error)

    fun <T> Single<T>.doAsync(
        successful: MutableLiveData<T>,
        error: MutableLiveData<Throwable>,
        loading: MediatorLiveData<Boolean>? = null,
        isShowProgress: Boolean = true
    ): Disposable =
        preSubscribe(loading, isShowProgress)
            .subscribe({ successful.value = it }, { error.value = it })

    fun <T> Observable<T>.doAsync(
        successful: Consumer<T>,
        error: Consumer<Throwable>,
        loading: MediatorLiveData<Boolean>? = null,
        isShowProgress: Boolean = true
    ): Disposable =
        preSubscribe(loading, isShowProgress)
            .subscribe(successful, error)

    fun <T> Observable<T>.doAsync(
        successful: MutableLiveData<T>,
        error: Consumer<Throwable> = Consumer { },
        loading: MediatorLiveData<Boolean>? = null,
        isShowProgress: Boolean = true
    ): Disposable =
        preSubscribe(loading, isShowProgress)
            .subscribe(Consumer { successful.value = it }, error)

    private fun <T> Single<T>.preSubscribe(
        loading: MediatorLiveData<Boolean>?,
        isShowProgress: Boolean = true
    ): Single<T> {
        loading?.hideOrShowProgress(isShowProgress)
        return compose(ioToMainSingle()).doOnEvent { _, _ -> loading?.hideProgress() }
    }

    private fun <T> Flowable<T>.preSubscribe(
        loading: MediatorLiveData<Boolean>?,
        isShowProgress: Boolean = true
    ): Flowable<T> {
        loading?.hideOrShowProgress(isShowProgress)
        return compose(ioToMainFlowable()).doOnEach { loading?.hideProgress() }
    }

    private fun <T> Observable<T>.preSubscribe(
        loading: MediatorLiveData<Boolean>?,
        isShowProgress: Boolean = true
    ): Observable<T> {
        loading?.hideOrShowProgress(isShowProgress)
        return compose(ioToMainObservable()).doOnEach { loading?.hideProgress() }
    }

    private fun MediatorLiveData<Boolean>.hideProgress() {
        value = false
    }

    private fun MediatorLiveData<Boolean>.hideOrShowProgress(hideOrShowFlag: Boolean) {
        value = hideOrShowFlag
    }
}
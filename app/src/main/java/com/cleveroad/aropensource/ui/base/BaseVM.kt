package com.cleveroad.aropensource.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.cleveroad.aropensource.utils.RxUtils.doAsync
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer

abstract class BaseVM(application: Application) : AndroidViewModel(application) {

    private var compositeDisposable: CompositeDisposable? = null

    val errorLD = MutableLiveData<Any>()

    val isLoadingLD = MediatorLiveData<Boolean>()

    protected val onErrorConsumer = Consumer<Throwable> {
        errorLD.value = it.message
    }

    override fun onCleared() {
        clearSubscription()
        super.onCleared()
    }

    @Suppress("unused")
    protected fun hideProgress() {
        isLoadingLD.value = false
    }

    @Suppress("unused")
    protected fun showProgress() {
        isLoadingLD.value = true
    }

    @Suppress("unused")
    protected fun hideOrShowProgress(hideOrShowFlag: Boolean) {
        isLoadingLD.value = hideOrShowFlag
    }

    private fun clearSubscription() {
        compositeDisposable?.apply {
            dispose()
            compositeDisposable = null
        }
    }

    private fun addBackgroundSubscription(subscription: Disposable) {
        compositeDisposable?.apply {
            add(subscription)
        } ?: let {
            compositeDisposable = CompositeDisposable()
            compositeDisposable?.add(subscription)
        }
    }

    protected fun Disposable.addSubscription() = addBackgroundSubscription(this)

    protected fun <T> Flowable<T>.doAsync(
        successful: Consumer<T>,
        error: Consumer<Throwable> = onErrorConsumer,
        isShowProgress: Boolean = true
    ) {
        doAsync(successful, error, isLoadingLD, isShowProgress)
            .addSubscription()
    }

    protected fun <T> Flowable<T>.doAsync(
        successful: MutableLiveData<T>,
        error: Consumer<Throwable> = onErrorConsumer,
        isShowProgress: Boolean = true
    ) {
        doAsync(successful, error, isLoadingLD, isShowProgress)
            .addSubscription()
    }

    protected fun <T> Single<T>.doAsync(
        successful: Consumer<T>,
        error: Consumer<Throwable> = onErrorConsumer,
        isShowProgress: Boolean = true
    ) {
        doAsync(successful, error, isLoadingLD, isShowProgress)
            .addSubscription()
    }

    protected fun <T> Single<T>.doAsync(
        successful: MutableLiveData<T>,
        error: Consumer<Throwable> = onErrorConsumer,
        isShowProgress: Boolean = true
    ) {
        doAsync(successful, error, isLoadingLD, isShowProgress)
            .addSubscription()
    }

    protected fun <T> Observable<T>.doAsync(
        successful: Consumer<T>,
        error: Consumer<Throwable> = onErrorConsumer,
        isShowProgress: Boolean = true
    ) {
        doAsync(successful, error, isLoadingLD, isShowProgress)
            .addSubscription()
    }

    protected fun <T> Observable<T>.doAsync(
        successful: MutableLiveData<T>,
        error: Consumer<Throwable> = onErrorConsumer,
        isShowProgress: Boolean = true
    ) {
        doAsync(successful, error, isLoadingLD, isShowProgress)
            .addSubscription()
    }
}
package com.cleveroad.aropensource.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val innerObserver = Observer<T> {
        value = it
        latch.countDown()
    }
    observeForever(innerObserver)
    latch.await(10, TimeUnit.SECONDS)
    return value
}

fun <T> LiveData<T>.safeObserve(owner: LifecycleOwner, observer: Observer<T>) {
    this.observe(owner, observer)
}

fun <T> LiveData<T>.safeObserveLet(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer {
        it?.let(observer)
    })
}

fun <T> LiveData<T>.safeObserveRun(owner: LifecycleOwner, observer: T.() -> Unit) {
    this.observe(owner, Observer {
        it?.observer()
    })
}

fun <T> MutableLiveData<T>.safeSingleObserve(owner: LifecycleOwner, observer: Observer<T>) {
    this.observe(owner, observer)
    this.value = null
}

fun <T> MutableLiveData<T>.safeSingleObserveLet(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer { data ->
        data?.let {
            observer(it)
            this.value = null
        }
    })
}

fun <T> MutableLiveData<T>.safeSingleObservRun(owner: LifecycleOwner, observer: T.() -> Unit) {
    this.observe(owner, Observer { data ->
        data?.let {
            it.observer()
            this.value = null
        }
    })
}
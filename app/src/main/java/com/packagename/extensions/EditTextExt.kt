package com.packagename.extensions

import android.widget.EditText
import com.packagename.utils.simple.SimpleTextWatcher
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun EditText.text() = text.toString()


/**
 * Wrap TextChangedListener with Flowable
 * For example, when you need to implement search view
 * Flowable will emit new characters
 */
fun EditText.getTextChangedFlowable() =
        Flowable.create({ emitter: FlowableEmitter<String> ->
            var watcher: SimpleTextWatcher?
            addTextChangedListener(object : SimpleTextWatcher() {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    super.onTextChanged(s, start, before, count)
                    s?.let { emitter.onNext(it.toString()) }
                }
            }.apply { watcher = this })
            emitter.setDisposable(object : Disposable {
                override fun isDisposed() = true

                override fun dispose() {
                    watcher?.let { removeTextChangedListener(it) }
                    watcher = null
                }
            })
        }, BackpressureStrategy.BUFFER)
                .observeOn(Schedulers.io())
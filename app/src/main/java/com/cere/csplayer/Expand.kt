package com.cere.csplayer

import android.view.View
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * Created by CheRevir on 2020/9/15
 */
@JvmOverloads
fun View.setOnShakeProofClickListener(
    windowDuration: Long = 1000,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    listener: (view: View) -> Unit
): Disposable {
    return Observable.create(ObservableOnSubscribe<View> { emitter ->
        setOnClickListener {
            if (!emitter.isDisposed) {
                emitter.onNext(it)
            }
        }
    }).throttleFirst(windowDuration, unit)
        .subscribe { listener(it) }
}
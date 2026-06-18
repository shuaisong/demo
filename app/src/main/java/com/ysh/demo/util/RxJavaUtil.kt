package com.ysh.demo.util

import android.annotation.SuppressLint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

object RxJavaUtil {
    @SuppressLint("CheckResult")
    @JvmStatic
    fun asyncNoResult(block: () -> Any, error: (e: Throwable) -> Unit) {
        Observable.create { emitter ->
            try {
                val any = block()
                emitter.onNext(any)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.subscribeOn(Schedulers.newThread()).subscribe({}, { error(it) })
    }


    @SuppressLint("CheckResult")
    @JvmStatic
    fun <R : Any> async(block: () -> R, success: (R) -> Unit, error: (e: Throwable) -> Unit) {
        Observable.create { emitter ->
            try {
                val r = block()
                emitter.onNext(r)
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(
            { success(it) }, { error(it) })
    }
}
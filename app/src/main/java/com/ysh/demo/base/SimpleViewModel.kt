package com.ysh.demo.base

import android.content.Intent
import com.blankj.utilcode.util.ToastUtils
import com.ysh.demo.MainApplication
import com.ysh.demo.ui.activity.SplashActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import me.hgj.jetpackmvvm.callback.livedata.event.EventLiveData
import me.hgj.jetpackmvvm.network.AppException
import me.hgj.jetpackmvvm.network.BaseResponse
import me.hgj.jetpackmvvm.network.ExceptionHandle

open class SimpleViewModel : BaseViewModel() {
    val msgEvent by lazy { EventLiveData<Message>() }

    private var handleTokenTime = 0L
    protected fun handleTokenValid() {
        if (System.currentTimeMillis() - handleTokenTime < 1000) {
            return
        }
        handleTokenTime = System.currentTimeMillis()
        val intent = Intent(MainApplication.instance, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        MainApplication.instance.startActivity(intent)
    }


    /**
     * 过滤请求结果，其他全抛异常
     * @param block 请求体
     * @param success 成功回调
     * @param error 失败回调
     * @param complete  完成回调（无论成功失败都会调用）
     * @param isShowDialog 是否显示加载框
     */
    fun <T> rxOnlyResult(block: () -> Observable<BaseResponse<T>>, success: (T) -> Unit,
            error: (AppException) -> Unit = {
                ToastUtils.showShort("${it.errCode}:${it.errorMsg}")
            }, complete: () -> Unit = {}, isShowDialog: Boolean = true) {
        if (isShowDialog) UiLoadingChange().dismissDialog.postValue(true)
        block().subscribeOn(Schedulers.newThread()).unsubscribeOn(Schedulers.newThread()).observeOn(
            AndroidSchedulers.mainThread()).subscribe(object : Observer<BaseResponse<T>> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                val handleException = ExceptionHandle.handleException(e)
                if (handleException.errCode == Constant.TOKEN_VALID) {
                    ToastUtils.showShort("${handleException.errCode}:${handleException.errorMsg}")
                    handleTokenValid()
                } else {
                    error(handleException)
                }
            }

            override fun onComplete() {
                if (isShowDialog) {
                    UiLoadingChange().dismissDialog.postValue(true)
                }
                complete()
            }

            override fun onNext(t: BaseResponse<T>) {
                if (!t.isSucces()) {
                    if (t.getResponseCode() == Constant.TOKEN_VALID) {
                        ToastUtils.showShort("${t.getResponseCode()}:${t.getResponseMsg()}")
                        handleTokenValid()
                    } else {
                        error(AppException(t.getResponseCode(), t.getResponseMsg()))
                    }
                } else {
                    success(t.getResponseData())
                }
            }

        })

    }


    /**
     * 异常统一处理
     */
    private suspend fun handleException(block: suspend CoroutineScope.() -> Unit,
            error: suspend CoroutineScope.(AppException) -> Unit,
            complete: suspend CoroutineScope.() -> Unit) {
        coroutineScope {
            try {
                block()
            } catch (e: Throwable) {
                val handleException = ExceptionHandle.handleException(e)
                if (handleException.errCode == Constant.TOKEN_VALID) {
                    ToastUtils.showShort("${handleException.errCode}:${handleException.errorMsg}")
                    handleTokenValid()
                } else {
                    error(handleException)
                }
            } finally {
                complete()
            }
        }
    }
}
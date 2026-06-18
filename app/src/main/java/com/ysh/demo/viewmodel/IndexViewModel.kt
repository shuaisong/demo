package com.ysh.demo.viewmodel

import android.util.Base64
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.ysh.demo.base.ApiHelper
import com.ysh.demo.base.Constant
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleViewModel
import com.ysh.demo.bean.UserInfoBean
import com.ysh.demo.extend.toast
import com.ysh.demo.util.CacheUtil
import com.ysh.demo.util.RSAUtil
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import me.hgj.jetpackmvvm.ext.request
import me.hgj.jetpackmvvm.network.AppException
import me.hgj.jetpackmvvm.network.ExceptionHandle
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class IndexViewModel : SimpleViewModel() {

    //用户信息
    val userInfo = MutableLiveData<UserInfoBean>()

    fun login(username: String, psd: String, showLoading: Boolean) {
        if (showLoading) {
            loadingChange.showDialog.postValue("正在登录中...")
        }
        var userInfoBean: UserInfoBean? = null
        val api = ApiHelper.service
        api.getRsaKey().subscribeOn(Schedulers.io()).flatMap { response ->
            if (!response.isSucces()) {
                throw AppException(response.getResponseCode(), response.getResponseMsg())
            }
            val data = JSONObject()
            data.put("username", username)
            data.put("password", psd)
            val jsonObject = JSONObject()
            jsonObject.put("loginType", "1")
            jsonObject.put("encryptedWords", Base64.encodeToString(
                RSAUtil.encryptByPublicKey(data.toString(), response.getResponseData()),
                Base64.DEFAULT))
            api.appLogin(jsonObject.toString().toRequestBody())
        }.flatMap { response ->
            if (!response.isSucces()) {
                throw AppException(response.getResponseCode(), response.getResponseMsg())
            }
            CacheUtil.setToken(response.getResponseData().token)
            CacheUtil.setPsd(psd)
            CacheUtil.setAccount(username)
            api.appInfo()
        }.map { response ->
            if (!response.isSucces()) {
                throw AppException(response.getResponseCode(), response.getResponseMsg())
            }
            userInfoBean = response.getResponseData()
        }.observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<Any> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onError(e: Throwable) {
                val handleException = if (e is AppException) {
                    e
                } else {
                    ExceptionHandle.handleException(e)
                }
                LogUtils.e(handleException.errorMsg)
                handleException.errorMsg.toast()
                if (showLoading) {
                    loadingChange.dismissDialog.postValue(true)
                }
                msgEvent.postValue(Message(Constant.LOG_OUT))
            }

            override fun onComplete() {
            }

            override fun onNext(t: Any) {
                userInfo.postValue(userInfoBean!!)
                if (showLoading) {
                    loadingChange.dismissDialog.postValue(null)
                }
            }

        })

    }


    fun logOut() {
        request({
            ApiHelper.service.logout()
        }, error = {
            it.errorMsg.toast()
        }, success = {
            msgEvent.postValue(Message(Constant.LOG_OUT))
        }, isShowDialog = true)

    }
}

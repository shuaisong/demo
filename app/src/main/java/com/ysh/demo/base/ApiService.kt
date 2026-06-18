package com.ysh.demo.base

import com.ysh.demo.bean.CheckVersionResult
import com.ysh.demo.bean.LoginBean
import com.ysh.demo.bean.UserInfoBean
import io.reactivex.rxjava3.core.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {
    //    获取RSA加密公钥
    @GET("auth/rsa-key")
    fun getRsaKey(): Observable<BaseResponseImp<String>>

    //    APP登录
    @POST("appLogin")
    @Headers("Content-Type: application/json")
    fun appLogin(@Body body: RequestBody): Observable<BaseResponseImp<LoginBean>>

    //检查新版本
    @GET("project/app/version/getNewVersion")
    suspend fun checkVersion(@Query("appType") appType: Int = 3): BaseResponseImp<CheckVersionResult?>


    @POST("logout")
    @Headers("Content-Type: application/json")
    suspend fun logout(): BaseResponseImp<Any>

    //    APP请求获取用户信息
    @GET("sys/user/appInfo")
    fun appInfo(): Observable<BaseResponseImp<UserInfoBean>>
}

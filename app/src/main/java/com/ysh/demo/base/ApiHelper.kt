package com.ysh.demo.base

import com.ysh.demo.util.CacheUtil
import me.hgj.jetpackmvvm.network.BaseNetworkApi
import me.hgj.jetpackmvvm.network.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiHelper : BaseNetworkApi() {

    fun getRequestBody(bodyStr: String): RequestBody {
        return bodyStr.toRequestBody("application/json; charset=utf-8".toMediaType())
    }

    companion object {
        private const val DEV = 0
        private const val DEBUG = 1
        private const val RELEASE = 2

        private var type: Int = DEBUG
        private var ip = when (type) {
            DEV -> "http://117.159.13.209:7527"
            DEBUG -> "http://117.159.13.210:8115"
            RELEASE -> "http://117.159.13.210:26502"
            else -> "http://117.159.13.209:7527"
        }

        val mqttIp = when (type) {
            DEV -> "tcp://117.159.13.209:7521"
            DEBUG -> "tcp://117.159.13.210:1883"
            RELEASE -> "tcp://117.159.13.210:26505"
            else -> "tcp://117.159.13.209:7521"
        }

        val minio = when (type) {
            DEV -> "http://117.159.13.209:7525"
            DEBUG -> "http://117.159.13.210:9001"
            RELEASE -> "http://117.159.13.210:26500"
            else -> "http://117.159.13.209:7525"
        }


        //        const val bucketName = "patrol-test"
        const val bucketName = "over-the-horizon"
        val fileAccess = if (type == DEV || type == DEBUG) {
            "minioadmin"
        } else {
            "minioxincheng"
        }
        val fileSecret = if (type == DEV || type == DEBUG) {
            "minioadmin"
        } else {
            "minioxincheng"
        }

        private var BASE_HORIZON_URL = "$ip/saas-admin/"

        private var apiHelper: ApiHelper? = null

        @JvmStatic val instance: ApiHelper
            get() {
                if (apiHelper == null) {
                    synchronized(ApiHelper::class.java) {
                        if (apiHelper == null) {
                            apiHelper = ApiHelper()
                        }
                    }
                }
                return apiHelper!!
            }


        //双重校验锁式-单例 封装NetApiService 方便直接快速调用
        val service: ApiService by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            instance.getApi(ApiService::class.java, BASE_HORIZON_URL)
        }
    }

    override fun setHttpClientBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return builder.addInterceptor(httpLoggingInterceptor).addInterceptor(Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            if (!CacheUtil.getToken().isNullOrEmpty()) {
                val newBuilder = request.newBuilder()
                val userInfo = CacheUtil.getUserInfo()
                if (userInfo != null) {
                    newBuilder.addHeader("X-Tenant-Id", userInfo.tenantId)
                }
                request = newBuilder.addHeader("token", CacheUtil.getToken()!!).build()
            }
            chain.proceed(request)
        }).connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时
            .readTimeout(5, TimeUnit.SECONDS) // 设置读取超时
            .writeTimeout(5, TimeUnit.SECONDS) // 设置写入超时
            // 默认重试5次取消http请求
            .retryOnConnectionFailure(true) // 在网络请求失败时重试
    }

    override fun setRetrofitBuilder(builder: Retrofit.Builder): Retrofit.Builder {
        return builder.baseUrl(BASE_HORIZON_URL).addCallAdapterFactory(CoroutineCallAdapterFactory()).addCallAdapterFactory(
            RxJava3CallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create())
    }
}
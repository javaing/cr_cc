package com.aliee.quei.mo.net.retrofit

import android.content.Context
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.component.GsonProvider
import com.aliee.quei.mo.net.ApiConstants
import com.aliee.quei.mo.utils.FileUtils
import com.aliee.quei.mo.utils.SharedPreUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @Author: YangYang
 * @Date: 2017/12/26
 * @Version: 1.0.0
 * @Description:
 */
object RetrofitClient {

    //http连接最大时长
    private val HTTP_CONNECT_TIMEOUT = 20000L

    //http读取最大时长
    private val HTTP_READ_TIMEOUT = 20000L

    //http写出最大时长
    private val HTTP_WRITE_TIMEOUT = 20000L

    /**
     * 获取读取手机信息权限后重新初始化请求头信息
     */
    fun initBaseParams(context: Context) {
        HeaderInterceptor.initBaseParam(context)
    }

    //OKHttp的相关配置,未配置log等
    private fun getClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (builder.interceptors() != null) {
            builder.interceptors().clear()
        }
        builder.addInterceptor(BaseParamsInterceptor())
//        builder.addInterceptor(CacheInterceptor())
        builder.addInterceptor(HeaderInterceptor())
//        builder.addInterceptor(ErrorReportInterceptor())
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        val cacheDir = File(FileUtils.getCachePath(), "response")
        val cache = Cache(cacheDir, 1024 * 1024 * 100)
      //  builder.cache(cache)
        builder.connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
        return builder.build()
    }

    //获取Retrofit的实力
    private fun retrofit(): Retrofit {
//        val gson = GsonBuilder()
//            .registerTypeAdapter(List<T>,ListDefaultAdapter<T>)
        var domain = ApiConstants.API_HOST
        if (domain.isNullOrEmpty()) {
            domain = SharedPreUtils.getInstance().getString("apiDomain")
        }
        if (domain.isNullOrEmpty()) {
            domain = "http://api.7775367.com/"
        }
        return Retrofit.Builder()
                .baseUrl(domain)
                .client(getClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonProvider.gson))
                .build()
    }

    //获取接口的Service
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit().create(serviceClass)
    }

}
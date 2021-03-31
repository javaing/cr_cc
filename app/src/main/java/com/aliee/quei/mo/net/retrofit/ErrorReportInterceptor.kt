package com.aliee.quei.mo.net.retrofit

import com.aliee.quei.mo.application.ReaderApplication
import com.umeng.analytics.MobclickAgent
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException
import java.io.IOException

class ErrorReportInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url
        var params = ""
        val body = chain.request().body
        try {
            if (body is FormBody) {
//            body.toString()
//            body.cont
                val size = body.size
                for (i in 0 until size) {
                    params += body.name(i) + "=" + body.value(i) + "&"
                }
            }
        } catch (e : Exception) {
        }
        try {
            return chain.proceed(chain.request())
        } catch (e : IOException) {
            if (e is HttpException) {
                MobclickAgent.onEvent(ReaderApplication.instance,"error_server","message = ${e.message()},requestUrl = $url,params = $params")
            }
            throw e
        }
    }
}
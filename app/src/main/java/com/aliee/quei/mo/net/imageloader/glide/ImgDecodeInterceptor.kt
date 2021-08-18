package com.aliee.quei.mo.net.imageloader.glide

import android.util.Base64
import android.util.Log
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.utils.AES
import com.dueeeke.videoplayer.util.PlayerUtils.getApplication
import com.elvishew.xlog.XLog
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * glide 的图片解密okHttp拦截器
 */
class ImgDecodeInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalResponse = chain.proceed(chain.request())
        val body = originalResponse.body ?: return originalResponse
        val decData: ByteArray
        try {
            val imageByte = body.bytes()
            var imageStr = String(imageByte)
            if (imageStr.contains("data:image/jpeg") || imageStr.contains("data:image/gif")) {
                imageStr = imageStr.replace("\\+".toRegex(), "*")
                    .replace("/".toRegex(), "+")
                    .replace("\\*".toRegex(), "\\/")
                decData = Base64.decode(imageStr.split(",")[1], Base64.DEFAULT)
                Log.e("ImgDecodeInterceptor", "BASE64")
            } else {
                decData = ReaderApplication.instance.aes.decrypt(imageByte)
                Log.e("ImgDecodeInterceptor", "AES")
            }
        } catch (e: Exception) {
            Log.e("ImgDecodeInterceptor", e.message)
            return originalResponse
        }
        if (decData == null || decData.isEmpty()) return originalResponse
        val newBody = decData.toResponseBody((originalResponse.headers.get("Content-Type")
                ?: "image/png").toMediaTypeOrNull())
        return Response.Builder()
                .code(200)
                .request(originalRequest)
                .protocol(Protocol.HTTP_1_1)
                .message("OK")
                .body(newBody)
                .build()
    }

}
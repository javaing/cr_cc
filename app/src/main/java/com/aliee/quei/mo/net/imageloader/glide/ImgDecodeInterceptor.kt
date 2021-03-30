package com.aliee.quei.mo.net.imageloader.glide

import android.util.Base64
import android.util.Log
import com.aliee.quei.mo.utils.AES
import okhttp3.*

/**
 * glide 的图片解密okHttp拦截器
 */
class ImgDecodeInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request();
        val originalResponse = chain.proceed(chain.request())
        val body = originalResponse.body()
        if (body == null) return originalResponse;
        var enc: ByteArray? = null
        val imageStr = body.bytes()
        val imageData = String(imageStr)
        if (imageData.contains("data:image/jpeg") || imageData.contains("data:image/gif")) {
            val imageStr: String = imageData.replace("\\+".toRegex(), "*")
            val imageStr1 = imageStr.replace("\\/".toRegex(), "+")
            val imageStr2 = imageStr1.replace("\\*".toRegex(), "\\/")
            enc = Base64.decode(imageStr2.split(",")[1], Base64.DEFAULT)
        } else {
            val password = "0123456789abcdef"
            val aes = AES()
            enc = aes.decrypt(imageStr, password.toByteArray())
        }
        if (enc == null || enc.isEmpty()) return originalResponse
        val newBody = ResponseBody.create(MediaType.parse(originalResponse.headers().get("Content-Type")
                ?: "image/png"), enc)
        return Response.Builder()
                .code(200)
                .request(originalRequest)
                .protocol(Protocol.HTTP_1_1)
                .message("OK")
                .body(newBody)
                .build()
    }
}
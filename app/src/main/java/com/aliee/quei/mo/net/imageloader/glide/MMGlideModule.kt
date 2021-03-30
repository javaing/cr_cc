package com.aliee.quei.mo.net.imageloader.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream


/**
 * @Author: YangYang
 * @Date: 2017/12/25
 * @Version: 1.0.0
 * @Description:
 */
@GlideModule
class MMGlideModule : AppGlideModule() {
    companion object {
        // 缓存池大小
        @JvmField
        val MAX_CACHE_SIZE = 1024 * 1024 * 512 // 512M

        // 缓存目录
        @JvmField
        val CACHE_FILE_NAME = "MM_IMG_CACHE" // cache file dir name
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)

        // 36MB, memory cache size
        // default value: 24MB
        val memoryCacheSize = 1024 * 1024 * 36L
        builder.setMemoryCache(LruResourceCache(memoryCacheSize))
        // Internal cache
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, CACHE_FILE_NAME, MAX_CACHE_SIZE.toLong()))
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        // Replace the http connection with okhttp
        //使用okhttp作为glide的urlLoader ,并添加图片解密拦截器
        val client = OkHttpClient.Builder()
                .addInterceptor(ImgDecodeInterceptor()) //添加图片解密拦截器
                .build()
        val factory = OkHttpUrlLoader.Factory(client)
        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)

        //  registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory())
    }

    /**
     * Disable the parsing of manifest file.
     */
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
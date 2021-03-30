package com.aliee.quei.mo.cache

/**
 * @Author: YangYang
 * @Date: 2017/12/27
 * @Version: 1.0.0
 * @Description:
 */
interface CacheManager {
    fun saveCache(key: String, value: String)

    fun getValueByKey(key: String): String
    /**
     * 根据key和缓存有效期获取缓存值
     * @param key
     * @param cacheTime 缓存有效期
     */
    fun getValueByKeyAndValidity(key: String, cacheTime: Long): String
}
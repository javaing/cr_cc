package com.aliee.quei.mo.cache

import com.aliee.quei.mo.utils.SharedPreUtils

class SPCacheManager : CacheManager{
    override fun saveCache(key: String, value: String) {
        SharedPreUtils.getInstance().putString(key,value)
    }

    override fun getValueByKey(key: String): String {
        return SharedPreUtils.getInstance().getString(key)
    }

    override fun getValueByKeyAndValidity(key: String, cacheTime: Long): String {
        return SharedPreUtils.getInstance().getString(key)
    }
}
package com.aliee.quei.mo.cache

import com.aliee.quei.mo.database.DatabaseProvider
import com.aliee.quei.mo.database.bean.CacheBean
import io.realm.Realm
import io.realm.RealmResults

/**
 * @Author: YangYang
 * @Date: 2017/12/27
 * @Version: 1.0.0
 * @Description:
 */
class DBCacheManager : CacheManager {
    override fun saveCache(key: String, value: String) {


        val realm: Realm = DatabaseProvider.getRealm()
        try {
            realm.executeTransaction {
                val bean = CacheBean()
                bean.key = key
                bean.value = value
                bean.time = System.currentTimeMillis()
                realm.copyToRealmOrUpdate(bean)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun getValueByKey(key: String): String {
        val realm: Realm = DatabaseProvider.getRealm()
        try {
            val results: RealmResults<CacheBean> = realm.where(CacheBean::class.java)
                    .equalTo("key", key)
                    .findAll()
            var value = ""
            if (results.isEmpty() || results[0]!!.value == null)
                return value
            value = results[0]!!.value!!
            return value
        } catch (e : Exception){
            e.printStackTrace()
            return ""
        }
    }

    override fun getValueByKeyAndValidity(key: String, cacheTime: Long): String {
        val realm: Realm = DatabaseProvider.getRealm()
        try {
            val results: RealmResults<CacheBean> = realm.where(CacheBean::class.java)
                    .equalTo("key", key)
                    .greaterThan("cdt", System.currentTimeMillis() - cacheTime)
                    .findAll()
            var value = ""
            if (results.isEmpty() || results[0]!!.value == null)
                return value
            value = results[0]!!.value!!
            return value
        } catch (e : Exception){
            return ""
        } finally {
            realm.close()
        }
    }
}
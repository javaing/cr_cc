package com.aliee.quei.mo.database.bean

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * @Author: YangYang
 * @Date: 2017/12/26
 * @Version: 1.0.0
 * @Description:
 */
open class CacheBean : RealmObject() {
    @PrimaryKey
    var key: String? = null
    var value: String? = null
    var time: Long? = null
}
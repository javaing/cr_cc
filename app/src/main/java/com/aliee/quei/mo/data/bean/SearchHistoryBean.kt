package com.aliee.quei.mo.data.bean

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SearchHistoryBean : RealmObject() {
    var time : Long = System.currentTimeMillis()
    @PrimaryKey
    var keyword : String = ""
}
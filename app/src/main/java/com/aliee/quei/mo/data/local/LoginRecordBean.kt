package com.aliee.quei.mo.data.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class LoginRecordBean() : RealmObject() {
    @PrimaryKey
    var date : String = ""

    override fun toString(): String {
        return "date = ${date}"
    }
}
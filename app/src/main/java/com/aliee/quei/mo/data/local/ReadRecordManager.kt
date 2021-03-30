package com.aliee.quei.mo.data.local

import com.aliee.quei.mo.database.DatabaseProvider

object ReadRecordManager {
    fun saveReadRecord(bean : ReadRecordBean) {
        val realm = DatabaseProvider.getRealm()
        realm.use { r ->
            r.executeTransaction {
                it.insertOrUpdate(bean)
            }
        }
    }

    fun getReadRecord (bookId : Int) : ReadRecordBean? {
        val realm = DatabaseProvider.getRealm()
        realm.use { r ->
            val bean = r.where(ReadRecordBean::class.java).equalTo("bookId",bookId).findFirst()
            bean?:return null
            return r.copyFromRealm(bean)
        }
    }
}
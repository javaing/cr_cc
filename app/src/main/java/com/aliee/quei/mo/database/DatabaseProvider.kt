package com.aliee.quei.mo.database

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * @Author: YangYang
 * @Date: 2017/12/26
 * @Version: 1.0.0
 * @Description:
 */
object DatabaseProvider {
    private val realmConfig: RealmConfiguration = RealmConfiguration.Builder()
            .name(MMDatabase.DATABASE_NAME) //文件名
            .schemaVersion(MMDatabase.DATABASE_VERSION) //版本号
            .migration(DBMigration())
            .allowWritesOnUiThread(true)
            .build()

    fun getRealm(): Realm {
        return Realm.getInstance(realmConfig)
    }
}
package com.aliee.quei.mo.database

import com.aliee.quei.mo.data.bean.ShelfBean

/**
 * Created by Administrator on 2018/4/27 0027.
 */
object ShelfDBManager{
    fun isInShelf(bookId : Int) : Boolean{
        val realm = DatabaseProvider.getRealm()
        try {
            val bean = realm.where(ShelfBean::class.java).equalTo("bookid",bookId).findFirst()
            if(bean != null)return true
        } catch (e : Exception){
            return false
        } finally {
            realm.close()
        }
        return false
    }

    fun saveAll(list : List<ShelfBean>){
        val realm = DatabaseProvider.getRealm()
        try {
            realm.executeTransaction {
                realm.delete(ShelfBean::class.java)
                it.copyToRealmOrUpdate(list)
            }
        } finally {
            realm.close()
        }
    }
}
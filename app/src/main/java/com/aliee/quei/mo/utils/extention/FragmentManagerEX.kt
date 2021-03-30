package com.aliee.quei.mo.utils.extention

/**
 * @Author: YangYang
 * @Date: 2018/1/4
 * @Version: 1.0.0
 * @Description:
 */
inline fun android.support.v4.app.FragmentManager.inTransaction(func: android.support.v4.app.FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commitNow()
    executePendingTransactions()
}

inline fun android.app.FragmentManager.inTransaction(func: android.app.FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
    executePendingTransactions()
}



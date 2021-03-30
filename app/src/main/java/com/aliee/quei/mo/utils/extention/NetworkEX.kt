package com.aliee.quei.mo.utils.extention

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */
fun Context.isNetworkAvailable(): Boolean {
    val state = getConnectivityManager().activeNetworkInfo?.state
    return (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING)
}

fun Context.getNetworkType(): Int = getConnectivityManager().activeNetworkInfo.type

fun Context.getNetworkTypeAsString(): String {
    when(getConnectivityManager().activeNetworkInfo.type) {
        ConnectivityManager.TYPE_WIFI -> return "WiFi"
        ConnectivityManager.TYPE_MOBILE -> return "Mobile"
        else -> return ""
    }
}
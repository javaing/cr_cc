package com.aliee.quei.mo.utils.extention

import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:Context类的服务获取扩展类
 */

fun Context.getClipboardManager() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

fun Context.getConnectivityManager() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Context.getInputMethodManager() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
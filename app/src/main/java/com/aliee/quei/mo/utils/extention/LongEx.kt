package com.aliee.quei.mo.utils.extention

import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: YangYang
 * @Date: 2018/1/24
 * @Version: 1.0.0
 * @Description:
 */

fun Long.toNormalDateString():String {
    val date = Date(this)
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return sdf.format(date)
}

fun Long.toYMDDateString():String {
    val date = Date(this)
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(date)
}

/**
 * 转换为只有时分的格式
 */
fun Long.toHmDateString():String {
    val date = Date(this)
    val sdf = SimpleDateFormat("HH:mm")
    return sdf.format(date)
}
/**
 * 转换为只有月日的格式
 */
fun Long.toMdDateString():String {
    val date = Date(this)
    val sdf = SimpleDateFormat("MM月dd日")
    return sdf.format(date)
}


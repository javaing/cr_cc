package com.aliee.quei.mo.utils.extention

import java.util.*

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */
/**
 * 判断是否是今天
 */
fun Calendar.isToday() = this.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)

fun Calendar.second() = get(Calendar.SECOND)

fun Calendar.hourOfDay() = get(Calendar.HOUR_OF_DAY) // 24小时制的时间

fun Calendar.hour() = get(Calendar.HOUR)

fun Calendar.minute() = get(Calendar.MINUTE)

fun Calendar.year() = get(Calendar.YEAR)

fun Calendar.month() = get(Calendar.MONTH)

fun Calendar.day() = get(Calendar.DATE)

fun Calendar.toYMDString(): String = "${year()}${month()}${day()}"
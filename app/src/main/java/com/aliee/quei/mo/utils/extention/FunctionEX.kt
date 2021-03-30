package com.aliee.quei.mo.utils.extention

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */

inline infix fun <T : Any?> T.orElse(orBlock: () -> Unit) = this ?: orBlock()

inline fun guard(vararg params: Any?): Boolean? {
    return if (params.all { it != null }) true else null
}

//三目运算
fun <T> select(isTrue: Boolean, param1: () -> T, param2: () -> T) = if (isTrue) param1() else param2()

fun <T> select(isTrue: Boolean, param1: T, param2: T) = if (isTrue) param1 else param2
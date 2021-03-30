package com.aliee.quei.mo.utils.extention

import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */
//判断字符串是否为null或者空字符串
fun String.isEmpty(s: String?) = s == null || s == ""

fun CharSequence.isPhone() : Boolean {
    val reg = "^1\\d{10}$".toRegex()
    return reg.matches(this)
}

fun CharSequence.setSpanColor(text : String,color : Int) : CharSequence{
    val sb = SpannableStringBuilder(this)
    try {
        val pattern : Pattern = Pattern.compile(text)
        val matcher : Matcher = pattern.matcher(sb)
        while (matcher.find()){
            val colorSpan = ForegroundColorSpan(color)
            sb.setSpan(colorSpan,matcher.start(),matcher.end(),SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    } catch (e : Exception){
        e.printStackTrace()
    }

    return sb
}
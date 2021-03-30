package com.aliee.quei.mo.utils.extention

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindUntilEvent
import com.umeng.analytics.MobclickAgent
import java.util.concurrent.TimeUnit

/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:
 */


fun View.click(function: (view : View) -> kotlin.Unit) {
    RxView.clicks(this)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .subscribe ({
                function(this)
            },{
                it.printStackTrace()
            })
}

fun View.click(function: (view : View) -> Unit,eventId : String){
    RxView.clicks(this)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .subscribe ({
                function(this)
                MobclickAgent.onEvent(context,eventId)
            },{
                it.printStackTrace()
            })
}

/**
 * 点击事件，默认销毁的时候
 */
fun View.click(owner: LifecycleOwner, function: () -> kotlin.Unit) {
    RxView.clicks(this)
            .throttleFirst(200, TimeUnit.MILLISECONDS)
            .bindUntilEvent(owner, Lifecycle.Event.ON_DESTROY)
            .subscribe {
                function()
            }
}

fun View.setTextColor(color : Int){
    when(this){
        is TextView -> this.setTextColor(color)
        is ViewGroup -> {
            val count = this.childCount
            for(i in 0 until count){
                this.getChildAt(i).setTextColor(color)
            }
        }
    }
}

fun View.setTypeFace(typeface: Typeface){
    when(this){
        is TextView -> this.typeface = typeface
        is ViewGroup -> {
            val count = this.childCount
            for(i in 0 until count){
                this.getChildAt(i).setTypeFace(typeface)
            }
        }
    }
}


/**
 * 点击事件
 */
fun View.click(owner: LifecycleOwner, event: Lifecycle.Event,
               function: () -> kotlin.Unit) {
    RxView.clicks(this)
            .throttleFirst(1, TimeUnit.SECONDS)
            .bindUntilEvent(owner, event)
            .subscribe {
                function()
            }
}

/**
 * 点击事件
 */
fun View.longClick(owner: LifecycleOwner, event: Lifecycle.Event,
                   function: () -> kotlin.Unit) {
    RxView.clicks(this)
            .throttleFirst(1, TimeUnit.SECONDS)
            .bindUntilEvent(owner, event)
            .subscribe {
                function()
            }
}

fun View.longClick(function: () -> kotlin.Unit){
    RxView.longClicks(this)
            .subscribe({
                function()
            },{})
}

fun View.gone() :View{
    this.visibility = View.GONE
    return this
}

fun View.invisible() : View {
    this.visibility = View.INVISIBLE
    return this
}

fun View.show() : View{
    this.visibility = View.VISIBLE
    return this
}

fun View.enable() : View{
    when(this){
        is ViewGroup -> {
            val count = this.childCount
            for(i in 0 until count){
                this.getChildAt(i).enable()
            }
        }
        else -> this.isEnabled = true
    }
    return this
}

fun View.disable() : View {
    when(this){
        is ViewGroup -> {
            val count = this.childCount
            for(i in 0 until count){
                this.getChildAt(i).disable()
            }
        }
        else -> this.isEnabled = false
    }
    return this
}
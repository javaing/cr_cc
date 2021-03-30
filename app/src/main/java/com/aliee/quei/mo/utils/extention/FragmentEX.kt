package com.aliee.quei.mo.utils.extention

import android.app.Fragment
import android.graphics.drawable.Drawable
import android.widget.Toast

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */
fun Fragment.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) = activity?.toast(resId, duration)

fun Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) = activity?.toast(text, duration)

fun Fragment.getResDrawable(resId: Int): Drawable = resources.getDrawable(resId)

fun Fragment.getResColor(resId: Int): Int = resources.getColor(resId)

fun android.support.v4.app.Fragment.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) = activity?.toast(resId, duration)

fun android.support.v4.app.Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) = activity?.toast(text, duration)

fun android.support.v4.app.Fragment.getDrawable(resId: Int): Drawable = resources.getDrawable(resId)

fun android.support.v4.app.Fragment.getResColor(resId: Int): Int = resources.getColor(resId)

fun android.support.v4.app.Fragment.addFragment(fragment: android.support.v4.app.Fragment, frameId: Int) {
    childFragmentManager.inTransaction {
        add(frameId, fragment)
    }
}


fun android.support.v4.app.Fragment.removeFragment(fragment: android.support.v4.app.Fragment) {
    childFragmentManager.inTransaction {
        remove(fragment)
    }
}

fun android.support.v4.app.Fragment.replaceFragment(fragment: android.support.v4.app.Fragment, frameId: Int) {
    childFragmentManager.inTransaction {
        replace(frameId, fragment)
    }
}

fun android.support.v4.app.Fragment.showHideFragment(show: android.support.v4.app.Fragment, hide: android.support.v4.app.Fragment) {
    childFragmentManager.inTransaction {
        hide(hide)
        show(show)
    }
}

fun android.support.v4.app.Fragment.showFragment(show: android.support.v4.app.Fragment) {
    childFragmentManager.inTransaction {
        show(show)
    }
}

fun android.support.v4.app.Fragment.hideFragment(hide: android.support.v4.app.Fragment) {
    childFragmentManager.inTransaction {
        hide(hide)
    }
}



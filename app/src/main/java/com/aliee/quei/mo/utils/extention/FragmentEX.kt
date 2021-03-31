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

fun androidx.fragment.app.Fragment.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) = activity?.toast(resId, duration)

fun androidx.fragment.app.Fragment.toast(text: String, duration: Int = Toast.LENGTH_SHORT) = activity?.toast(text, duration)

fun androidx.fragment.app.Fragment.getDrawable(resId: Int): Drawable = resources.getDrawable(resId)

fun androidx.fragment.app.Fragment.getResColor(resId: Int): Int = resources.getColor(resId)

fun androidx.fragment.app.Fragment.addFragment(fragment: androidx.fragment.app.Fragment, frameId: Int) {
    childFragmentManager.inTransaction {
        add(frameId, fragment)
    }
}


fun androidx.fragment.app.Fragment.removeFragment(fragment: androidx.fragment.app.Fragment) {
    childFragmentManager.inTransaction {
        remove(fragment)
    }
}

fun androidx.fragment.app.Fragment.replaceFragment(fragment: androidx.fragment.app.Fragment, frameId: Int) {
    childFragmentManager.inTransaction {
        replace(frameId, fragment)
    }
}

fun androidx.fragment.app.Fragment.showHideFragment(show: androidx.fragment.app.Fragment, hide: androidx.fragment.app.Fragment) {
    childFragmentManager.inTransaction {
        hide(hide)
        show(show)
    }
}

fun androidx.fragment.app.Fragment.showFragment(show: androidx.fragment.app.Fragment) {
    childFragmentManager.inTransaction {
        show(show)
    }
}

fun androidx.fragment.app.Fragment.hideFragment(hide: androidx.fragment.app.Fragment) {
    childFragmentManager.inTransaction {
        hide(hide)
    }
}



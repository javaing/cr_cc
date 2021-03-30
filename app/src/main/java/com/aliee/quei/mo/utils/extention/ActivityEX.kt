package com.aliee.quei.mo.utils.extention

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

/**
 * @Author: YangYang
 * @Date: 2018/1/4
 * @Version: 1.0.0
 * @Description:
 */

fun Activity.hideKeyboard() {
    try {
        getInputMethodManager().hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
    } catch (e: NullPointerException) {

    }

}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        replace(frameId, fragment)
    }
}

fun AppCompatActivity.showHideFragment(show: Fragment, hide: Fragment) {
    supportFragmentManager.inTransaction {
        hide(hide)
        hide.userVisibleHint = false
        show(show)
        show.userVisibleHint = true
    }
}

fun AppCompatActivity.showFragment(show: Fragment) {
    supportFragmentManager.inTransaction {
        show(show)
    }
}

fun AppCompatActivity.hideFragment(hide: Fragment) {
    supportFragmentManager.inTransaction {
        hide(hide)
    }
}


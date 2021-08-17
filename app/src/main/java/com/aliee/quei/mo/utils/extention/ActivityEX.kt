package com.aliee.quei.mo.utils.extention

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity

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

fun Activity.openExternalWeb(url: String?) {
    val uri = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, uri)
    startActivity(intent)
}

/**
 * 提示弹窗
 */
fun Activity.showDialog(title:String, msg:String) {
    val builder = android.app.AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(msg)
        .setPositiveButton("知道了") { _, _ ->
        }
    builder!!.create().show()
}

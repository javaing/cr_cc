package com.aliee.quei.mo.utils.extention

import android.content.ClipData
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.net.Uri
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import com.aliee.quei.mo.utils.ToastUtil

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */

fun Context.getVersionCode(): Int = packageManager.getPackageInfo(packageName, 0).versionCode

fun Context.getVersionName(): String = packageManager.getPackageInfo(packageName, 0).versionName

fun Context.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    if (text.isEmpty())return
    ToastUtil.showToast(this,text.toString(),duration)
}

fun Context.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) = ToastUtil.showToast(this,getString(resId),duration)

fun Context.getResColor(resId: Int): Int = resources.getColor(resId)

fun Context.getResDrawable(resId: Int): Drawable = resources.getDrawable(resId)


fun Context.dp2px(dip: Int): Int {
    val scale = resources.displayMetrics.density
    return (dip * scale + 0.5f).toInt()
}

fun Context.screenWidth(): Int {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    val display = windowManager.defaultDisplay
    display.getMetrics(dm)
    return dm.widthPixels
}

fun Context.screenHeight(): Int {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    val display = windowManager.defaultDisplay
    display.getMetrics(dm)
    return dm.heightPixels
}

fun Context.copyTextToClipboard(value: String) {
    getClipboardManager().primaryClip = ClipData.newPlainText("text", value)
}

fun Context.copyUriToClipboard(uri: Uri) {
    getClipboardManager().primaryClip = ClipData.newUri(contentResolver, "uri", uri)
}

fun Context.getTextFromClipboard(): CharSequence {
    val clipData = getClipboardManager().primaryClip
    if (clipData != null && clipData.itemCount > 0) {
        return clipData.getItemAt(0).coerceToText(this)
    }

    return ""
}

fun Context.getUriFromClipboard(): Uri? {
    val clipData = getClipboardManager().primaryClip
    if (clipData != null && clipData.itemCount > 0) {
        return clipData.getItemAt(0).uri
    }

    return null
}

fun Context.getPreferences(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}

fun Context.getPreferences(name: String, mode: Int = Context.MODE_PRIVATE): SharedPreferences {
    return getSharedPreferences(name, mode)
}
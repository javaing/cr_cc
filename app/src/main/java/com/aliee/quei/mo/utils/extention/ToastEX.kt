package com.aliee.quei.mo.utils.extention

import android.view.Gravity
import android.view.View
import android.widget.Toast

/**
 * @Author: YangYang
 * @Date: 2018/1/3
 * @Version: 1.0.0
 * @Description:
 */
//自定义Toast
fun Toast.makeView(customeView: View, gravity: Int = Gravity.CENTER, xOff: Int = 0,
                   yOff: Int = 0, duration: Int = Toast.LENGTH_LONG): Toast {
    this.view = customeView
    setGravity(gravity, xOff, yOff)
    setDuration(duration)
    return this
}
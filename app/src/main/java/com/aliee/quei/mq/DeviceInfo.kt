package com.aliee.quei.mq

import android.content.Context
import android.util.Pair

object DeviceInfo {
    init {
        System.loadLibrary("device")
    }
    @JvmStatic
    external fun getDeviceId(context:Context,time:Long): Pair<String, String>



}
package com.aliee.quei.mo.component

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.alibaba.android.arouter.launcher.ARouter

/**
 * Created by Administrator on 2018/5/7 0007.
 */
class NotificationReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION = "com.ex.tt.ss.notification"
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == ACTION) {
            val url = intent.getStringExtra("url")
            if(!TextUtils.isEmpty(url)){
                ARouter.getInstance().build(url)
                    .navigation()
            }
        }
    }
}
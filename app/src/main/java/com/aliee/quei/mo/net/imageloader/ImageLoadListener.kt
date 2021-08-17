package com.aliee.quei.mo.net.imageloader

import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.aliee.quei.mo.data.service.OtherService
import com.aliee.quei.mo.net.retrofit.RetrofitClient
import com.aliee.quei.mo.utils.extention.load
import com.aliee.quei.mo.utils.rxjava.SchedulersUtil
import java.net.URLDecoder

class ImageLoadListener(private val imageView : ImageView) : RequestListener<Bitmap> {
    override fun onLoadFailed(e: GlideException?, model: Any?,
        target: Target<Bitmap>?,
        isFirstResource: Boolean
    ): Boolean {
        RetrofitClient.createService(OtherService::class.java)
            .reportErr(URLDecoder.decode(model.toString(), "utf-8"))
            .compose(SchedulersUtil.applySchedulers())
            .subscribe(
                {
                    imageView.load(it.data?.url)
                },{
                    it.printStackTrace()
                }
            )
        return true
    }

    override fun onResourceReady(
        resource: Bitmap?,
        model: Any?,
        target: Target<Bitmap>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        return false
    }
}
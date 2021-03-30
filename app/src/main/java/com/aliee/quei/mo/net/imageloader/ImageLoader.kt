package com.aliee.quei.mo.net.imageloader

import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File

/**
 * @Author: YangYang
 * @Date: 2017/12/25
 * @Version: 1.0.0
 * @Description:
 */
interface ImageLoader {

    //加载文件类型
    fun loadImage(imageView: ImageView, file: File)

    //通过图片地址加载
    fun loadImage(imageView: ImageView, url: String)

    //通过图片资源ID加载
    fun loadImage(imageView: ImageView, resId: Int)

    //有占位图和异常图片的文件类型加载
    fun loadImage(imageView: ImageView, file: File, loadingResId: Int, errorResId: Int)

    //有占位图和异常图片的图片地址加载
    fun loadImage(imageView: ImageView, url: String, loadingResId: Int, errorResId: Int)

    //有占位图和异常图片的资源ID加载
    fun loadImage(imageView: ImageView, resId: Int, loadingResId: Int, errorResId: Int)

    //加载文件类型圆形图片
    fun loadCircleImage(imageView: ImageView, file: File)

    //通过图片地址加载圆形图片
    fun loadCircleImage(imageView: ImageView, url: String)

    //通过图片资源ID加载圆形图片
    fun loadCircleImage(imageView: ImageView, resId: Int)

    //有占位图和异常图片的文件类型圆形图片加载
    fun loadCircleImage(imageView: ImageView, file: File, loadingResId: Int, errorResId: Int)

    //有占位图和异常图片的通过图片地址加载圆形图片
    fun loadCircleImage(imageView: ImageView, url: String, loadingResId: Int, errorResId: Int)

    //有占位图和异常图片的资源ID加载圆形图片
    fun loadCircleImage(imageView: ImageView, resId: Int, loadingResId: Int, errorResId: Int)

    //加载文件类型圆角图片
    fun loadRoundImage(imageView: ImageView, file: File, radius: Int)

    //通过图片地址加载圆角图片
    fun loadRoundImage(imageView: ImageView, url: String, radius: Int)

    //通过图片资源ID加载圆角图片
    fun loadRoundImage(imageView: ImageView, resId: Int, radius: Int)

    //有占位图和异常图片的文件类型圆角图片加载
    fun loadRoundImage(imageView: ImageView, file: File, loadingResId: Int, errorResId: Int, radius: Int)

    //有占位图和异常图片的通过图片地址加载圆角图片
    fun loadRoundImage(imageView: ImageView, url: String, loadingResId: Int, errorResId: Int, radius: Int)

    //有占位图和异常图片的资源ID加载圆角图片
    fun loadRoundImage(imageView: ImageView, resId: Int, loadingResId: Int, errorResId: Int, radius: Int)


    fun loadBlurImage(imageView: ImageView, url: String, r: Int, sampling: Int)

    fun loadImageByte(imageView: ImageView, byteArray: ByteArray)

    fun loadBlurImageBitmap(imageView: ImageView, bitmap: Bitmap)
    fun loadImageBitmap(imageView: ImageView, bitmap: Bitmap)

    fun loadImageScale(imageView: ImageView, url: String)
    fun loadImageScale(imageView: ImageView, url: String, viewParent: View)


    fun loadGif(imageView: ImageView,url:String)
    fun loadGif(imageView: ImageView,byteArray: ByteArray)
}
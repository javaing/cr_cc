package com.aliee.quei.mo.utils.extention

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.net.imageloader.ImageLoaderProvider
import com.aliee.quei.mo.net.imageloader.glide.GlideApp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

/**
 * @Author: YangYang
 * @Date: 2017/12/28
 * @Version: 1.0.0
 * @Description:
 */


/**
 * 加载图片
 */
fun ImageView.load(url: String?) {
    url?.let {
        var imgUrl = it
        if (!it.startsWith("http://")) {
            imgUrl = "${CommonDataProvider.instance.getImgDomain()}$it"
        }
        ImageLoaderProvider.getImageLoader().loadImage(this, imgUrl.trim())
    }
}

/**
 * 加载图片有占位图和加载错误图
 */
fun ImageView.load(url: String?, loadingResId: Int, errorResId: Int) {
    //  this.setImageResource(loadingResId)
    url?.let {
        var imgUrl = it
        if (!it.startsWith("http://")) {
            imgUrl = "${CommonDataProvider.instance.getImgDomain() + "/"}$it"
        }
        ImageLoaderProvider.getImageLoader().loadImage(this, imgUrl.trim(), loadingResId, errorResId)
    }
}

/**
 *
 */
fun ImageView.loadNovelCover(url: String?) {
    // Log.d("loadNovelCover", "url:$url")
    this.load(url, R.mipmap.img_default_cover, R.mipmap.img_default_cover)
}


fun ImageView.loadBlurCover(url: String?) {
    url?.let {
        var imgUrl = it
        if (!it.startsWith("http://")) {
            imgUrl = "${CommonDataProvider.instance.getImgDomain() + "/"}$it"
        }
        ImageLoaderProvider.getImageLoader().loadBlurImage(this, imgUrl.trim(), 25, 8)
    }
}


fun ImageView.loadBlurCoverBitmap(bitmap: Bitmap) {
    ImageLoaderProvider.getImageLoader().loadBlurImageBitmap(this, bitmap)
}

fun ImageView.loadCoverBitmap(bitmap: Bitmap) {
    ImageLoaderProvider.getImageLoader().loadBlurImageBitmap(this, bitmap)
}

fun ImageView.loadImageScale(url: String) {
    ImageLoaderProvider.getImageLoader().loadImageScale(this, url)
}

fun ImageView.loadImageScale(url: String, viewGroup: View/*, position:Int,textView: TextView*/) {
    ImageLoaderProvider.getImageLoader().loadImageScale(this, url, viewGroup/*,position,textView*/)
}

fun ImageView.loadImageScale(url: String, viewGroup: FrameLayout) {
    ImageLoaderProvider.getImageLoader().loadImageScale(this, url, viewGroup)
}

fun ImageView.loadHtmlImg(url: String) {
    val suffix: String = url.substring(url.lastIndexOf(".") + 1)
    val imgUrl = "$url".replace(".$suffix", ".html")
   /* if (contentType == "gif") {
        ImageLoaderProvider.getImageLoader().loadGif(this, imgUrl)
    } else {
        load(imgUrl)
    }*/
    load(imgUrl)
  // Glide.with(this.context).asGif().load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605952974631&di=8d632765c03ecc8235f4fbadb31e76a7&imgtype=0&src=http%3A%2F%2Fpic.87g.com%2Fupload%2F2018%2F0905%2F20180905095730580.gif").into(this)
}





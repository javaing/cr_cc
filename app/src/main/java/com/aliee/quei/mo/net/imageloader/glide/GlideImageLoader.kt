package com.aliee.quei.mo.net.imageloader.glide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.aliee.quei.mo.R
import com.aliee.quei.mo.net.imageloader.ImageLoadListener
import com.aliee.quei.mo.net.imageloader.ImageLoader
import com.aliee.quei.mo.utils.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.File


/**
 * @Author: YangYang
 * @Date: 2017/12/25
 * @Version: 1.0.0
 * @Description:
 */
object GlideImageLoader : ImageLoader {
    val intArray = IntArray(ImageView.ScaleType.values().size)

    init {
        intArray[ImageView.ScaleType.CENTER_INSIDE.ordinal] = 1
        intArray[ImageView.ScaleType.FIT_CENTER.ordinal] = 2
        intArray[ImageView.ScaleType.CENTER_CROP.ordinal] = 3
    }

    open fun a(scaleType: ImageView.ScaleType): RequestOptions {
        val requestOptions = RequestOptions()
        val i4 = intArray[scaleType.ordinal]
        when {
            i4 == 1 -> {
                requestOptions.centerInside()
            }
            i4 == 2 -> {
                requestOptions.fitCenter()
            }
            i4 != 3 -> {
                requestOptions.centerCrop()
            }
            else -> {
                requestOptions.centerCrop()
            }
        }
        //requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
        return requestOptions
    }

    override fun loadImageScale(imageView: ImageView, url: String, viewParent: View) {
        val context = imageView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val point =  Point()
        display.getRealSize(point)

        val MAX_WIDTH = point.x
        val MAX_HEIGHT = ScreenUtils.dpToPx(380)
        val options: RequestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
            .format(DecodeFormat.PREFER_RGB_565)
                .dontAnimate()


        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(options)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        val width = resource.width
                        val height = resource.height
                        val imageWidth: Int
                        val imageHeight: Int
                        val params1 = viewParent.layoutParams
                        val params = imageView.layoutParams

                        if (width > height) {
                            imageWidth = MAX_WIDTH
                            imageHeight = imageWidth * height / width
                            params.width = imageWidth
                            params.height = imageHeight
                            params1.width = imageWidth
                            params1.height = imageHeight
                            viewParent.layoutParams = params1
                        } else if (height > width) {
                            imageHeight = MAX_HEIGHT
                            imageWidth = width * imageHeight / height
                            params.width = imageWidth
                            params.height = imageHeight
                            params1.width = MAX_WIDTH
                            params1.height = imageHeight
                            viewParent.layoutParams = params1
                        } else if (height == width) {
                            imageHeight = ScreenUtils.dpToPx(200)
                            imageWidth = ScreenUtils.dpToPx(200)
                            params.width = imageWidth
                            params.height = imageHeight
                            params1.width = MAX_WIDTH
                            params1.height = imageHeight
                            viewParent.layoutParams = params1
                        }
                        imageView.layoutParams = params
                        imageView.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }

    override fun loadGif(imageView: ImageView, url: String) {
        Glide.with(imageView.context)
                .load(url)
                .into(imageView)
    }

    override fun loadGif(imageView: ImageView, byteArray: ByteArray) {
        Glide.with(imageView.context).load(byteArray).into(imageView)
    }


    override fun loadImageScale(imageView: ImageView, url: String) {
        //???????????????????????????
        Glide.with(imageView.context)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        var width: Int = resource.width
                        var height: Int = resource.height

                        val params = imageView.layoutParams
                        params.width = LinearLayout.LayoutParams.MATCH_PARENT
                        params.height = LinearLayout.LayoutParams.MATCH_PARENT
                        imageView.layoutParams = params
                        imageView.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
    }


    override fun loadBlurImageBitmap(imageView: ImageView, bitmap: Bitmap) {
        Glide.with(imageView.context)
                .load(bitmap)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 8)))
                .into(imageView)
    }

    override fun loadImageBitmap(imageView: ImageView, bitmap: Bitmap) {
        Glide.with(imageView.context)
                .load(bitmap)
                .into(imageView)
    }


    override fun loadBlurImage(imageView: ImageView, url: String, r: Int, sampling: Int) {
        /*   GlideApp.with(imageView.context)
              // .asBitmap()
               .load(url)
               .apply(RequestOptions.bitmapTransform(GlideBlurTransformer(r, sampling)))
               .into(imageView)*/

        var requestOptions = RequestOptions.bitmapTransform(BlurTransformation(r, sampling))
                .skipMemoryCache(false)
        Glide.with(imageView.context)
                .load(url)
                .apply(requestOptions)
                .into(imageView)
    }

    override fun loadImageByte(imageView: ImageView, byteArray: ByteArray) {
        Glide.with(imageView.context)
                .load(byteArray)
                .into(imageView)
    }

    override fun loadImage(imageView: ImageView, file: File) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .load(file)
                .into(imageView)
    }

    val bitmpListener = object : RequestListener<Bitmap> {
        override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: com.bumptech.glide.request.target.Target<Bitmap>?, p3: Boolean): Boolean {
            Log.e("Glide", "bitmpListener onLoadFailed:${p0.toString()}}")
            return false
        }
        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            return false
        }
    }

    val drawableListener = object : RequestListener<Drawable> {
        override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
            Log.e("Glide", "drawableListener onLoadFailed:${p0.toString()}}")
            return false
        }
        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            return false
        }
    }

    override fun loadImage(imageView: ImageView, url: String) {
        try {
            GlideApp.with(imageView.context)
                    .load(url)
                    .listener(drawableListener)
                    .placeholder(R.mipmap.img_default_cover)
                    .into(imageView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loadImage(imageView: ImageView, resId: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .load(resId)
                .into(imageView)
    }

    override fun loadImage(imageView: ImageView, file: File, loadingResId: Int, errorResId: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .listener(bitmpListener)
                .placeholder(imageView.drawable)
                .load(file)
                .centerCrop()
                .error(errorResId)
                .into(imageView)
    }

    override fun loadImage(imageView: ImageView, url: String, loadingResId: Int, errorResId: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .placeholder(R.mipmap.img_default_cover)
                .load(url)
                .centerCrop()
                .error(errorResId)
                .addListener(ImageLoadListener(imageView))
                .into(imageView)
    }

    override fun loadImage(imageView: ImageView, resId: Int, loadingResId: Int, errorResId: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .placeholder(loadingResId)
                .load(resId)
                .centerCrop()
                .error(errorResId)
                .into(imageView)
    }

    override fun loadCircleImage(imageView: ImageView, file: File) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .load(file)
                .circleCrop()
                .into(imageView)
    }

    override fun loadCircleImage(imageView: ImageView, url: String) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .load(url)
                .circleCrop()
                .into(imageView)
    }

    override fun loadCircleImage(imageView: ImageView, resId: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .load(resId)
                .circleCrop()
                .into(imageView)
    }

    override fun loadCircleImage(imageView: ImageView, file: File, loadingResId: Int, errorResId: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .placeholder(loadingResId)
                .load(file)
                .circleCrop()
                .error(errorResId)
                .into(imageView)
    }

    override fun loadCircleImage(imageView: ImageView, url: String, loadingResId: Int, errorResId: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .placeholder(loadingResId)
                .load(url)
                .circleCrop()
                .error(errorResId)
                .into(imageView)
    }

    override fun loadCircleImage(imageView: ImageView, resId: Int, loadingResId: Int, errorResId: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .placeholder(loadingResId)
                .load(resId)
                .circleCrop()
                .error(errorResId)
                .into(imageView)
    }

    override fun loadRoundImage(imageView: ImageView, file: File, radius: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .load(file)
                .transform(GlideRoundTransform(imageView.context, radius))
                .into(imageView)
    }

    override fun loadRoundImage(imageView: ImageView, url: String, radius: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .load(url)
                .transform(GlideRoundTransform(imageView.context, radius))
                .into(imageView)
    }

    override fun loadRoundImage(imageView: ImageView, resId: Int, radius: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .load(resId)
                .transform(GlideRoundTransform(imageView.context, radius))
                .into(imageView)
    }

    override fun loadRoundImage(imageView: ImageView, file: File, loadingResId: Int, errorResId: Int, radius: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .placeholder(loadingResId)
                .load(file)
                .transform(GlideRoundTransform(imageView.context, radius))
                .error(errorResId)
                .into(imageView)
    }

    override fun loadRoundImage(imageView: ImageView, url: String, loadingResId: Int, errorResId: Int, radius: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .placeholder(loadingResId)
                .load(url)
                .transform(GlideRoundTransform(imageView.context, radius))
                .error(errorResId)
                .into(imageView)
    }

    override fun loadRoundImage(imageView: ImageView, resId: Int, loadingResId: Int, errorResId: Int, radius: Int) {
        GlideApp.with(imageView.context)
                .asBitmap()
                .placeholder(loadingResId)
                .load(resId)
                .transform(GlideRoundTransform(imageView.context, radius))
                .error(errorResId)
                .into(imageView)
    }

}


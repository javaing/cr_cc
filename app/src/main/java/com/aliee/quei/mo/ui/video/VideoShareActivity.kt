package com.aliee.quei.mo.ui.video

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.data.bean.Video
import com.aliee.quei.mo.ui.video.vm.VideoInfoModel
import com.aliee.quei.mo.utils.QrCodeUtils
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.loadNovelCover
import com.aliee.quei.mo.utils.extention.videoUrl
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.activity_video_share.*
import kotlinx.android.synthetic.main.activity_video_share.ivCode
import kotlinx.android.synthetic.main.activity_video_share.btn_save_album
import kotlinx.android.synthetic.main.activity_video_share.btnCopyLink
import kotlinx.android.synthetic.main.activity_video_share.tv_share_link
import kotlinx.android.synthetic.main.save_album_view.*
import kotlinx.android.synthetic.main.video_share_view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class VideoShareActivity : BaseActivity() {

    private lateinit var mVideo: Video
    private lateinit var thumbUrl: String
    private var shareUrl: String? = null
    private val VM = VideoInfoModel()


    companion object {
        fun toThis(context: Context, video: Video, thumbUrl: String) {
            val bundle = Bundle()
            bundle.putString("imageUrl", thumbUrl)
            bundle.putParcelable("video", video)
            val intent = Intent(context, VideoShareActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_video_share
    }

    override fun initView() {
        initVM()
        ib_close.click {
            finish()
        }
        btn_save_album.click {
            saveToAlbum()
        }
        btnCopyLink.click {
            copyText("成人互动专区，激情短视频应有尽有\n${shareUrl!!}\n（@建议安卓使用Chrome浏览器，苹果使用Safari打开）")
        }
    }

    override fun initData() {
        val bundle = intent.extras
        if (bundle != null) {
            mVideo = bundle.getParcelable("video")
            thumbUrl = bundle.getString("imageUrl")
        }

        mVideo.let {
            tv_title.text = Html.fromHtml(it.name).trim()
            tv_thumb.loadNovelCover(imageUrl(thumbUrl, it.thumbImg!!).videoUrl())

            tv_title1.text = Html.fromHtml(it.name).trim()
            tv_thumb1.loadNovelCover(imageUrl(thumbUrl, it.thumbImg!!).videoUrl())
        }
        VM.videoShare(this, mVideo.id!!)

    }

    private fun initVM() {
        VM.videoShare.observeForever {
            when (it?.status) {
                Status.Success -> {
                    Log.d("tag", "url:${it.data}")
                    tv_share_link.text = it.data
                    tv_share_link1.text = it.data
                    shareUrl = it.data
                    genQrCode(it.data!!)
                }
            }
        }
    }

    fun genQrCode(url: String) {
        val bitmap: Bitmap = QrCodeUtils.createQRImage(this, ivCode, url)
        ivCode.setImageBitmap(bitmap)
        ivCode1.setImageBitmap(bitmap)
    }

    override fun getPageName(): String? {
        return "视频分享"
    }


    fun createBitmapFromView(): Bitmap {
        val view = video_share_view
        val w = ScreenUtils.getScreenSize(this).x
        val h = ScreenUtils.getScreenSize(this).y
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        view.layout(0, 0, w, h)
        view.draw(canvas)
        return bmp
    }

    fun saveToAlbum() {
        val PERMISSIONS = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")
        val permission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1)
        }

        val bitmap = createBitmapFromView()
        saveImageToGallery(bitmap, "share_qrcode_v")
    }

    /**
     * 保存图片到图库
     * @param bmp
     */
    fun saveImageToGallery(bmp: Bitmap, bitName: String) {
        val appDir = File(Environment.getExternalStorageDirectory(), "shareImg")
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = "$bitName.jpg"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        MediaStore.Images.Media.insertImage(this.contentResolver, file.absolutePath, bitName, null)
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(file)
        intent.data = uri
        sendBroadcast(intent)
        Toast.makeText(this, "二维码保存成功，路径为:${file.absolutePath}", Toast.LENGTH_LONG).show()
        Log.d("tag", "image:${file.absolutePath}")
    }


    fun imageUrl(url: String, thumbImg: String): String {
        val suffix: String = thumbImg.substring(thumbImg.lastIndexOf(".") + 1)
        return "${url}${thumbImg}".replace(".${suffix}", ".html")
    }

    fun copyText(shareUrl: String) {
        var myClipboard: ClipboardManager? = null
        var myClip: ClipData? = null
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        myClip = ClipData.newPlainText("text", shareUrl)
        myClipboard?.primaryClip = myClip

        Toast.makeText(this, "复制分享链接成功", Toast.LENGTH_SHORT).show()
    }
}
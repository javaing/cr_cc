package com.aliee.quei.mo.ui.user.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.main.vm.MainVideoModel
import com.aliee.quei.mo.utils.QrCodeUtils
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.save_album_view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


@Route(path = Path.PATH_VIDEO_SHARE)
class ShareActivity : BaseActivity() {


    private val VM = MainVideoModel()

    private var shareUrl = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_share
    }

    override fun initData() {
        VM.getShareDomain(this)
        //  CommonDataProvider.instance.cacheShareLinkId()
    }

    override fun initView() {
        initVM()


        titleBack.click {
            finish()
        }
        btn_save_album.click {
            saveToAlbum()
        }
        btnCopyLink.click {
            copyText(shareUrl)
        }
    }

    private fun initVM() {
        VM.shareDomainLiveData.observeForever {
            when (it?.status) {
                Status.Success -> {
                    Log.d("tag", "url:${it.data}")
                    shareUrl = "超级好看超级好玩超级刺激\uD83D\uDE0B\uD83C\uDF36${it.data!!}"
                    genQrCode(it.data!!)
                }
            }
        }
    }

    fun genQrCode(url: String) {
        val bitmap: Bitmap = QrCodeUtils.createQRImage(this, ivCode, url)
        ivCode.setImageBitmap(bitmap)
        iv_qrcode.setImageBitmap(bitmap)
    }

    fun copyText(shareUrl: String) {
        var myClipboard: ClipboardManager? = null
        var myClip: ClipData? = null
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?;
        myClip = ClipData.newPlainText("text", shareUrl);
        myClipboard?.setPrimaryClip(myClip);

        Toast.makeText(this, "复制分享链接成功", Toast.LENGTH_SHORT).show();
    }

    fun createBitmapFromView(): Bitmap {
        val view = fl_save_view
        val w = view.width
        val h = view.height
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
        saveImageToGallery(bitmap, "hh_v_qrcode")
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

    override fun getPageName(): String? {
        return "视频分享"
    }

    override fun onDestroy() {
        super.onDestroy()
        //  CommonDataProvider.instance.cacheShareLinkId(linkid = "0")
    }
}
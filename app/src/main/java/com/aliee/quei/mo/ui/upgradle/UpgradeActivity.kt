package com.aliee.quei.mo.ui.upgradle

import android.app.DownloadManager
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.util.Log
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.data.bean.VersionInfoBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.service.VersionUpdate
import com.aliee.quei.mo.ui.main.vm.MainVModel
import com.aliee.quei.mo.utils.DownloadManagerUtil
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.extention.*
import com.elvishew.xlog.XLog
import kotlinx.android.synthetic.main.activity_upgrade.*
import org.jetbrains.anko.downloadManager
import org.jetbrains.anko.textColor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class UpgradeActivity : BaseActivity() {

    var coins: Int = 0
    private val VM = MainVModel()
    private val CONTENT_URI = Uri.parse("content://downloads/my_downloads")

    companion object {
        var clientInfoBean: VersionInfoBean? = null
        fun start(context: Context, clientUpdateBean: VersionInfoBean) {
            clientInfoBean = clientUpdateBean
            val intent = Intent(context, UpgradeActivity::class.java)
            context.startActivity(intent)
        }
    }

    private var downloadId = -1L

    override fun getLayoutId(): Int {
        return R.layout.activity_upgrade
    }

    override fun initData() {
        signADCheck()
    }

    override fun initView() {
        val bean = clientInfoBean ?: return
        tv_version.text = bean.version
        tvDesc.text = bean.descr

        //替换版本更新弹窗背景图
        if (bean.descImgUrl!!.isNotEmpty()) {
            iv_bg.loadNovelCover(bean.descImgUrl)
            btnUpgrade.setBackgroundColor(Color.parseColor(bean.button))
        }else{
            iv_bg.setImageResource(R.mipmap.bg_upgrade)
        }
        btnUpgrade.click {
            btnUpgrade.setBackgroundColor(resources.getColor(R.color.color_disable))
            val url = bean.url ?: return@click
            if (bean.version.equals("-200")) {
                //临时变量-200为下载导流apk
//                Log.e("tag", "下载导流apk")
                Log.e("tag", bean.url)
                actDownloadApk(bean.url)
                //记录下载导流，
                bean.userInfo?.let {
                    if (it.tempUid != null) {
                        //临时用户tempuid不为null
                        VM.appUpdateOp(UpgradeActivity@ this, it.id!!, 1, 3)
                    } else {
                        //注册用户tempuid为null，uid不为空
                        VM.appUpdateOp(UpgradeActivity@ this, it.uid!!, 0, 3)
                    }
                }
                finish()
            } else {
                //版本更新
                startDownload(url, "${getString(R.string.app_name)}${bean.version}", bean.descr
                        ?: "")
                //新增最新的版本更新逻辑   20200930
                bean.userInfo?.let {
                    if (it.tempUid != null) {
                        VM.updateAppop(UpgradeActivity@ this, 3, it.id!!, 1)
                    } else {
                        //注册用户tempuid为null，uid不为空
                        VM.updateAppop(UpgradeActivity@ this, 3, it.uid!!, 0)
                    }
                }
            }

            //actIntentUpdate(getString(R.string.app_name),url)
            btnUpgrade.disable()
        }
        if (bean.isForce == 0) {
            ivClose.show()
        } else {
            ivClose.invisible()
        }
        ivClose.click {
            if (bean.version.equals("-200")) {
                //记录下载导流，
                bean.userInfo?.let {
                    if (it.tempUid != null) {
                        //临时用户tempuid不为null
                        VM.appUpdateOp(UpgradeActivity@ this, it.id!!, 1, 2)
                    } else {
                        //注册用户tempuid为null，uid不为空
                        VM.appUpdateOp(UpgradeActivity@ this, it.uid!!, 0, 2)
                    }
                }
            } else {
                //新增最新的版本更新逻辑   20200930
                bean.userInfo?.let {
                    if (it.tempUid != null) {
                        VM.updateAppop(UpgradeActivity@ this, 2, it.id!!, 1)
                    } else {
                        //注册用户tempuid为null，uid不为空
                        VM.updateAppop(UpgradeActivity@ this, 2, it.uid!!, 0)
                    }
                }
            }

          //  ARouterManager.goDailyLoginActivity(this, coins)
            finish()
        }
        initVM()
    }

    private fun initVM() {

        VM.signLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val list = it.data ?: return@Observer
                    val coin = list.bookbean!!
                    coins = coin.toInt()
//                    XLog.st(0).e("SignCoin = $coins")
                }
            }
        })


        tvDesc.text = clientInfoBean?.descr
    }

    private fun signADCheck() {

        val installTime = SharedPreUtils.getInstance().getLong("installTime")
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val today = sdf.format(Date())
        val installDay = sdf.format(Date(installTime))
        XLog.st(1).e("installDate = $installDay;today = $today")
        if (installDay != today) {
            var openRequest = SharedPreUtils.getInstance().getString("openRequest")
//        Log.e("MainActivity", "openRequest:" + openRequest)
            if (openRequest.equals("") || openRequest.isEmpty()) {
                VM.getSignAd(this)
                SharedPreUtils.getInstance().putString("openRequest", today.toString());
            } else {
//            val sdf = SimpleDateFormat("yyyy-MM-dd")
                val tomorrow = today.toString()
                var compare = tomorrow.compareTo(openRequest)
//            Log.e("MainActivity", "openRequest compare:" + compare)
                if (compare > 0) {
                    VM.getSignAd(this)
                    SharedPreUtils.getInstance().putString("openRequest", today.toString());
                }
            }
        }

    }

    override fun onBackPressed() {
//        super.onBackPressed()
        if (BuildConfig.DEBUG) {
            signADCheck()
            return
        }
        val isForce = clientInfoBean?.isForce ?: 0 > 0
        if (isForce) {
            ReaderApplication.instance.appManager.appExit()
        } else {
            signADCheck()
        }
    }

    override fun getPageName(): String? {
        return "升级弹窗"
    }

    private val mHandler = Handler()
    private var downloadUri: Uri? = null
    private var downloadObserver = object : ContentObserver(mHandler) {
        override fun onChange(selfChange: Boolean) {
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {

            val progress = getBytesAndStatus(downloadId)
            val downloadedByte = progress.getOrNull(0) ?: 0
            val totalByte = progress.getOrNull(1) ?: 1

            val status = progress[2]
            when (status) {
                DownloadManager.ERROR_CANNOT_RESUME,
                DownloadManager.ERROR_DEVICE_NOT_FOUND,
                DownloadManager.ERROR_HTTP_DATA_ERROR,
                DownloadManager.ERROR_UNKNOWN,
                DownloadManager.STATUS_FAILED -> {
                    finish()
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    btnUpgrade.enable()
                    btnUpgrade.text = "安装"
                    btnUpgrade.click {
                        installApk(this@UpgradeActivity, downloadId)
                    }
                    installApk(this@UpgradeActivity, downloadId)
                }
                DownloadManager.STATUS_RUNNING -> {
                    val p = "${(downloadedByte * 1.0f / totalByte * 100).toInt()} % "
                    btnUpgrade.text = p
                }
            }
        }
    }

    private fun installApk(context: Context, downloadApkId: Long) {
        val dManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val install = Intent(Intent.ACTION_VIEW)
        val downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId)
        if (downloadFileUri != null) {
            Log.d("DownloadManager", downloadFileUri.toString())
            install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive")
            if ((Build.VERSION.SDK_INT >= 24)) {//判读版本是否在7.0以上
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
            }
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (install.resolveActivity(context.packageManager) != null) {
                context.startActivity(install)
            } else {
//                L.e("自动安装失败，请手动安装")
//
//                val service = context as SavePicNoLogService
//                service.showErrorTip("下载完成，请点击下拉列表的通知手动安装")
            }
        } else {
            Log.e("DownloadManager", "download error")
        }
    }

    fun getBytesAndStatus(downloadId: Long): IntArray {
        val bytesAndStatus = intArrayOf(-1, -1, 0)
        val query = DownloadManager.Query().setFilterById(downloadId)
        var c: Cursor? = null
        try {
            c = downloadManager.query(query)
            if (c != null && c.moveToFirst()) {
                bytesAndStatus[0] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                bytesAndStatus[1] = c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
            }
        } finally {
            c?.close()
        }
        return bytesAndStatus
    }

    private fun startDownload(url: String, title: String, desc: String) {
        downloadId = DownloadManagerUtil(this).download(url, title, desc)
        downloadUri = downloadManager.getUriForDownloadedFile(downloadId)
        contentResolver.registerContentObserver(CONTENT_URI, true, downloadObserver)
    }


    private fun actIntentUpdate(appName: String, downUrl: String) {
        var intent = Intent(this, VersionUpdate::class.java)
        intent.putExtra("appName", appName)
        intent.putExtra("down_url", downUrl)
        startService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(downloadObserver)
    }

    private fun actDownloadApk(url: String?) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}

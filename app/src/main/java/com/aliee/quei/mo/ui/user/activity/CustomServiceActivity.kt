package com.aliee.quei.mo.ui.user.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.webkit.*
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.SharedPreUtils.Key_CSRoute
import com.aliee.quei.mo.utils.extention.click
import kotlinx.android.synthetic.main.activity_custom_service.*
import kotlinx.android.synthetic.main.layout_title.*

@Route(path = Path.PATH_USER_CUSTOMERSERVICE)
class CustomServiceActivity : BaseActivity(){

    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mUploadMessageAboveL: ValueCallback<Array<Uri>>? = null
    private var pageTitle : String? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_custom_service
    }

    val wcc = object : WebChromeClient(){
//        override fun onReceivedTitle(p0: WebView?, p1: String?) {
//            super.onReceivedTitle(p0, p1)
//            pageTitle = p1
//            toolbar_title.text = pageTitle
//        }

        //        override fun onProgressChanged(p0: WebView?, p1: Int) {
//            super.onProgressChanged(p0, p1)
//            progressBar.progress = p1
//            if(p1 == 100){
//                progressBar.invisible()
//            } else {
//                progressBar.show()
//            }
//        }
        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?,
                                       fileChooserParams: FileChooserParams?): Boolean {
//            Log.d(TAG, "onShowFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)")
//            mUploadCallbackAboveL = filePathCallback
            mUploadMessageAboveL = filePathCallback!!
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            startActivityForResult(i, FILE_CHOOSER_RESULT_CODE)
            return true
        }
    }

    val wvc = object  : WebViewClient(){


        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


            return false
        }
    }


    override fun initData() {
        val csUrl = SharedPreUtils.getInstance().getString(Key_CSRoute)
        Log.d("CustomServiceActivity", "CSUrl:"+csUrl)
        webView.loadUrl(csUrl)
    }

    override fun initView() {
        titleBack.click { onBackPressed() }
        titleText.text = getString(R.string.customer_service)
        initWebView()
//        val fragment = ARouterManager.getWebFragmentCS(this)
//        addFragment(fragment, R.id.container)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webViewClient = wvc
//        webView.webChromeClient = wcc

        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(false)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.webChromeClient = object: WebChromeClient() {

            fun openFileChooser(
                    valueCallback: ValueCallback<Uri>,
                    acceptType: String,
                    capture: String
            ) {
                mUploadMessage = valueCallback
                openImageChooserActivity()
            }


            override fun onShowFileChooser(
                    webView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
            ): Boolean {
                mUploadMessageAboveL = filePathCallback
                openImageChooserActivity()
                return true
            }

        }
        webView.setOnLongClickListener(View.OnLongClickListener { true })

    }
    private fun openImageChooserActivity() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.addCategory(Intent.CATEGORY_OPENABLE)
        i.type = "image/*"
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == mUploadMessage && null == mUploadMessageAboveL) return
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            if (mUploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data)
            } else if (mUploadMessage != null) {
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {

        if (requestCode != FILE_CHOOSER_RESULT_CODE || mUploadMessageAboveL == null)
            return
        var results: Array<Uri>? = null
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    results = Array(clipData.itemCount){
                        i -> clipData.getItemAt(i).uri
                    }
                }
                if (dataString != null)
                    results = arrayOf(Uri.parse(dataString))
            }
        }
        mUploadMessageAboveL!!.onReceiveValue(results)
        mUploadMessageAboveL = null

    }

    companion object {
        private val FILE_CHOOSER_RESULT_CODE = 10000
    }

    override fun onResume() {
        webView.onResume()
        if(mUploadMessageAboveL != null){
            mUploadMessageAboveL!!.onReceiveValue(null)
            mUploadMessageAboveL = null

        }
        super.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ARouterManager.goContentActivity(
                this,
                showPage = ARouterManager.TAB_SHOP,
                showTab = ARouterManager.TAB_SHOP
        )
    }


    override fun getPageName() = "客服"
}
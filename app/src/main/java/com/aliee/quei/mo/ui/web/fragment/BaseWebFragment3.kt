package com.aliee.quei.mo.ui.web.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.component.*
import com.aliee.quei.mo.data.Global
import com.aliee.quei.mo.data.Global.KEY_HAS_OPEN_H5_PAY
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.invisible
import com.aliee.quei.mo.utils.extention.show
import com.aliee.quei.mo.utils.extention.toast
import com.aliee.quei.mo.utils.rxjava.RxBus
import kotlinx.android.synthetic.main.fragment_web.*
import kotlinx.android.synthetic.main.toorbar_web.*


@Route(path = Path.PATH_WEB_FRAGMENT3)
open class BaseWebFragment3 : BaseFragment(), FragmentBackHandler {

    override fun onBackPressed(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    val TAG = "BaseWebFragment3"
    private var pageTitle: String? = null

    @Autowired
    @JvmField
    var title: String = ""


    @Autowired
    @JvmField
    var pageName: String = ""


    @Autowired
    @JvmField
    var showTitle = false

    @Autowired
    @JvmField
    var referer = ""

    protected val COVER_SCREEN_PARAMS = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    lateinit var mCustomView: View
    lateinit var mvideoContainer: FrameLayout
    lateinit var mCustomViewCallback: WebChromeClient.CustomViewCallback

    companion object {
        fun newInstance(): BaseWebFragment3 {
            val arg = Bundle()
            val fragment = BaseWebFragment3()
            fragment.arguments = arg
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_web
    }

    val wcc = object : WebChromeClient() {

        override fun onReceivedTitle(p0: WebView?, p1: String?) {
            super.onReceivedTitle(p0, p1)
            pageTitle = p1
            toolbar_title.text = pageTitle
        }

        override fun onProgressChanged(p0: WebView?, p1: Int) {
            super.onProgressChanged(p0, p1)
            progressBar.progress = p1
            if (p1 == 100) {
                progressBar.invisible()
            } else {
                progressBar.show()
            }
        }

//         override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
//             if (mCustomView != null) {
//                 callback!!.onCustomViewHidden();
//                 return;
//             }
//
//             mCustomView = view!!
//             mFrameLayout.addView(mCustomView);
//             mCustomViewCallback = callback!!
//             webView.setVisibility(View.GONE);
//             activity!!.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//             super.onShowCustomView(view, callback)
//         }
//
//         override fun onHideCustomView() {
//             webView.setVisibility(View.VISIBLE);
//             if (mCustomView == null) {
//                 return;
//             }
//             mCustomView.setVisibility(View.GONE);
//             mFrameLayout.removeView(mCustomView);
//             mCustomViewCallback.onCustomViewHidden();
//             activity!!.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//             super.onHideCustomView()
//         }

    }

    val wvc = object : WebViewClient() {


        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {


            return false
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

        }

    }


    override fun initData() {
        Global[KEY_HAS_OPEN_H5_PAY] = "false"
        val url_head = CommonDataProvider.instance.getVideoDomain()
        //val url = url_head + "/#/?token=" + CommonDataProvider.instance.getToken()
        val url = "${url_head}/#/?token=${CommonDataProvider.instance.getToken()}&player=1"
       // val url = "${url_head}/#/?token=${CommonDataProvider.instance.getToken()}"
        Log.e("tag", "url:${url}")
        webView.loadUrl(url)


    }

    protected fun initTitle() {
        if (showTitle) {
            toolbar.visibility = View.VISIBLE
        } else {
            toolbar.visibility = View.GONE
        }
        iv_finish.click { activity?.finish() }
        if (BuildConfig.DEBUG) {
            menu.show()
        } else {
            menu.invisible()
        }
        menu.click {
            showMenu(it)
        }
    }

    private var popupMenu: PopupMenu? = null
    private fun showMenu(view: View) {
        if (popupMenu == null) {
            popupMenu = PopupMenu(activity!!, view)
            popupMenu?.inflate(R.menu.toolbar_menu)
            popupMenu?.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.refresh -> {
                        webView.reload()
                    }
                    R.id.copy -> {
                        toCopy(activity!!, webView.url)
                    }
                    R.id.default_browser -> {
                        openBrowser(webView.url)
                    }

                }
                true
            }
        }
        popupMenu?.show()
    }

    private fun openBrowser(targetUrl: String) {
        if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
            Toast.makeText(this.context, "$targetUrl 该链接无法使用浏览器打开。", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val mUri = Uri.parse(targetUrl)
        intent.data = mUri
        startActivity(intent)
    }

    /**
     * 复制字符串
     *
     * @param context
     * @param text
     */
    private fun toCopy(context: Context, text: String) {

        val mClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        mClipboardManager.primaryClip = ClipData.newPlainText(null, text)
        toast("网址已经复制到剪切板")
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        initTitle()
        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView() {
        webView.webViewClient = wvc
        webView.webChromeClient = wcc

        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        val userAgent: String = webSetting.getUserAgentString()
        webView.getSettings().setUserAgentString(userAgent + "webview");
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(false)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        webSetting.setAppCacheEnabled(true)
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.setOnLongClickListener(View.OnLongClickListener { true })
        webView.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                webView.goBack() // Navigate back to previous web page if there is one
                return@setOnKeyListener true;
            }

            false
        }

        webView.addJavascriptInterface(JsInterface(), "obj")

    }

    inner class JsInterface {
        @JavascriptInterface
        fun returnApp(result: String) {
//            Toast.makeText(ReaderApplication.instance, result, Toast.LENGTH_LONG).show()
            Log.d("tag", "returnApp：${result}")
            when (result) {
                "home" -> {
                    RxBus.getInstance().post(EventReturnComic())
                }
                "mine" -> {
                    RxBus.getInstance().post(EventToMine())
                }
                "recharge" -> {
                    RxBus.getInstance().post(EventToRecharge())
                }
                "bulletin" -> {
                    RxBus.getInstance().post(EventToBulletin())
                }
            }
        }

        @JavascriptInterface
        fun player(result: String) {
            ARouterManager.goVideoInfoActivity(context!!, result)
        }

    }

    override fun getPageName(): String? {
        return "H5$pageName"
    }

    override fun onResume() {
        webView.onResume()
        super.onResume()
    }

    override fun onPause() {
        webView.onPause()
        super.onPause()
    }

    override fun onDestroyView() {
        try {
            if (webView != null) {
//                val parent = webView.parent
//                if (parent is ViewGroup) {
//                    parent.removeAllViews()
//                }
                webView.destroy()
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        super.onDestroyView()
    }

}
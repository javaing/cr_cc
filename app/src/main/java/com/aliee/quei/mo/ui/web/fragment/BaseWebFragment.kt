package com.aliee.quei.mo.ui.web.fragment

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.PopupMenu
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.Global
import com.aliee.quei.mo.data.Global.KEY_HAS_OPEN_H5_PAY
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.invisible
import com.aliee.quei.mo.utils.extention.show
import com.aliee.quei.mo.utils.extention.toast
import kotlinx.android.synthetic.main.fragment_agentweb.*
import kotlinx.android.synthetic.main.toorbar_web.*

@Route(path = Path.PATH_WEB_FRAGMENT)
open class BaseWebFragment : BaseFragment(), FragmentBackHandler {
    override fun onBackPressed(): Boolean {
        if(webView.canGoBack()){
            webView.goBack()
            return true
        }
        return false
    }

    val TAG = "BaseWebFragment"
    private var pageTitle : String? = null

    @Autowired
    @JvmField var title : String = ""

    @Autowired
    @JvmField var url : String = ""

    @Autowired
    @JvmField var pageName : String = ""

    @Autowired
    @JvmField var orderId : String = ""

    @Autowired
    @JvmField var showTitle = false

    @Autowired
    @JvmField var referer = ""

    override fun getLayoutId(): Int {
        return R.layout.fragment_agentweb
    }

    val wcc = object : WebChromeClient(){
        override fun onReceivedTitle(p0: WebView?, p1: String?) {
            super.onReceivedTitle(p0, p1)
            pageTitle = p1
            toolbar_title.text = pageTitle
        }

        override fun onProgressChanged(p0: WebView?, p1: Int) {
            super.onProgressChanged(p0, p1)
            progressBar.progress = p1
            if(p1 == 100){
                progressBar.invisible()
            } else {
                progressBar.show()
            }
        }

    }

    private fun isSchemeInWhiteList(url: String): Boolean {
        if (url.startsWith("weixin://wap/pay") || url.startsWith("alipays://"))return true
        val list = CommonDataProvider.instance.getSchemeWhiteList() ?: return false
        for (scheme in list) {
            if (url.startsWith(scheme)) return true
        }
        return false
    }

    val wvc = object  : WebViewClient(){


        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

            if (url.startsWith("app://comic.hkzy.com") || url.startsWith("http://comic.hkzy.com")) {
                ARouter.getInstance().build(Uri.parse(url))
                    .navigation(activity)
                return true
            }
            if (url.startsWith("http://") || url.startsWith("https")) {
                if (referer.isNotEmpty()) {
                    view.loadUrl(url, mutableMapOf("Referer" to referer))
                } else {
                    view.loadUrl(url)
                }
                return true
            }

            if (isSchemeInWhiteList(url)) {
                try {
                    val uri = Uri.parse(url)
                    val intent = Intent()
                    intent.data = uri
                    view.context.startActivity(intent)
                    if (url.startsWith("weixin://wap/pay") || url.startsWith("alipays://")){
                        isPay = true
                        hasOpenPay = true
                        Global[KEY_HAS_OPEN_H5_PAY] = "true"
                        activity?.finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            return true
        }
    }

    var isPay = false
    var hasOpenPay = false
    override fun initData() {
        Global[KEY_HAS_OPEN_H5_PAY] = "false"

        if (referer.isNotEmpty()) {
            webView.loadUrl(url, mutableMapOf("Referer" to referer))
        } else {
            webView.loadUrl(url)
        }

    }

    protected fun initTitle() {
        if (showTitle) {
            toolbar.visibility = View.VISIBLE
        } else {
            toolbar.visibility = View.GONE
        }
        iv_finish.click { activity?.finish() }
        if(BuildConfig.DEBUG){
            menu.show()
        } else {
            menu.invisible()
        }
        menu.click {
            showMenu(it)
        }
    }

    private var popupMenu : PopupMenu? = null
    private fun showMenu(view : View) {
        if(popupMenu == null){
            popupMenu = PopupMenu(activity!!,view)
            popupMenu?.inflate(R.menu.toolbar_menu)
            popupMenu?.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.refresh -> {
                        webView.reload()
                    }
                    R.id.copy -> {
                        toCopy(activity!!,webView.url)
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.webViewClient = wvc
        webView.webChromeClient = wcc

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
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        //        webView.getSettings().setJavaScriptEnabled(true);
        webView.setOnLongClickListener(View.OnLongClickListener { true })
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
                val parent = webView.parent
                if (parent is ViewGroup){
                    parent.removeAllViews()
                }
            }
            webView.destroy()
        } catch (e : java.lang.Exception) {
            e.printStackTrace()
        }
        super.onDestroyView()
    }

}
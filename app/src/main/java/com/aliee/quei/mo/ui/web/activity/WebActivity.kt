package com.aliee.quei.mo.ui.web.activity

import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.github.ikidou.fragmentBackHandler.BackHandlerHelper
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseActivity
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.utils.extention.addFragment


@Route(path = Path.PATH_WEB_ACTIVITY)
open class WebActivity : BaseActivity(){

    @Autowired
    @JvmField var title : String = ""

    @Autowired
    @JvmField var url : String = ""

    @Autowired
    @JvmField var referer : String = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_webview
    }

    override fun initData() {
    }

    override fun initView() {
        val fragment = ARouterManager.getWebFragment(this,url,false,referer = referer)
        addFragment(fragment,R.id.container)
        initVM()
    }

    private fun initVM() {
    }

    override fun onBackPressed() {
        if(!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed()
        }
    }

    override fun getPageName(): String? {
        return "web页面"
    }
}
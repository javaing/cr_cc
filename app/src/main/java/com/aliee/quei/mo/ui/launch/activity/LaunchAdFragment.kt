package com.aliee.quei.mo.ui.launch.activity

import android.os.Bundle
import com.aliee.quei.mo.BuildConfig
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.video.fragment.VideoRecommendFragment
import com.aliee.quei.mo.utils.extention.loadHtmlImg
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.launch_ad_item.*
import java.util.concurrent.TimeUnit

class LaunchAdFragment : BaseFragment() {
    private var adInfo: AdInfo? = null

    companion object {
        fun newInstance(adInfo: AdInfo, position: Int): LaunchAdFragment {
            val args = Bundle()
            args.putInt("position", position)
            args.putParcelable("adInfo", adInfo)
            val fragment = LaunchAdFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.launch_ad_item
    }

    override fun initView() {
        val bundle = arguments
        if (bundle != null) {
            adInfo = bundle.getParcelable("adInfo")
        }
    }

    override fun initData() {
        iv.loadHtmlImg(adInfo?.imgurl!!)
        doDelays(adInfo?.sec!!, {
            bskip.text = "$it"
        }, {
            bskip.text = "跳过"
            adSkip()
        })
    }

    private fun adSkip() {
        bskip.setOnClickListener {
            enterApp()
        }
    }

    private fun adPreview() {
        // AdConfig.adPreview(adInfo!!.callbackurl)
    }

    private fun adClick() {
        // AdConfig.adClick(this,adInfo!!.clickurl)
    }


    fun doDelays(delay: Int, time: (time: Long) -> Unit, over: () -> Unit) {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(delay.toLong())
                .map {
                    delay - it
                }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ time(it) }, {}, { over.invoke() })
    }

    /**
     * 进入应用
     */
    private fun enterApp() {
        if (activity!!.isFinishing) return

        if (BuildConfig.futsu && !CommonDataProvider.instance.hasLogin()) {
            ARouterManager.goLoginActivity(activity!!, url = "app://comic.hkzy.com/main/activity?showPage=0")
            activity!!.finish()
            return
        }

//        ARouterManager.goMainActivity(this, showPage = ARouterManager.TAB_SHOP)
//        ARouterManager.goReserveActivity(this)
        ARouterManager.goContentActivity(activity!!, showPage = ARouterManager.TAB_SHOP)
        activity!!.finish()
        return
    }

    override fun getPageName(): String? {
        return "广告"
    }
}
package com.aliee.quei.mo.ui.main.fragment

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.android.arouter.facade.annotation.Route
import com.aliee.quei.mo.R
import com.aliee.quei.mo.base.BaseFragment
import com.aliee.quei.mo.base.response.Status
import com.aliee.quei.mo.component.EventRefreshHome
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.config.AdEnum
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.router.Path
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.ui.launch.vm.LaunchVModel
import com.aliee.quei.mo.ui.main.adapter.IconListAdapter
import com.aliee.quei.mo.ui.main.adapter.ItemData
import com.aliee.quei.mo.ui.main.adapter.ShopAdapter
import com.aliee.quei.mo.ui.main.vm.AdVModel
import com.aliee.quei.mo.ui.main.vm.ShopVModel
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.elvishew.xlog.XLog
import com.umeng.analytics.MobclickAgent
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_shop.*
import kotlinx.android.synthetic.main.layout_common_list.*
import retrofit2.HttpException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by Administrator on 2018/4/18 0018.
 */
@Route(path = Path.PATH_MAIN_SHOP)
class ShopFragment : BaseFragment() {
    override fun getPageName() = "书城"
    private val VM = ShopVModel()
    private val adVModel = AdVModel()
    private val adapter = ShopAdapter()

    companion object {
        fun newInstance(): ShopFragment {
            val args = Bundle()
            val fragment = ShopFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var shareLink = ""
    override fun getLayoutId() = R.layout.fragment_shop
    override fun initView() {
        initVM()
        initRefresh()
        initRecyclerView()
        initTitle()
        initEvent()
    }

    private fun initTitle() {
//        btn.gone()
//        edit.isFocusable = false
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 6)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    ShopAdapter.VIEW_TYPE_ITEM_LINEAR,
                    ShopAdapter.VIEW_TYPE_BANNER,
                    ShopAdapter.VIEW_TYPE_TITLE,
                    ShopAdapter.VIEW_TYPE_AD,
                    ShopAdapter.VIEW_TYPE_LAND_IMG -> 6
                    ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2 -> 3
                    else -> 2
                }
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.addItemDecoration(ShopItemDecoration())
        recyclerView.itemAnimator = DefaultItemAnimator()

        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        adapter.titleMoreClick = { title, rid ->
             ARouterManager.goMoreActivity(activity!!, title, rid)
           // loadAdInfoFolw()
        }




        val hashMap: HashMap<String, String?> = HashMap<String, String?>() //define empty hashmap
        adapter.itemClick = {
            //  VM.getComicDetailRand(activity!!, it.id, if (it.rid == "") "27" else it.rid!!)
            // ARouterManager.goComicDetailActivity(activity!!,it.id,true,it.rid?:"")
            if (it.bookcover!!.contains("http")) {
                AdConfig.adClick(activity!!, it.adClickUrl!!)
            } else {
                hashMap.clear()
                hashMap.put("BookCollect", it.title)
                MobclickAgent.onEvent(activity!!, "1", hashMap)
                Log.d("ShopFragment", "Rid：" + it.rid)
                ARouterManager.goReadActivity(activity!!, it.id, if (it.rid == "") 27 else it.rid!!.toInt(), 0, true)
                if (layoutModify.visibility == View.VISIBLE) {
                    layoutModify.visibility = View.INVISIBLE
                }
            }

        }

        adapter.ShareClick = {
            copyText()
        }

        val list: ArrayList<ItemData> = ArrayList()
        list.add(ItemData("原图示", R.mipmap.ic_launcher))
        list.add(ItemData("換图示1", R.mipmap.ic_fake1))
        list.add(ItemData("換图示2", R.mipmap.ic_fake2))
        list.add(ItemData("換图示3", R.mipmap.ic_fake3))

        val iconListAdapter = IconListAdapter(activity!!, R.layout.listview_item, list)
        listview.adapter = iconListAdapter
        listview.setOnItemClickListener { parent, view, position, id ->

            changeIcon(position)
            layoutModify.visibility = View.INVISIBLE
        }

        adapter.MenuClick = {
            if (layoutModify.visibility == View.INVISIBLE) {
                layoutModify.visibility = View.VISIBLE
            } else {
                layoutModify.visibility = View.INVISIBLE
            }
        }
    }


    private fun initRefresh() {
        refreshLayout.setOnRefreshListener {
            Log.d("tag", "refresh:11111")
            loadAdInfoFolw()
            //loadAdBanner()
            //  VM.loadShop(this)
        }
        refreshLayout.setOnLoadMoreListener {
            VM.loadMore()
        }
    }


    private fun doRetry() {
        statuslayout.showLoading()
        //VM.retryInitData(this)
        Log.d("tag", "err 重试")
        loadAdInfoFolw()

    }

    /**
     * 获取banner广告
     */
    private var adInfo: AdInfo? = null
    private fun loadAdBanner() {
        val adBean = AdConfig.getAd(AdEnum.BANNER.zid)
        adBean?.also {
            adVModel.getRotation(it, {
                this.adInfo = it
                loadAdInfoFolw()

            }, {

            })
        }
    }

    /**
     * 信息流
     */
    private var adMap = mutableMapOf<String, AdInfo>()
    private fun loadAdInfoFolw() {
//        if(1+1==2){
//            VM.loadShop(this)
//            return
//        }
        val banner = AdConfig.getAd(AdEnum.BANNER.zid)
        val flow90 = AdConfig.getAd(AdEnum.COMIC_INFO_FLOW_90.zid)
        val flowQu = AdConfig.getAd(AdEnum.COMIC_INFO_FLOW_QU.zid)
        val flowQiang = AdConfig.getAd(AdEnum.COMIC_INFO_FLOW_QIANG.zid)
//        Log.e("ad", "ad banner:$banner")
//        Log.e("ad", "ad flow90:$flow90")
//        Log.e("ad", "ad flowQu:$flowQu")
//        Log.e("ad", "ad flowQiang:$flowQiang")
        if (banner == null || flow90==null || flow90==null || flowQiang==null){
            VM.loadShop()
            return
        }
        adVModel.multipleAdApi(banner, flow90, flowQu!!, flowQiang, {
            //Log.d("tag", "ad map:1")
            if (it["flowObs90"] != null) {
                it["flowObs90"]!!.isClose = flow90.close
            }
            //Log.d("tag", "ad map:2")
            if (it["flowObsQu"] != null) {
                it["flowObsQu"]!!.index = flowQu.interval
            }
            //Log.d("tag", "ad map:3")
            if (it["flowObsQiang"] != null) {
                it["flowObsQiang"]!!.index = flowQiang.interval
            }
            //Log.d("tag", "ad map:4")
            adMap.clear()
            adMap.putAll(it)
            //Log.d("tag", "ad map:$adMap")
            VM.loadShop()
        }, {
            VM.loadShop()
        })
    }


    private fun initVM() {
        VM.moreLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val result = adapter.addMore(it.data?.list, adMap)
                    result ?: return@Observer
                }
                Status.Complete -> {
                    refreshLayout.finishLoadMore()
                    refreshLayout.finishRefresh()
                }
            }
        })
        VM.shopLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Error -> {
                    Log.d("tag", "数据加载err: shopLiveData")
                    isFirst = true
                    statuslayout.showError {
                        doRetry()
                    }
                    val e = it.e
                    if (e is HttpException) {
                        MobclickAgent.onEvent(requireContext(), "error_shop")
                    }
                }
                Status.NoNetwork -> {
                    isFirst = true
                    statuslayout.showNoNetwork {
                        doRetry()
                    }
                }
                Status.Complete -> {
                    disLoading()
                }
                Status.Success -> {
                    refreshLayout.finishRefresh()
                    statuslayout.showContent()
                    Log.d("tag", "数据加载成功:$adMap")
                    adapter.setData(it.data, adMap)
                }
            }
        })

        VM.comicDetailLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val bean = it.data ?: return@Observer
                    VM.getHistoryChapter(activity!!, bean.id)
                }
                Status.Error -> {
                    Log.d("tag", "数据加载err:comicDetailLiveData")
                }
            }
        })

        VM.historyChapterLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    val bean = it.data ?: return@Observer
                    ARouterManager.goReadActivity(activity!!, bean.bookid, bean.id)
                }
                Status.Error -> {
                    Log.d("tag", "数据加载err: historyChapterLiveData")
                }
                Status.NoNetwork -> {

                }
            }
        })

        VM.shareLinkLiveData.observe(this, Observer {
            when (it?.status) {
                Status.Success -> {
                    shareLink = it.data ?: return@Observer
//                    Log.d("ShopFragment", "ShareLink：" + shareLink)
                }
                Status.Error -> {
                    Log.d("tag", "数据加载err: shareLinkLiveData")
                }
                Status.NoNetwork -> {

                }
            }
        })


    }

    var sex = 1
    override fun initData() {
        loadAdInfoFolw()
        //  VM.loadShop(this)
        //  loadAdBanner()
//        VM.shareLink(this)
        statuslayout.showLoading()

    }


    /*  */
    /**
     * 是否弹出apk下载弹窗
     *//*
    private fun appDrainage() {
        val userInfo = CommonDataProvider.instance.getUserInfo()
        userInfo?.let {
            if (userInfo.tempUid != null) {
                //临时用户tempuid不为null
                VM.appDrainage(activity!!, userInfo.id!!, 1)
            } else {
                //注册用户tempuid为null，uid不为空
                VM.appDrainage(activity!!, userInfo.uid!!, 0)
            }
        }
    }*/

    override fun scrollToTop() {
        try {
            recyclerView.smoothScrollToPosition(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun changeIcon(position: Int) {
        if (position == 0) {
            setComponentState("LaunchActivity", PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            setComponentState("LaunchActivity_fake")
            setComponentState("LaunchActivity_fake2")
            setComponentState("LaunchActivity_fake3")
        } else if (position == 1) {
            setComponentState("LaunchActivity")
            setComponentState("LaunchActivity_fake", PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            setComponentState("LaunchActivity_fake2")
            setComponentState("LaunchActivity_fake3")

        } else if (position == 2) {
            setComponentState("LaunchActivity")
            setComponentState("LaunchActivity_fake")
            setComponentState("LaunchActivity_fake2", PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            setComponentState("LaunchActivity_fake3")

        } else if (position == 3) {
            setComponentState("LaunchActivity")
            setComponentState("LaunchActivity_fake")
            setComponentState("LaunchActivity_fake2")
            setComponentState("LaunchActivity_fake3", PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        }
    }

    fun setComponentState( activityName:String, enalbeDisable:Int =PackageManager.COMPONENT_ENABLED_STATE_DISABLED ) {
        activity!!.packageManager.setComponentEnabledSetting(
                ComponentName("com.due.ff.cc", "com.due.ff.cc.ui.launch.activity.$activityName"),
                enalbeDisable, PackageManager.DONT_KILL_APP)
    }

    fun copyText() {
        var myClipboard: ClipboardManager? = null
        var myClip: ClipData? = null
        myClipboard = activity!!.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager?
        myClip = ClipData.newPlainText("text", shareLink)
        myClipboard?.primaryClip = myClip

        Toast.makeText(activity!!, "复制链结成功", Toast.LENGTH_SHORT).show()
    }


    private fun initEvent() {
        RxBus.getInstance().toMainThreadObservable(this, Lifecycle.Event.ON_DESTROY)
                .subscribe {
                    if (it is EventRefreshHome) {
                        XLog.e("msg---onActivityDestroyed-进入")
                        Log.d("tag", "onActivityDestroyed进入")
                        //VM.loadShop(this)
                        // loadAdInfoFolw()
                    }
                }
    }

}
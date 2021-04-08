package com.aliee.quei.mo.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.component.CommonDataProvider.Companion.instance
import com.aliee.quei.mo.component.EventLoginSuccess
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.RecommendListBean
import com.aliee.quei.mo.data.bean.RecommendListBeanNewSkin
import com.aliee.quei.mo.net.imageloader.glide.GlideApp
import com.aliee.quei.mo.net.imageloader.glide.GlideRoundTransform
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.ui.common.adapter.ComicGrid2Holder
import com.aliee.quei.mo.ui.common.adapter.ComicGrid3Holder
import com.aliee.quei.mo.ui.common.adapter.ComicLandImgHolder
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.LogUtil
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.extention.*
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.widget.DepthScaleTransformer
import com.aliee.quei.mo.widget.FixedSpeedScroller
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.elvishew.xlog.XLog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scwang.smartrefresh.layout.util.DensityUtil
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.UltraViewPagerAdapter
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*


class ShopAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mData = mutableListOf<Any>()
    private var newData = mutableListOf<Any>()

    private var shouldShuffle = false

    init {
        shouldShuffle = true
        val installTime = SharedPreUtils.getInstance().getLong("installTime")
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val installDay = sdf.format(Date(installTime))
        val today = sdf.format(Date())
        XLog.st(1).e("installDate = $installDay;today = $today")

        if (installDay == today) {
            shouldShuffle = false
        }
    }


    companion object {
        const val VIEW_TYPE_BANNER = 0
        const val VIEW_TYPE_TITLE = 1
        const val VIEW_TYPE_ITEM_LINEAR = 3
        const val VIEW_TYPE_LAND_IMG = 4
        const val VIEW_TYPE_AD = 5

        const val VIEW_TYPE_HOTRANK = 6     //热门排行,(精品 人气 新作)
        const val VIEW_TYPE_CATEGORY = 7    //分类精选
        const val VIEW_TYPE_FREE = 8        //免费漫画推荐
        const val VIEW_TYPE_WEEKLY = 9      //每周更新
        const val VIEW_TYPE_GUESSLIKE = 10  //猜你喜欢
        const val VIEW_TYPE_BULLETIN = 11   //公告
    }

    fun setData(list: MutableList<RecommendListBean>?, adMap: MutableMap<String, AdInfo>) {
        LogUtil.e("tag", "书城：${list.toString()}")
        list ?: return
        mData.clear()
        val copyListHot : MutableList<RecommendListBean> = arrayListOf()
        val copyListLatest : MutableList<RecommendListBean> = arrayListOf()
        val copyListPop : MutableList<RecommendListBean> = arrayListOf()
        list.forEach {
            val bean = it
            if (shouldShuffle) {
                bean.list?.shuffle()
            }
            if (BeanConstants.RecommendPosition.getByRid(it.rid) == BeanConstants.RecommendPosition.BANNER) {
                //插入banner广告
                fillBannerData(adMap, bean)

                //热门推荐
                fillNewSkinHotRank(bean)
            } else {
                if (bean.list != null && bean.list.isNotEmpty()) {
                    mData.add(TitleBean(bean.rid, bean.name))
                    var max = getItemMax(bean, adMap)
                    if (bean.rid == BeanConstants.RecommendPosition.WEEK_TOP10.rid) {
                        bean.list.forEach {
                            it.showType = VIEW_TYPE_ITEM_LINEAR
                        }
                    }
                    when (bean.rid) {
                        BeanConstants.RecommendPosition.HOT_RECOMMEND.rid -> {
                            bean.list.forEach {
                                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                                it.tagText = "精品"
                                it.tagColor = Color.parseColor("#ee82ee")
                            }

//                            copyListHot.addAll(bean.list)
//                            copyListHot.forEach {
//                                it.showType = VIEW_TYPE_HOTRANK
//                            }

                        }
                        BeanConstants.RecommendPosition.LATELY_UPDATE.rid -> {
                            bean.list.forEach {
                                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                            }
                            //copyListLatest.addAll(bean.list)
                        }
                        BeanConstants.RecommendPosition.WEEK_POPULAR.rid -> {
                            var rankIndex = 0
                            var colors = intArrayOf(
                                    Color.parseColor("#f44336"),
                                    Color.parseColor("#ff9800"),
                                    Color.parseColor("#2196f3")
                            )
                            bean.list.forEach {
                                it.tagText = "NO.${rankIndex + 1}"
                                it.tagColor = colors.getOrNull(rankIndex) ?: 0
                                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                                rankIndex++
                            }
                            //copyListPop.addAll(bean.list)
                        }
                        BeanConstants.RecommendPosition.FREE.rid -> {
                            bean.list.forEach {
                                it.showType = VIEW_TYPE_LAND_IMG
                                it.tagText = "免费"
                            }
                        }
                        BeanConstants.RecommendPosition.WEEK_TOP10.rid -> {
                            var index = 0
                            bean.list.forEach {
                                val m = index % 10
                                when {
                                    m < 5 -> it.showType = VIEW_TYPE_ITEM_LINEAR //5
                                    m < 8 -> it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                                    else -> it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2
                                }
                                index++
                            }
                        }

                        else -> {
                            bean.list.forEach {
                                it.showType = VIEW_TYPE_ITEM_LINEAR
                            }
                        }
                    }

                    if (bean.list.size > max) {
                        if (max == 5) {
                            //插入信息流区块
                            val adInfo = adMap["flowObsQu"]
                            val adQu = bean.list.subList(0, max)
                            if (adInfo != null) {
                                Log.d("tag", "adInfo flowObsQu:${adInfo.toString()}")
                                val recommendBookBean = RecommendBookBean("", adInfo!!.imgurl, AdConfig.BANNER_DEF_ID, adInfo!!.desc, 0, adInfo!!.imgurl, adInfo!!.title, "")
                                recommendBookBean.adCallbackUrl = adInfo!!.callbackurl
                                recommendBookBean.adClickUrl = adInfo!!.clickurl
                                recommendBookBean.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                                adQu.add(if (adInfo.index!! > 5) 5 else adInfo.index!!, recommendBookBean)
                            }
                            mData.addAll(adQu)
                        } else {
                            mData.addAll(bean.list.subList(0, max))
                        }
                        //插入90高度的广告
                        val adInfo = adMap["flowObs90"]
                        if (adInfo != null) {
                            mData.add(adInfo!!)
                        }
                    } else {
                        if (max != 6) {
                            val adInfo = adMap["flowObsQiang"]
                            val adQiang = bean.list
                            if (adInfo != null) {
                                val recommendBookBean = RecommendBookBean("", adInfo!!.imgurl, AdConfig.BANNER_DEF_ID, adInfo.desc, 0, adInfo.imgurl, adInfo.title, "")
                                recommendBookBean.adCallbackUrl = adInfo.callbackurl
                                recommendBookBean.adClickUrl = adInfo.clickurl
                                recommendBookBean.showType = VIEW_TYPE_ITEM_LINEAR
                                for (i in 0 until adQiang.size) {
                                    if (i % (adInfo.index!! + 1) == 0) {
                                        if (adQiang[i].showType == VIEW_TYPE_ITEM_LINEAR) {
                                            adQiang.add(i, recommendBookBean)
                                        }
                                    }
                                }
                            }
                            mData.addAll(adQiang)
                        } else {
                            mData.addAll(bean.list)
                        }
                    }

                    //NEW SKIN
                    if (bean.rid == BeanConstants.RecommendPosition.HOT_RECOMMEND.rid) {
                        //mData.add(TitleBean(BeanConstants.RecommendPosition.HOTRANK_NEWSKIN.rid, BeanConstants.RecommendPosition.HOTRANK_NEWSKIN.title))
//                        bean.list.forEach {
//                            it.showType = VIEW_TYPE_HOTRANK
//                        }
//                        Log.e("tag", "NEW SKIN:" + bean.list.toString())
//                        mData.addAll(bean.list)
                    }

                }
            }
        }
        newData = mData
        notifyDataSetChanged()
    }

    private fun getItemMax(bean: RecommendListBean, adMap: MutableMap<String, AdInfo>): Int {
        return when (bean.rid) {
            BeanConstants.RecommendPosition.HOT_RECOMMEND.rid -> 9 //精品荟萃  9
            BeanConstants.RecommendPosition.LATELY_UPDATE.rid -> {
                //新书推荐 6
                if (adMap["flowObsQu"] != null) 5 else 6
            }
            BeanConstants.RecommendPosition.WEEK_POPULAR.rid -> 3 //新人 4
            BeanConstants.RecommendPosition.WEEK_TOP10.rid -> Int.MAX_VALUE //免费
            else -> 6
        }
    }

    private fun fillBannerData(adMap: MutableMap<String, AdInfo>, bean: RecommendListBean) {
        val adInfo = adMap["bannerAd"]
        if (adInfo != null) {
            val recommendBookBean = RecommendBookBean("", "", AdConfig.BANNER_DEF_ID, "", 0, adInfo!!.imgurl, "", "")
            recommendBookBean.adCallbackUrl = adInfo!!.callbackurl
            recommendBookBean.adClickUrl = adInfo!!.clickurl
            bean.list!!.add(0, recommendBookBean)
        }
        //填充数据
        mData.add(bean)
    }

    private fun fillNewSkinHotRank(bean: RecommendListBean) {
        val list2: MutableList<RecommendBookBean> = arrayListOf()
        bean.list?.forEach {

        }
        val bean2 = RecommendListBeanNewSkin(BeanConstants.RecommendPosition.HOTRANK_NEWSKIN.rid, BeanConstants.RecommendPosition.HOTRANK_NEWSKIN.title, bean.list)
        mData.add(bean2)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_AD -> {
                val v = parent.context.inflate(R.layout.item_shop_ad)
                return AdHolder(v)
            }
            VIEW_TYPE_BANNER -> {
                val v = parent.context.inflate(R.layout.item_shop_banner)
                return BannerHolder(v)
            }
            VIEW_TYPE_HOTRANK -> {
                val v = parent.context.inflate(R.layout.item_shop_hotrank)
                Log.e("TAG", "VIEW_TYPE_HOTRANK set...")
                return PagerHolder(v)
            }
            VIEW_TYPE_TITLE -> {
                val v = parent.context.inflate(R.layout.item_shop_title)
                return TitleHolder(v)
            }
            ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3 -> {
                val v = parent.context.inflate(R.layout.item_comic_grid_3)
                return ComicGrid3Holder(v)
            }
            VIEW_TYPE_LAND_IMG -> {
                val v = parent.context.inflate(R.layout.item_comic_land_img, parent, false)
                return ComicLandImgHolder(v)
            }
            ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2 -> {
                val v = parent.context.inflate(R.layout.item_comic_grid_2, parent, false)
                return ComicGrid2Holder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_comic_linear, parent, false)
                return ComicLinearHolder(v)
            }
        }
    }

    override fun getItemCount(): Int {
        return newData.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = newData[position]
        return when (item) {
            is AdInfo -> VIEW_TYPE_AD
            is RecommendListBean -> VIEW_TYPE_BANNER
            is RecommendListBeanNewSkin -> VIEW_TYPE_HOTRANK
            is TitleBean -> VIEW_TYPE_TITLE
            is RecommendBookBean -> {
                return item.showType
            }
            else -> return VIEW_TYPE_ITEM_LINEAR
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = newData.get(position)
        when (item) {
            is AdInfo -> {
                holder as AdHolder
                holder.bind(item)
            }
            is RecommendListBean -> {
                holder as BannerHolder
                holder.bind(item)
            }
            is RecommendListBeanNewSkin -> {
                holder as PagerHolder
                holder.bind(item)
            }
            is TitleBean -> {
                holder as TitleHolder
                holder.bind(item.name, item.rid)
            }
            is RecommendBookBean -> {
                if (holder is ComicGrid3Holder) {
                    holder.bind(item, itemClick)
                }
                if (holder is ComicLinearHolder) {
                    holder.bindRecommend(item, itemClick)
                }
                if (holder is ComicLandImgHolder) {
                    holder.bind(item, itemClick)
                }
                if (holder is ComicGrid2Holder) {
                    holder.bind(item, itemClick)
                }
            }
        }
    }

    var moreIndex = 0
    fun addMore(data: MutableList<RecommendBookBean>?, adMap: MutableMap<String, AdInfo>): Int? {
        data ?: return null
        XLog.st(1).e("去重前 ${data.size}")
        /* val list = data.filterNot {
             newData.contains(it)
         }*/
        //   XLog.st(1).e("去重后 ${list.size}")
        data.forEach {
            var m = moreIndex % 10
            if (m < 5) {
                it.showType = VIEW_TYPE_ITEM_LINEAR
            } else if (m < 8) {
                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
            } else {
                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_2
            }
            moreIndex++
        }
        val adInfo = adMap["flowObsQiang"]
        val adQiang = data
        if (adInfo != null) {
            val recommendBookBean = RecommendBookBean("", adInfo!!.imgurl, AdConfig.BANNER_DEF_ID, adInfo.desc, 0, adInfo.imgurl, adInfo.title, "")
            recommendBookBean.adCallbackUrl = adInfo.callbackurl
            recommendBookBean.adClickUrl = adInfo.clickurl
            recommendBookBean.showType = VIEW_TYPE_ITEM_LINEAR
            for (i in 0 until adQiang.size) {
                if (i % (adInfo.index!! + 1) == 0) {
                    if (adQiang[i].showType == VIEW_TYPE_ITEM_LINEAR) {
                        adQiang.add(i, recommendBookBean)
                    }
                }
            }
        }
        newData.addAll(adQiang)
        notifyDataSetChanged()
        return data.size
    }

    var itemClick: ((bean: RecommendBookBean) -> Unit)? = null
    var titleMoreClick: ((title: String, rid: String) -> Unit)? = null


    var MenuClick: ((counter: Int) -> Unit)? = null
    var ShareClick: ((counter: Int) -> Unit)? = null

    inner class AdHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val adImage = itemView.find<ImageView>(R.id.iv_shop_ad)
        private val ad_iv_close = itemView.find<ImageView>(R.id.ad_iv_close)
        fun bind(adInfo: AdInfo) {
            AdConfig.adPreview(adInfo!!.callbackurl!!)
            adImage.loadHtmlImg(adInfo!!.imgurl)
            if (AdConfig.isClosed(adInfo.isClose!!)) {
                ad_iv_close.show()
            } else {
                ad_iv_close.gone()
            }
            ad_iv_close.click {
                newData.remove(adInfo)
                notifyDataSetChanged()
            }
            adImage.click {
                AdConfig.adClick(adImage.context, adInfo!!.clickurl!!)
            }
        }
    }

    class HotRankAdapter(private val datas: List<String>) : RecyclerView.Adapter<HotRankAdapter.BaseViewHolder?>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_one_list, parent, false)
            Log.e("tag", "HotRankAdapter onCreateViewHolder")
            return BaseViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
            holder.tv.text = datas.get(position)
            Log.e("tag", "HotRankAdapter onBindViewHolder")
        }

        override fun getItemCount(): Int {
            return 3
        }

        inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv: TextView = itemView.findViewById(R.id.tv)
        }

    }

    inner class PagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val shopPager = itemView.find<ViewPager2>(R.id.shop_pager)
        private val tabLayout = itemView.find<com.google.android.material.tabs.TabLayout>(R.id.shop_tabLayout)


        @SuppressLint("CheckResult")
        fun bind(recommendListBean: RecommendListBeanNewSkin) {
            Log.e("TAG", "PagerHolder bind: size=${recommendListBean.list?.size}")

            with(shopPager) {
                adapter = HotRankAdapter(listOf("111", "222", "333"))
                isUserInputEnabled = true // 禁止滚动true为可以滑动false为禁止
                orientation = ViewPager2.ORIENTATION_HORIZONTAL // 设置垂直滚动ORIENTATION_VERTICAL
                setCurrentItem(1, true) //切换到指定页，是否展示过渡中间页
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                    ) {
                        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                        Log.e("TAG", "onPageScrolled: $position--->$positionOffset--->$positionOffsetPixels"
                        )
                    }

                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        Log.e("TAG", "onPageSelected: $position")
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                        Log.e("TAG", "onPageScrollStateChanged: $state")
                    }
                }) // 监听滑动
            }


            TabLayoutMediator(tabLayout, shopPager) { tab, position ->
                tab.text = when (position) {
                    0 -> "精品"
                    1 -> "人气"
                    2 -> "新作"
                    else -> null
                }
            }.attach()

//            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab) {
//                    if(tab.isSelected) {
//                        tab.setBackgroundResource(R.drawable.bg_tab_select)
//                    } else {
//                        (tab as TextView).setBackgroundColor(Color.WHITE)
//                    }
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab) {}
//                override fun onTabReselected(tab: TabLayout.Tab) {}
//            })

        }


    }

    fun View.getColor(resId: Int):Int {
        return ContextCompat.getColor(this.context, resId)
    }

    inner class BannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pager = itemView.find<UltraViewPager>(R.id.pager)
        //private val categoryBtn = itemView.find<View>(R.id.category)
        private val tvHomePage = itemView.find<View>(R.id.tvHomePage)
        private val tvHomeSerial = itemView.find<View>(R.id.tvHomeSerial)
        private val tvHomeFinish = itemView.find<View>(R.id.tvHomeFinish)
        private val tvHomeRanking = itemView.find<View>(R.id.tvHomeRanking)
        private val tvHomeJapan = itemView.find<View>(R.id.tvHomeJapan)
        private val tvHomeBulletin = itemView.find<View>(R.id.tvHomeBulletin)
        private val tvHomeFree = itemView.find<View>(R.id.tvHomeFree)
        private val tvHomeRecharge = itemView.find<View>(R.id.tvHomeRecharge)
        private var tvShare = itemView.find<View>(R.id.tvShare)
        private var ivSearch = itemView.find<View>(R.id.ivSearch)
        private var ivModify = itemView.find<View>(R.id.ivModify)
        //private val cateFree = itemView.find<View>(R.id.cateFree)
        private var indicatorColorSelected = itemView.getColor(R.color.indicator_shelf_banner_s)
        private var indicatorColorNormal = itemView.getColor(R.color.indicator_shelf_banner_n)
//        lateinit var arrayAdapter: ArrayAdapter<*>

        var str = arrayOf("图标1", "图标2", "图标3")

        @SuppressLint("CheckResult")
        fun bind(recommendListBean: RecommendListBean) {
            Log.e("TAG", "BannerHolder bind...")
            pager.adapter = UltraViewPagerAdapter(BannerAdapter(recommendListBean.list
                    ?: mutableListOf()))
            pager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
            pager.initIndicator()
            pager.indicator.setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                    .setFocusColor(indicatorColorSelected)
                    .setNormalColor(indicatorColorNormal)
                    .setRadius(DensityUtil.dp2px(3f))
                    .setGravity(Gravity.END or Gravity.BOTTOM)
                    .setMargin(0, 0, DensityUtil.dp2px(10F), DensityUtil.dp2px(10F))
                    .build()
            pager.setPageTransformer(true, DepthScaleTransformer())
            pager.setInfiniteLoop(true)
            pager.setAutoScroll(3000)
            try {
                val field = ViewPager::class.java.getDeclaredField("mScroller")
                field.isAccessible = true
                val scroller = FixedSpeedScroller(
                        itemView.context,
                        AccelerateInterpolator(),
                        500
                )
                field.set(pager.viewPager, scroller)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            tvHomeRanking.click {
                ARouterManager.goRankActivity(it.context)
            }
            tvHomeFree.click {
                ARouterManager.goMoreActivity(
                        it.context,
                        BeanConstants.RecommendPosition.FREE.title,
                        BeanConstants.RecommendPosition.FREE.rid
                )
            }
//            categoryBtn.click {
//                ARouterManager.goComicCategoryActivity(it.context)
//            }
            tvHomeRecharge.click {
                ARouterManager.goRechargeActivity(it.context, "", 0)
            }

            tvHomeBulletin.click {
                ARouterManager.goBulletinActivity(it.context)
            }

            RxBus.getInstance().toMainThreadObservable(ReaderApplication.instance, Lifecycle.Event.ON_DESTROY).subscribe({
                when (it) {
                    is EventLoginSuccess -> {
                        val tlength = instance.getToken()
                        if (tlength.length == 11) {
                            tvShare.visibility = View.INVISIBLE
                        }
                    }

                }
            }, {
                it.printStackTrace()
            })

            val tlength = instance.getToken()
            if (tlength.length > 11) {
                tvShare.visibility = View.INVISIBLE
            }
            tvShare.click {
                ShareClick?.invoke(1)
            }
            ivSearch.click {
                ARouterManager.goSearch(it.context)
            }
            ivModify.click {
                MenuClick?.invoke(1)
            }
        }
    }

    inner class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle = itemView.find<TextView>(R.id.text)
        private val more = itemView.find<TextView>(R.id.more)
        fun bind(title: String, rid: String) {
            tvTitle.text = title
            more.click {
                titleMoreClick?.invoke(title, rid)
            }
        }
    }

    data class TitleBean(val rid: String, val name: String)

    inner class BannerAdapter(val list: List<RecommendBookBean>) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val bean = list[position]
            val v = container.context.inflate(R.layout.item_banner, container, false)
            val cover = v.find<ImageView>(R.id.imageView)
            if (bean.id == AdConfig.BANNER_DEF_ID) {
                AdConfig.adPreview(bean.adCallbackUrl!!)
                GlideApp.with(cover.context).load(bean.thumb).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .apply(RequestOptions.bitmapTransform(GlideRoundTransform(cover.context, 120)))
                        .centerCrop().into(cover)
            } else {
                cover.loadNovelCover(bean.thumb)
            }

            container.addView(v)
            v.click {
                if (bean.id == AdConfig.BANNER_DEF_ID) {
                    AdConfig.adClick(v.context, bean.adClickUrl!!)
                } else {
                    itemClick?.invoke(bean)
                }
            }
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            `object` as View
            container.removeView(`object`)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return if (list.size > 6) 6 else list.size
        }
    }
}

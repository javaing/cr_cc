package com.aliee.quei.mo.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.component.CommonDataProvider.Companion.instance
import com.aliee.quei.mo.component.EventLoginSuccess
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.*
import com.aliee.quei.mo.net.imageloader.glide.GlideApp
import com.aliee.quei.mo.net.imageloader.glide.GlideRoundTransform
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.common.ShopItemDecoration
import com.aliee.quei.mo.ui.common.adapter.ComicGrid2Holder
import com.aliee.quei.mo.ui.common.adapter.ComicGrid3Holder
import com.aliee.quei.mo.ui.common.adapter.ComicLandImgHolder
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.ComicUtils
import com.aliee.quei.mo.utils.LogUtil
import com.aliee.quei.mo.utils.SharedPreUtils
import com.aliee.quei.mo.utils.extention.*
import com.aliee.quei.mo.utils.rxjava.RxBus
import com.aliee.quei.mo.widget.DepthScaleTransformer
import com.aliee.quei.mo.widget.FixedSpeedScroller
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.elvishew.xlog.XLog
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.haozhang.lib.SlantedTextView
import com.scwang.smartrefresh.layout.util.DensityUtil
import com.tmall.ultraviewpager.UltraViewPager
import com.tmall.ultraviewpager.UltraViewPagerAdapter
import org.jetbrains.anko.*
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
        const val VIEW_TYPE_AUTOROTATE = 7    //免费漫画推荐

    }

    fun setData(list: MutableList<RecommendListBean>?, adMap: MutableMap<String, AdInfo>) {
        LogUtil.e("tag", "书城：${list.toString()}")
        list ?: return
        mData.clear()

        //处理Banner和热门排行
        val listMap: MutableMap<Int, List<RecommendBookBean>> = mutableMapOf()
        var beanFree: RecommendListAutoBean?=null
        list.forEach {
            val bean = it
            if (shouldShuffle) {
                bean.list?.shuffle()
            }
            if (BeanConstants.RecommendPosition.getByRid(it.rid) == BeanConstants.RecommendPosition.BANNER) {
                //插入banner广告
                fillBannerData(adMap, bean)
            } else {
                if (bean.list != null && bean.list.isNotEmpty()) {

                    when (bean.rid) {
                        BeanConstants.RecommendPosition.HOT_RECOMMEND.rid -> {
                            bean.list.let { listMap.put(0, it.toList()) }
                        }
                        BeanConstants.RecommendPosition.WEEK_POPULAR.rid -> {
                            bean.list.let { listMap.put(1, it.toList()) }
                        }
                        BeanConstants.RecommendPosition.LATELY_UPDATE.rid -> {
                            bean.list.let { listMap.put(2, it.toList()) }
                        }
                        BeanConstants.RecommendPosition.FREE.rid -> {
                            bean.list.forEach {
                                it.showType = VIEW_TYPE_LAND_IMG
                                it.tagText = "免费"
                            }
                            beanFree = RecommendListAutoBean(BeanConstants.RecommendPosition.FREE.rid, BeanConstants.RecommendPosition.FREE.title, bean.list)
                        }
                    }
                }

            }
        }
        val beanHot = RecommendListBeanNewSkin(BeanConstants.RecommendPosition.HOTRANK_NEWSKIN.rid, BeanConstants.RecommendPosition.HOTRANK_NEWSKIN.title, listMap)
        mData.add(beanHot)

        //Rotate type
        beanFree?.let {
            val titleBean = TitleBean(it.rid, it.name)
            titleBean.name = "免费漫画推荐"
            titleBean.resId = R.mipmap.list_icon_03
            mData.add(titleBean)
            mData.add(it)
        }


        list.forEach {
            val bean = it
            if (shouldShuffle) {
                bean.list?.shuffle()
            }
            if (BeanConstants.RecommendPosition.getByRid(it.rid) == BeanConstants.RecommendPosition.BANNER) {
                //插入banner广告
                //fillBannerData(adMap, bean)

            } else {
                if (bean.list != null && bean.list.isNotEmpty()) {

                    if(bean.name.contains("精品")||bean.name.contains("新书")||
                            bean.rid==BeanConstants.RecommendPosition.FREE.rid) {

                    } else {

                        //Title处理
                        val titleBean = TitleBean(bean.rid, bean.name)
                        when(bean.rid) {
//                            BeanConstants.RecommendPosition.FREE.rid -> {
//                                //bean.rid = VIEW_TYPE_AUTOROTATE.toString()
//                                titleBean.name = "免费漫画推荐"
//                                titleBean.resId = R.mipmap.list_icon_03
//                            }
                            BeanConstants.RecommendPosition.WEEK_POPULAR.rid -> {
                                titleBean.resId = R.mipmap.list_icon_04
                                titleBean.desc = "每24小时更新漫画!"
                            }
                            BeanConstants.RecommendPosition.GUESS_LIKE.rid -> {
                                titleBean.resId = R.mipmap.list_icon_05
                                titleBean.desc = "时间久了总要换换口味~"
                            }
                            else -> {}
                        }
                        mData.add(titleBean)



                        var max = getItemMax(bean, adMap)
//                        if (bean.rid == BeanConstants.RecommendPosition.WEEK_TOP10.rid) {
//                            bean.list.forEach {
//                                it.showType = VIEW_TYPE_ITEM_LINEAR
//                            }
//                        }
                        when (bean.rid) {
                            BeanConstants.RecommendPosition.HOT_RECOMMEND.rid -> {
                                bean.list.forEach {
                                    it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                                    it.tagText = "精品"
                                    it.tagColor = Color.parseColor("#ee82ee")
                                }
                            }
                            BeanConstants.RecommendPosition.LATELY_UPDATE.rid -> {
                                bean.list.forEach {
                                    it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                                }
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
            BeanConstants.RecommendPosition.GUESS_LIKE.rid,
            BeanConstants.RecommendPosition.WEEK_POPULAR.rid -> 3
            BeanConstants.RecommendPosition.WEEK_TOP10.rid -> Int.MAX_VALUE
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
                return HotRankPagerHolder(v)
            }
            VIEW_TYPE_AUTOROTATE -> {
                val v = parent.context.inflate(R.layout.item_shop_autororate, parent, false)
                return AutoRotateHolder(v)
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
                //val v = parent.context.inflate(R.layout.item_comic_land_img, parent, false)
                //return ComicLandImgHolder(v)
                val v = parent.context.inflate(R.layout.item_shop_autororate, parent, false)
                return AutoRotateHolder(v)
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
            is RecommendListAutoBean -> VIEW_TYPE_AUTOROTATE
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
            is RecommendListAutoBean -> {
                holder as AutoRotateHolder
                holder.bind(item, itemClick)
            }
            is RecommendListBeanNewSkin -> {
                holder as HotRankPagerHolder
                holder.bind(item, itemClick)
            }
            is TitleBean -> {
                holder as TitleHolder
                holder.bind(item)
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


    inner class AutoRotateHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pager = itemView.find<UltraViewPager>(R.id.pager_autorotate)


        @SuppressLint("CheckResult")
        fun bind(dataBean: RecommendListAutoBean, itemClick: ((bean: RecommendBookBean) -> Unit)?) {
            pager.adapter = UltraViewPagerAdapter(AutoRotateAdapter(dataBean.list?: mutableListOf()))
            pager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL)
            pager.initIndicator()
//            pager.indicator.setOrientation(UltraViewPager.Orientation.HORIZONTAL)
//                    .setFocusColor(indicatorColorSelected)
//                    .setNormalColor(indicatorColorNormal)
//                    .setRadius(DensityUtil.dp2px(3f))
//                    .setGravity(Gravity.END or Gravity.BOTTOM)
//                    .setMargin(0, 0, DensityUtil.dp2px(10F), DensityUtil.dp2px(10F))
//                    .build()
            pager.setInfiniteLoop(true)
            pager.setAutoScroll(3000)



        }
    }

    inner class HotRankPagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val shopPager = itemView.find<ViewPager2>(R.id.shop_pager)
        private val tabLayout = itemView.find<TabLayout>(R.id.shop_tabLayout)
        private val imgHotBanner = itemView.find<ImageView>(R.id.img_hot_banner)
        private val flexHotCategory = itemView.find<FlexboxLayout>(R.id.flexHotCategory)
        private val img_hot_up = itemView.find<ImageView>(R.id.img_hot_up)


        @SuppressLint("CheckResult")
        fun bind(dataBean: RecommendListBeanNewSkin, itemClick: ((bean: RecommendBookBean) -> Unit)?) {
            //Log.e("TAG", "PagerHolder bind: size=${dataBean.list?.size}")

            imgHotBanner.click {
                ARouterManager.goRechargeActivity(it.context, "", 0)
            }
            addLayouts()

            with(shopPager) {
                adapter = HotRankAdapter(dataBean.listMap, itemClick)
                isUserInputEnabled = true // 禁止滚动true为可以滑动false为禁止
                orientation = ViewPager2.ORIENTATION_HORIZONTAL // 设置垂直滚动ORIENTATION_VERTICAL
                //setCurrentItem(1, true) //切换到指定页，是否展示过渡中间页
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
        }


        fun addLayouts() {
            val params = FlexboxLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.flexBasisPercent = 0.22f
            params.margin= 6

            flexHotCategory.removeAllViews();
            val categoryBean = CommonDataProvider.instance.categoryConfig
            categoryBean?.forEachIndexed { index, bean ->
                val tv = TextView(flexHotCategory.context, null, 0, ComicUtils.catalog_tags[index % ComicUtils.catalog_tags.size ])
                tv.text = bean.typename
                tv.tag = bean
                flexHotCategory.addView(tv , params);

                tv.click {
                    it.tag?:return@click
                    //http://api.7775367.com/2/cartoon/chapter/catalog
                    //ARouterManager.goComicCatalogActivity(it.context,(it.tag as CategoryBean).id )

                    //
                    ARouterManager.goComicCategoryActivity(it.context )
                }
            }

            img_hot_up.click {
                val params = flexHotCategory.layoutParams
                if(params.height == img_hot_up.getDimensionInt(R.dimen.catalog_unfold)) {
                    flexHotCategory.setHeightByDimension(R.dimen.catalog_fold)
                    img_hot_up.setImageResource(android.R.drawable.arrow_down_float)
                } else {
                    flexHotCategory.setHeightByDimension(R.dimen.catalog_unfold)
                    img_hot_up.setImageResource(android.R.drawable.arrow_up_float)
                }
            }
            //changeFlexboxHeight(180)
        }

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
        private val textDesc = itemView.find<TextView>(R.id.text_desc)
        private val more = itemView.find<TextView>(R.id.more)
        fun bind(bean: TitleBean) {
            tvTitle.text = bean.name
            if(bean.desc!=null) {
                textDesc.visibility=VISIBLE
                textDesc.text = bean.desc
            }
            if(bean.resId!=null) {
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(bean.resId!!,0,0,0)
            }

            more.click {
                titleMoreClick?.invoke(bean.name, bean.rid)
            }
        }
    }

    data class TitleBean(val rid: String, var name: String, var desc: String?=null, var resId: Int?=null)

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

    inner class AutoRotateAdapter(val list: List<RecommendBookBean>) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val bean = list[position]
            val v = container.context.inflate(R.layout.item_comic_land_img, container, false)

            val ivLand = v.find<ImageView>(R.id.ivLand)
            val tagRight = v.find<SlantedTextView>(R.id.tagRight)
            val tvStatus = v.find<TextView>(R.id.tvTagStatus)
            val tvViewNum = v.find<TextView>(R.id.tvViewNum)
            val tvStar = v.find<TextView>(R.id.tvStar)
            val tvDescr = v.find<TextView>(R.id.tvDescr)
            val tvTitle = v.find<TextView>(R.id.tvTitle)

            ivLand.loadNovelCover(if (bean.thumb.isNullOrEmpty()) bean.bookcover else bean.thumb)
            tagRight.text = bean.tagText
            tvDescr.text = bean.desc
            tvDescr.visibility= GONE
            tvTitle.text = bean.title
            if (bean.status == BeanConstants.STATUS_FINISH) {
                tvStatus.text = "完结"
                tvStatus.setBackgroundResource(R.mipmap.bg_tag_status_finish)
            } else {
                tvStatus.text = "连载"
                tvStatus.setBackgroundResource(R.mipmap.bg_tag_status_ongoing)
            }
            v.click {
                itemClick?.invoke(bean)
            }
            tvViewNum.text = ComicUtils.getReadNum(bean.id).toString()
            tvStar.text = ComicUtils.getCommentStar(bean.id)

            container.addView(v)
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
            return list.size
        }
    }


}

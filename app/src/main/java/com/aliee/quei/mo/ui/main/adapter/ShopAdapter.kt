package com.aliee.quei.mo.ui.main.adapter

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import android.graphics.Color
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.application.ReaderApplication
import com.aliee.quei.mo.component.CommonDataProvider.Companion.instance
import com.aliee.quei.mo.component.EventLoginSuccess
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.RecommendListBean
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.elvishew.xlog.XLog
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
    }

    fun setData(list: MutableList<RecommendListBean>?, adMap: MutableMap<String, AdInfo>) {
        LogUtil.e("tag", "?????????${list.toString()}")
        list ?: return
        mData.clear()
        list.forEach {
            val bean = it
            if (shouldShuffle) {
                bean.list?.shuffle()
            }
            if (BeanConstants.RecommendPosition.getByRid(it.rid) == BeanConstants.RecommendPosition.BANNER) {
                //??????banner??????
                val adInfo = adMap["bannerAd"]
                if (adInfo != null) {
                    val recommendBookBean = RecommendBookBean("", "", AdConfig.BANNER_DEF_ID, "", 0, adInfo.imgurl, "", "")
                    recommendBookBean.adCallbackUrl = adInfo.callbackurl
                    recommendBookBean.adClickUrl = adInfo.clickurl
                    bean.list!!.add(0, recommendBookBean)
                }
                //????????????
                mData.add(bean)
            } else {
                if (bean.list != null && bean.list.isNotEmpty()) {
                    mData.add(TitleBean(bean.rid, bean.name))
                    var max = 6
                    when (bean.rid) {
                        BeanConstants.RecommendPosition.HOT_RECOMMEND.rid -> max = 9 //????????????  9
                        BeanConstants.RecommendPosition.LATELY_UPDATE.rid -> {
                            //???????????? 6
                            val adInfo = adMap["flowObsQu"]
                            max = if (adInfo != null) {
                                5
                            } else {
                                6
                            }
                        }
                        BeanConstants.RecommendPosition.WEEK_POPULAR.rid -> max = 3 //?????? 4
                        BeanConstants.RecommendPosition.WEEK_TOP10.rid -> max = Int.MAX_VALUE //??????
                    }
                    if (bean.rid == BeanConstants.RecommendPosition.WEEK_TOP10.rid) {
                        bean.list.forEach {
                            it.showType = VIEW_TYPE_ITEM_LINEAR
                        }
                    }
                    when (bean.rid) {
                        BeanConstants.RecommendPosition.HOT_RECOMMEND.rid -> {
                            bean.list.forEach {
                                it.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                                it.tagText = "??????"
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
                                it.tagText = "??????"
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
                            //?????????????????????
                            val adInfo = adMap["flowObsQu"]
                            val adQu = bean.list.subList(0, max)
                            if (adInfo != null) {
                                Log.d("tag", "adInfo flowObsQu:$adInfo")
                                val recommendBookBean = RecommendBookBean("", adInfo.imgurl, AdConfig.BANNER_DEF_ID, adInfo.desc, 0, adInfo.imgurl, adInfo.title, "")
                                recommendBookBean.adCallbackUrl = adInfo.callbackurl
                                recommendBookBean.adClickUrl = adInfo.clickurl
                                recommendBookBean.showType = ShopItemDecoration.VIEW_TYPE_COMIC_GRID_3
                                adQu.add(if (adInfo.index!! > 5) 5 else adInfo.index!!, recommendBookBean)
                            }
                            mData.addAll(adQu)
                        } else {
                            mData.addAll(bean.list.subList(0, max))
                        }
                        //??????90???????????????
                        val adInfo = adMap["flowObs90"]
                        if (adInfo != null) {
                            mData.add(adInfo)
                        }
                    } else {
                        if (max != 6) {
                            val adInfo = adMap["flowObsQiang"]
                            val adQiang = bean.list
                            if (adInfo != null) {
                                val recommendBookBean = RecommendBookBean("", adInfo.imgurl, AdConfig.BANNER_DEF_ID, adInfo.desc, 0, adInfo.imgurl, adInfo.title, "")
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
        newData = mData
        Log.e("ShopAdapter", "setData")
        notifyDataSetChanged()

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
        XLog.st(1).e("????????? ${data.size}")
        /* val list = data.filterNot {
             newData.contains(it)
         }*/
        //   XLog.st(1).e("????????? ${list.size}")
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
            val recommendBookBean = RecommendBookBean("", adInfo.imgurl, AdConfig.BANNER_DEF_ID, adInfo.desc, 0, adInfo.imgurl, adInfo.title, "")
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
            AdConfig.adPreview(adInfo.callbackurl)
            adImage.loadHtmlImg(adInfo.imgurl)
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
                AdConfig.adClick(adImage.context, adInfo.clickurl)
            }
        }
    }

    inner class BannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pager = itemView.find<UltraViewPager>(R.id.pager)
        private val categoryBtn = itemView.find<View>(R.id.category)
        private val rankBtn = itemView.find<View>(R.id.rank)
        private val recharge = itemView.find<View>(R.id.recharge)
        private val bulletin = itemView.find<View>(R.id.bulletin)
        private var tvShare = itemView.find<View>(R.id.tvShare)
        private var ivSearch = itemView.find<View>(R.id.ivSearch)
        private var ivModify = itemView.find<View>(R.id.ivModify)
        private val cateFree = itemView.find<View>(R.id.cateFree)
        private var indicatorColorSelected = itemView.context.resources.getColor(R.color.indicator_shelf_banner_s)
        private var indicatorColorNormal = itemView.context.resources.getColor(R.color.indicator_shelf_banner_n)
//        lateinit var arrayAdapter: ArrayAdapter<*>

        var str = arrayOf("??????1", "??????2", "??????3")

        @SuppressLint("CheckResult")
        fun bind(recommendListBean: RecommendListBean) {
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

            rankBtn.click {
                ARouterManager.goRankActivity(it.context)
            }
            cateFree.click {
                ARouterManager.goMoreActivity(
                        it.context,
                        BeanConstants.RecommendPosition.FREE.title,
                        BeanConstants.RecommendPosition.FREE.rid
                )
            }
            categoryBtn.click {
                ARouterManager.goComicCategoryActivity(it.context)
            }
            recharge.click {
                ARouterManager.goRechargeActivity(it.context, "", 0)
            }

            bulletin.click {
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
                        .centerInside().into(cover)
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

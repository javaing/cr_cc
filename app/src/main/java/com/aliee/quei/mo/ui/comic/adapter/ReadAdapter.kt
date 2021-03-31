package com.aliee.quei.mo.ui.comic.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.elvishew.xlog.XLog
import com.aliee.quei.mo.R
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.utils.ScreenUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import org.jetbrains.anko.find


class ReadAdapter constructor(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val imgs = mutableListOf<String>()
    private val screenWidth: Int
    private val screenHeight: Int

    private var mPrice: Int? = 0
    private var mBalance: Int? = 0
    private var isPay: Int? = 0

    var isLoading = true

    var readmodes: String? = null

    companion object {
        const val VIEW_TYPE_IMG = 0
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_RECOMMEND = 2
        const val VIEW_TYPE_AD = 3
    }

    init {
        val p = ScreenUtils.getScreenSize(context as Activity)
        screenWidth = p.x
        screenHeight = p.y
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_IMG -> {
                val v = parent.context.inflate(R.layout.item_content_img, parent)
                return ImageHolder(v)
            }
            VIEW_TYPE_LOADING -> {
                val v = parent.context.inflate(R.layout.item_comic_loading, parent)
                return ComicLoadingHolder(v)
            }
            VIEW_TYPE_RECOMMEND -> {
                val v = parent.context.inflate(R.layout.item_chapter_end_recommend, parent)
                return RecommendHolder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.layout_read_ad, parent)
                return ComicAdHolder(v)
            }
        }
    }

    fun isPay(code: Int?) {
        isPay = code
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (position < imgs.size) return VIEW_TYPE_IMG
        if (isLoading) return VIEW_TYPE_LOADING
        if (position == imgs.size - 1) return VIEW_TYPE_AD
        else return VIEW_TYPE_RECOMMEND
    }

    override fun getItemCount(): Int {
        if (isPay == 1009) {
            return 1
        }
        if (isLoading) return imgs.size

        return imgs.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            VIEW_TYPE_IMG -> {
                holder as ImageHolder
                holder.bind(imgs.getOrNull(position), position)
            }
        }
        if (holder is RecommendHolder) {
            holder.bind()
        }
    }

    fun addReadmode(readmode: String?) {
        Log.d("ReadAdapter:", "ReadMode:" + readmode)
        readmodes = readmode
    }

    fun addImages(list: List<String>?) {
        list ?: return
        val start = imgs.size
        imgs.addAll(list)
        notifyItemRangeInserted(start, list.size)
    }

    private var adInfo: AdInfo? = null
    private var adIndex: Int? = null
    fun insertAd(index: Int, adInfo: AdInfo) {
        this.adInfo = adInfo
        this.adIndex = index
        this.imgs.add(index, adInfo.imgurl)
        notifyDataSetChanged()
    }


    fun setChapterPrice(price: Int?) {
        mPrice = price
        notifyDataSetChanged()
    }

    fun setUserBalance(balance: Int?) {
        mBalance = balance
        notifyDataSetChanged()
    }

    fun clear() {
        imgs.clear()
        notifyDataSetChanged()
    }

    fun getPreloadItems(position: Int): MutableList<String> {
        return imgs.subList(position, position + 1)
    }

    fun downloadFinish() {
        isLoading = false
//        notifyDataSetChanged()
        notifyItemInserted(imgs.size)
    }

    fun downloadStart() {
        isLoading = true
        notifyDataSetChanged()
    }

    private val mRecommend = mutableListOf<RecommendBookBean>()

    var nextChapterClick: (() -> Unit)? = null
    var onRechargeClick: (() -> Unit)? = null

    fun setRecommendBottom(data: List<RecommendBookBean>?) {
        data ?: return
        this.mRecommend.clear()
        this.mRecommend.addAll(data)
    }

    inner class ComicAdHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    inner class ComicLoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class RecommendHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView = itemView.find<RecyclerView>(R.id.recyclerView)
        val btnNextChapter = itemView.find<TextView>(R.id.btnNextChapter)
        private var subAdapter = ChpaterEndRecommendAdapter()
        val dp1 = ScreenUtils.dpToPx(1)

        init {
            subAdapter.setHasStableIds(true)
            recyclerView.adapter = subAdapter
            recyclerView.layoutManager = GridLayoutManager(itemView.context, 3)
            recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                    val column = itemPosition % 3
                    when (column) {
                        0 -> outRect?.right = dp1 * 2
                        1 -> {
                            outRect?.right = dp1
                            outRect?.left = dp1
                        }
                        2 -> outRect?.left = dp1 * 2
                    }
                }
            })
        }

        fun bind() {
            mRecommend.shuffle()
            if (mRecommend.size > 3) {
                subAdapter.setData(mRecommend.subList(0, 3))
            } else {
                subAdapter.setData(mRecommend)
            }
            btnNextChapter.click { nextChapterClick?.invoke() }
        }
    }

    inner class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image = itemView.find<ImageView>(R.id.iv_page)
        private val iv_read_ad_close = itemView.find<ImageView>(R.id.iv_read_ad_close)
        private val tv_num = itemView.find<TextView>(R.id.tv_num)
        private val view_readmode = itemView.find<View>(R.id.view_readmode)
        private val ll_pay = itemView.find<LinearLayout>(R.id.pay_layout)
        private val tv_recharge = itemView.find<TextView>(R.id.tv_recharge)
        private val tv_price = itemView.find<TextView>(R.id.tv_price)
        private val tv_balance = itemView.find<TextView>(R.id.tv_balance)
        private val tv_vip_tip = itemView.find<TextView>(R.id.tv_vip_tip)

        fun bind(url: String?, position: Int) {
            image.layoutParams.width = screenWidth
            image.layoutParams.width = screenHeight
//            image.setImageResource(R.mipmap.img_default_cover)
            tv_num.text = "${position + 1}"
            tv_num.visibility = View.VISIBLE


            /*tv_price.text = "本章价格：${mPrice}书币"
            tv_balance.text = "您的余额：${mBalance}书币"

            tv_recharge.click { onRechargeClick?.invoke() }
            tv_vip_tip.click { onRechargeClick?.invoke() }*/
            //  Log.e("图片地址L------", url)

            if (isPay == 1009) {
                image.layoutParams.width = screenWidth
                tv_num.visibility = View.GONE
            } else {
                tv_num.visibility = View.VISIBLE
            }
            if (readmodes.equals("2.0")) {
                view_readmode.visibility = View.VISIBLE
            }


            Log.d("tag", "imageUrl:${CommonDataProvider.instance.getImgDomain() + "/" + url}")
            var imgUrl: String = ""
            imgUrl = if (url!!.contains("http")) {
                iv_read_ad_close.visibility = View.VISIBLE
                AdConfig.adPreview(adInfo!!.callbackurl)
                url
            } else {
                iv_read_ad_close.visibility = View.GONE
                CommonDataProvider.instance.getImgDomain() + "/" + url
            }

            Glide.with(image)
                    .asDrawable()
                    .load(imgUrl)
                    //.load("http://pic.hawzh.com/$url")
                    .apply(
                            RequestOptions()
                                    .override(screenWidth + 2, Short.MAX_VALUE.toInt())
                                    .downsample(DownsampleStrategy.CENTER_INSIDE)
                                    .error(R.mipmap.img_default_cover)
                                    .placeholder(R.mipmap.img_default_cover)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    )
                    .into(object : SimpleTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {

                            try {
                                val height = resource.intrinsicHeight
                                val width = resource.intrinsicWidth
                                val r = screenWidth * 1.0f / width
                                tv_num.layoutParams.width = (height * r).toInt()
                                tv_num.layoutParams.height = screenHeight
                                var animation = AlphaAnimation(1f, 0f)
                                animation.duration = 100
                                tv_num.startAnimation(animation)
                                tv_num.visibility = View.GONE


                                image.layoutParams.height = (height * r).toInt()
                                image.layoutParams.width = screenWidth
                                image.setImageDrawable(resource)


                            } catch (e: Exception) {
                                XLog.e("图片加载错误::-->${url}")
                                XLog.st(1).e("url = $url")
                                e.printStackTrace()
                            }
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            XLog.e("图片加载错误::-onLoadFailed->${url}")
                        }
                    })


            image.click {
                if (url.contains("http")) {
                    AdConfig.adClick(itemView.context, adInfo!!.clickurl)
                }
            }
            iv_read_ad_close.click {
                imgs.removeAt(adIndex!!)
                notifyItemChanged(position)
            }
        }
    }
}
package com.aliee.quei.mo.ui.main.adapter

import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.dueeeke.videoplayer.util.PlayerUtils
import com.aliee.quei.mo.R
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.bean.AdInfo
import com.aliee.quei.mo.data.bean.Tag
import com.aliee.quei.mo.data.bean.Tags
import com.aliee.quei.mo.data.bean.Video
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.ui.video.view.PrepareView
import com.aliee.quei.mo.utils.extention.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import org.jetbrains.anko.find

class MainVideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mData = mutableListOf<Video>()
    private var url: String? = null

    companion object {
        //    const val VIEW_TYPE_ENTER = 0
        const val VIEW_TYPE_VIDEO = 1
        const val VIEW_TYPE_VIDEO_AD = 2
    }


    fun getData(): MutableList<Video> {
        /* val videos = mData
         if (videos[0].title == "视频圈子") {
             //移除头部的数据
             videos.removeAt(0)
         }
         return videos*/
        return mData
    }

    fun setVideoThumbDomain(url: String) {
        this.url = url
    }


    fun loadMore(videos: MutableList<Video>) {
        mData.addAll(videos)
        /*videos.forEach {
            it.type = VIEW_TYPE_VIDEO
        }*/
        notifyItemRangeChanged(mData.size, mData.size);
    }

    fun setData(tag: Tag, videos: MutableList<Video>) {
        Log.d("tag", "videos :${videos.toString()}")
        mData.clear()
        mData = videos
        notifyDataSetChanged()
    }

    fun insertAd(index: Int, adInfo: AdInfo) {
        val adVideo = Video()
        adVideo.thumbImg = adInfo.imgurl
        adVideo.name = adInfo.title
        adVideo.adCallbackUrl = adInfo.callbackurl
        adVideo.adClickUrl = adInfo.clickurl
        this.mData.add(index, adVideo)
        notifyDataSetChanged()
    }

    fun setCollectionStatus(position: Int) {
        val video = mData[position]
        video.isFav = 1
        notifyItemRangeChanged(position, mData.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            /*VIEW_TYPE_ENTER -> {
                val v = parent.context.inflate(R.layout.video_enter_layout)
                EnterHolder(v)
            }*/
            VIEW_TYPE_VIDEO_AD -> {
                val v = parent.context.inflate(R.layout.item_video_ad)
                AdHolder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_video)
                VideoHolder(v)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = mData[position]
        /*  val holder1 = holder as VideoHolder
           holder1.bind(mData[position], position)*/
        when (mData[position].type) {
            /*VIEW_TYPE_ENTER -> {
                holder as EnterHolder
                holder.bind(mData[position].title)
            } */

            VIEW_TYPE_VIDEO_AD -> {
                holder as AdHolder
                holder.bind(data)
            }
            else -> {
                holder as VideoHolder
                holder.bind(mData[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (mData[position].type) {
            /* 0 -> {
                 VIEW_TYPE_ENTER
             }*/
            2 -> {
                VIEW_TYPE_VIDEO_AD
            }
            else -> {
                VIEW_TYPE_VIDEO
            }
        }

    }


    inner class EnterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val llRanking = itemView.find<View>(R.id.ll_ranking)
        private val llCate = itemView.find<View>(R.id.ll_cate)
        private val llRecharge = itemView.find<View>(R.id.ll_recharge)
        private val llRecommend = itemView.find<View>(R.id.ll_recommend)
        private val llNotice = itemView.find<View>(R.id.ll_notice)
        private val tvTitle = itemView.find<TextView>(R.id.tv_title)


        fun bind(title: String) {
            llRanking.click { ARouterManager.goVideoRankingActivity(it.context) }
            //  llCate.click { }
            llRecharge.click { ARouterManager.goRechargeActivity(it.context, "", 0, isBook = false) }
            llRecommend.click { ARouterManager.goVideoRecommendActivity(it.context) }
            llNotice.click { ARouterManager.goBulletinActivity(it.context) }
            tvTitle.text = title
        }
    }

    inner class AdHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iv_ad = itemView.find<ImageView>(R.id.iv_ad)
        private val tv_ad_title = itemView.find<TextView>(R.id.tv_ad_title)
        private val tv_ad_desc = itemView.find<TextView>(R.id.tv_ad_desc)
        fun bind(video: Video?) {
            video?.also {
                iv_ad.loadHtmlImg(it.thumbImg!!)
                tv_ad_title.text = video.name
                tv_ad_desc.text = video.content
                AdConfig.adPreview(it.adCallbackUrl!!)
            }
            itemView.click {
                AdConfig.adClick(itemView.context, video?.adClickUrl!!)
            }
        }
    }

    var onAddVideoClick: ((position: Int, video: Video) -> Unit)? = null
    var onItemClick: ((position: Int, video: Video) -> Unit)? = null
    var onItemShareClick: ((position: Int, video: Video, thumbUrl: String) -> Unit)? = null

    inner class VideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mPosition: Int = 0
        open val playerContainer = itemView.find<FrameLayout>(R.id.player_container)
        private val tvTitle = itemView.find<TextView>(R.id.tv_title)

        open val ivBlueThumb = itemView.find<ImageView>(R.id.iv_blue_thumb)
        open val prepareView = itemView.find<PrepareView>(R.id.prepare_view)
        private val tvCollectionNum = itemView.find<TextView>(R.id.tv_collection_num)
        private val tvWatchNum = itemView.find<TextView>(R.id.tv_watch_num)
        private val tvVideoPrice = itemView.find<TextView>(R.id.tv_video_price)
        private val btnCollection = itemView.find<TextView>(R.id.btn_collection)
        open val ivThumb = prepareView.findViewById<ImageView>(R.id.thumb)
        private val mTotalTime = prepareView.findViewById<TextView>(R.id.tv_video_times)
        private val tag_layout = itemView.findViewById<TagFlowLayout>(R.id.tag_layout)
        private val btn_recharge = itemView.findViewById<TextView>(R.id.btn_recharge)
        private val iv_share = itemView.findViewById<ImageView>(R.id.iv_share)

        fun bind(video: Video?, position: Int) {
            mPosition = position
            Log.d("tag", "item:${video.toString()},position:$mPosition")
            video?.apply {

                //广告
                if (thumbImg!!.contains("http")) {
                    ivBlueThumb.loadHtmlImg(thumbImg!!)
                    tvTitle.text = Html.fromHtml(name).trim()
                    prepareView.visibility = View.GONE
                    return
                }
                prepareView.visibility = View.VISIBLE
                val imageUrl = imageUrl(url!!, thumbImg!!)
                Log.d("tag", "imageUrl:${imageUrl}")
                //  ivThumb.loadImageScale(imageUrl, playerContainer)
                // val options: RequestOptions = RequestOptions()
                //         .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                // Glide.with(ivThumb.context).asGif().load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1605952974631&di=8d632765c03ecc8235f4fbadb31e76a7&imgtype=0&src=http%3A%2F%2Fpic.87g.com%2Fupload%2F2018%2F0905%2F20180905095730580.gif").apply(options).into(ivThumb)

                ivThumb.loadImageScale(imageUrl, playerContainer)
                ivBlueThumb.loadBlurCover(imageUrl)
                tvTitle.text = Html.fromHtml(name).trim()
                tvCollectionNum.text = favcounts
                tvWatchNum.text = playcounts
                mTotalTime.text = PlayerUtils.stringForTime((video_long!! * 1000))
                //  tvVideoPrice.text = price.toString()

                if (isFav == 1) {
                    btnCollection.isEnabled = false
                    btnCollection.setBackgroundResource(R.drawable.bg_join_my_video_cli)
                } else {
                    btnCollection.isEnabled = true
                    btnCollection.setBackgroundResource(R.drawable.bg_join_my_video)
                }

                if (video.tags!!.isEmpty()) {
                    tag_layout.visibility = View.GONE
                } else {
                    tag_layout.visibility = View.VISIBLE
                    tag_layout.adapter = object : TagAdapter<Tags?>(video?.tags) {
                        override fun getView(parent: FlowLayout, position: Int, t: Tags?): View? {
                            val textView = parent?.context?.inflate(R.layout.video_tag_layout, parent, false) as TextView
                            textView.text = t?.name
                            return textView
                        }
                    }
                }
            }


            btn_recharge.click {
                ARouterManager.goRechargeActivity(it.context, "", bookid = video?.id!!, isBook = false)
            }

            btnCollection.click {
                onAddVideoClick?.invoke(position, video!!)
            }
            /* playerContainer.click {
                 Log.d("tag","playerContainer点击了视频")
                 onItemClick?.invoke(position, video!!)
             }*/
            prepareView.click {
                Log.d("tag", "prepareView点击了视频")
                onItemClick?.invoke(position, video!!)
            }
            itemView.click {
                val videoInfoJson = Gson().toJson(video)
                ARouterManager.goVideoInfoActivity(it.context, videoInfoJson)
            }

            iv_share.click {
                onItemShareClick?.invoke(position, video!!, url!!)
            }

            itemView.tag = this
        }


        fun imageUrl(url: String, thumbImg: String): String {
            val suffix: String = thumbImg.substring(thumbImg.lastIndexOf(".") + 1)
            return "${url}${thumbImg}".replace(".${suffix}", ".html")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder.itemViewType == VIEW_TYPE_VIDEO) {
            val prepareView = holder.itemView.find<PrepareView>(R.id.prepare_view)
            val ivThumb = prepareView.findViewById<ImageView>(R.id.thumb)
            val ivBlueThumb = holder.itemView.find<ImageView>(R.id.iv_blue_thumb)
            if (ivThumb != null) {
                Glide.with(ivThumb.context).clear(ivThumb)
            }
            if (ivBlueThumb != null) {
                Glide.with(ivThumb.context).clear(ivBlueThumb)
            }
        }
    }
}
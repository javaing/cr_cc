package com.aliee.quei.mo.ui.video.adapter

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.Tags
import com.aliee.quei.mo.data.bean.Video
import com.aliee.quei.mo.ui.main.adapter.MainVideoAdapter
import com.aliee.quei.mo.ui.video.view.PrepareView
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import com.aliee.quei.mo.utils.extention.loadBlurCover
import com.aliee.quei.mo.utils.extention.loadImageScale
import com.bumptech.glide.Glide
import com.dueeeke.videoplayer.util.PlayerUtils
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout
import org.jetbrains.anko.find

class VideoListAdapter : RecyclerView.Adapter<VideoListAdapter.VideoHolder>() {
    private var mData = mutableListOf<Video>()

    fun setData(list: MutableList<Video>?) {
        list ?: return
        mData.clear()
        mData.addAll(list)
        notifyDataSetChanged()
    }

    fun getData():MutableList<Video>{
        return mData
    }

    fun loadMore(videos: MutableList<Video>) {
        mData.addAll(videos)
        notifyItemRangeChanged(mData.size, mData.size);
    }
    fun setCollectionStatus(position: Int) {
        val video = mData[position]
        video.isFav = 1
        notifyItemRangeChanged(position, mData.size)
    }
   override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: VideoHolder, position: Int) {
        holder.bind(mData[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoHolder {
        val view = parent.context.inflate(R.layout.item_video, parent, false)
        return VideoHolder(view)
    }


    inner class VideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mPosition: Int = 0
        open val playerContainer = itemView.find<FrameLayout>(R.id.player_container)
        private val tvTitle = itemView.find<TextView>(R.id.tv_title)

          private val ivBlueThumb = itemView.find<ImageView>(R.id.iv_blue_thumb)
        open val prepareView = itemView.find<PrepareView>(R.id.prepare_view)
        private val tvCollectionNum = itemView.find<TextView>(R.id.tv_collection_num)
        private val tvWatchNum = itemView.find<TextView>(R.id.tv_watch_num)
        private val tvVideoPrice = itemView.find<TextView>(R.id.tv_video_price)
        private val btnCollection = itemView.find<TextView>(R.id.btn_collection)
        private val ivThumb = prepareView.findViewById<ImageView>(R.id.thumb)
        private val tag_layout = itemView.findViewById<TagFlowLayout>(R.id.tag_layout)
        private val mTotalTime = prepareView.findViewById<TextView>(R.id.tv_video_times)
        private val iv_share = itemView.findViewById<ImageView>(R.id.iv_share)

        fun bind(video: Video?, position: Int) {
            mPosition = position
            video?.apply {
                val url = CommonDataProvider.instance.getVideoThumbDomain()
                val imageUrl = imageUrl(url, thumbImg!!)
                Log.d("tag", "imageUrl:${imageUrl}")
                ivThumb.loadImageScale(imageUrl,playerContainer)
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

            btnCollection.click {
                onAddVideoClick?.invoke(position, video!!)
            }
            playerContainer.click {
                onItemClick?.invoke(position, video!!)
            }
            iv_share.click {
                onItemShareClick?.invoke(position,video!!,CommonDataProvider.instance.getVideoThumbDomain())
            }
            itemView.tag = this
        }
    }

    var onAddVideoClick: ((position: Int, video: Video) -> Unit)? = null
    var onItemClick: ((position: Int, video: Video) -> Unit)? = null
    var onItemShareClick: ((position: Int, video: Video,thumbUrl:String) -> Unit)? = null

    fun imageUrl(url: String, thumbImg: String): String {
        val suffix: String = thumbImg.substring(thumbImg.lastIndexOf(".") + 1)
        Log.d("tag","suffix:${suffix}")
        return "${url}${thumbImg}".replace(".${suffix}", ".html")
    }

    override fun onViewRecycled(holder: VideoHolder) {
        super.onViewRecycled(holder)
         val ivBlueThumb = holder.itemView.find<ImageView>(R.id.iv_blue_thumb)
         val prepareView = holder.itemView.find<PrepareView>(R.id.prepare_view)
         val ivThumb = prepareView.findViewById<ImageView>(R.id.thumb)
        if (ivThumb!=null){
            Glide.with(ivThumb.context).clear(ivThumb)
        }
        if (ivBlueThumb!=null){
            Glide.with(ivThumb.context).clear(ivBlueThumb)
        }
    }
}
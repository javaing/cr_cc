package com.aliee.quei.mo.ui.search.adapter

import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.data.bean.SearchHistoryBean
import com.aliee.quei.mo.data.bean.Video
import com.aliee.quei.mo.ui.common.adapter.ComicLinearHolder
import com.aliee.quei.mo.utils.StringUtils
import com.aliee.quei.mo.utils.extention.*
import org.jetbrains.anko.find

class SearchVideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val searchRecordList = mutableListOf<SearchHistoryBean>()
    private val videos = mutableListOf<Video>()

    companion object {
        const val VIEW_TYPE_RECORD_TITLE = 0
        const val VIEW_TYPE_RECORD_ITEM = 1
        const val VIEW_TYPE_RECOMMEND = 2
        const val VIEW_TYPE_RECOMMEND_TITLE = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_RECORD_TITLE -> {
                val v = parent.context.inflate(R.layout.item_record_title, parent)
                return TitleHolder(v)
            }
            VIEW_TYPE_RECORD_ITEM -> {
                val v = parent.context.inflate(R.layout.item_search_record, parent)
                return SearchRecordHolder(v)
            }
            VIEW_TYPE_RECOMMEND_TITLE -> {
                val v = parent.context.inflate(R.layout.item_search_recommend_title, parent)
                return RecommendTitleHolder(v)
            }
            else -> {
                val v = parent.context.inflate(R.layout.item_video_guess, parent)
                return GuessLikeVideoHolder(v)
            }
        }
    }

    var recordDelClick: ((keyword: String) -> Unit)? = null
    var videoClick: ((bean: Video) -> Unit)? = null
    var recordClick: ((keyword: String) -> Unit)? = null
    var shiftClick: (() -> Unit)? = null

    private var recordTitleRange = 0
    private var recordItemRange = 0
    private var recommendTitleRange = 0
    private var recommendRange = 0
    override fun getItemCount(): Int {
        if (searchRecordList.isNotEmpty()) {
            recordTitleRange = 1
            recordItemRange = recordTitleRange + searchRecordList.size
        } else {
            recordTitleRange = 0
            recordItemRange = 0
        }
        recommendTitleRange = recordItemRange + 1
        recommendRange = recommendTitleRange + videos.size
        return recommendRange
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            VIEW_TYPE_RECORD_TITLE -> {
            }
            VIEW_TYPE_RECORD_ITEM -> {
                holder as SearchRecordHolder
                holder.bind(searchRecordList.getOrNull(position - recordTitleRange))
            }
            VIEW_TYPE_RECOMMEND_TITLE -> {
                holder as RecommendTitleHolder
            }
            VIEW_TYPE_RECOMMEND -> {
                holder as GuessLikeVideoHolder
                val bean = videos[position - recommendTitleRange]
                holder.bind(bean)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position < recordTitleRange) return VIEW_TYPE_RECORD_TITLE
        if (position < recordItemRange) return VIEW_TYPE_RECORD_ITEM
        if (position < recommendTitleRange) return VIEW_TYPE_RECOMMEND_TITLE
        return VIEW_TYPE_RECOMMEND
    }

    fun setSearchHistory(list: List<SearchHistoryBean>?) {
        list ?: return
        this.searchRecordList.clear()
        this.searchRecordList.addAll(list)
        notifyDataSetChanged()
    }

    fun setData(list: List<Video>?) {
        list ?: return
        this.videos.clear()
        this.videos.addAll(list)
        notifyDataSetChanged()
    }

    inner class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    inner class SearchRecordHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvKeyword = itemView.find<TextView>(R.id.keyword)
        val imgDel = itemView.find<ImageView>(R.id.del)
        fun bind(bean: SearchHistoryBean?) {
            bean ?: return
            tvKeyword.text = bean.keyword
            imgDel.click {
                recordDelClick?.invoke(bean.keyword)
            }
            tvKeyword.click {
                recordClick?.invoke(bean.keyword)
            }
        }
    }

    inner class RecommendTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.find<TextView>(R.id.shift)
                    .click {
                        shiftClick?.invoke()
                    }
        }
    }

    inner class GuessLikeVideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val videoThumb: ImageView = itemView.findViewById(R.id.iv_video_thumb)
        val videoTime: TextView = itemView.findViewById(R.id.tv_video_time)
        val videoDis: TextView = itemView.findViewById(R.id.tv_video_dis)
        val iv_start: ImageView = itemView.findViewById(R.id.iv_start)
        fun bind(video: Video) {
            val url = CommonDataProvider.instance.getVideoThumbDomain()
            if (video.thumbImg!!.contains("http")){
                videoThumb.loadHtmlImg(video.thumbImg!!)
                AdConfig.adPreview(video.adCallbackUrl!!)
                iv_start.gone()
                videoTime.gone()
            }else{
                val imageUrl = imageUrl(url,video!!.thumbImg!!)
                videoThumb.loadNovelCover(imageUrl)
                iv_start.show()
                videoTime.show()
            }

            videoDis.text = Html.fromHtml(video.name)
            videoTime.text = StringUtils.secToTime(video.video_long!!)

            itemView.click {
                videoClick?.invoke(video)
            }
        }
    }

    fun imageUrl(url: String, thumbImg: String): String {
        val suffix: String = thumbImg.substring(thumbImg.lastIndexOf(".") + 1)
        Log.d("tag","suffix:${suffix}")
        return "${url}${thumbImg}".replace(".$suffix", ".html")
    }
}
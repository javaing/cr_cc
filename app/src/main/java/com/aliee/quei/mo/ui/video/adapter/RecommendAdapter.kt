package com.aliee.quei.mo.ui.video.adapter

import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.HistoryBean
import com.aliee.quei.mo.data.bean.Video
import com.aliee.quei.mo.ui.main.adapter.MainVideoAdapter
import com.aliee.quei.mo.ui.video.view.PrepareView
import com.aliee.quei.mo.utils.StringUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.loadNovelCover
import com.aliee.quei.mo.utils.extention.show
import com.bumptech.glide.Glide
import org.jetbrains.anko.find

class RecommendAdapter : RecyclerView.Adapter<RecommendAdapter.VH>() {
    private var videos = mutableListOf<Video>()
    private var isEdit = false

    /**
     * 是否编辑
     *
     * @param b
     */
    fun setIsEdit(b: Boolean) {
        isEdit = b
        notifyDataSetChanged()
    }
    fun removeItem(id: Int?) {
        val bean : Video? = videos.find {
            it.id == id
        }
        videos.remove(bean)
        notifyDataSetChanged()
    }
    fun setData(items: MutableList<Video>) {
        if (items.isEmpty() && videos == null) {
            return
        }
        videos.clear()
        videos.addAll(items)
        notifyDataSetChanged()
    }

    fun loadMore(items: MutableList<Video>){
        videos.addAll(items)
        notifyDataSetChanged()
    }


    var removeClick : ((video : Video)->Unit)? = null
    var onItemClick : ((video : Video)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_guess, parent, false)
        return VH(view)

    }

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.apply {
            val video = videos[position]
            // var imageUrl = "${domain}${mVideoInfo!!.img_path}".replace(".jpeg",".html")
            val url = CommonDataProvider.instance.getVideoThumbDomain()
            val imageUrl = imageUrl(url!!, video!!.thumbImg!!)
            videoThumb.loadNovelCover(imageUrl)
            videoDis.text = Html.fromHtml(video.name)
            videoTime.text = StringUtils.secToTime(video.video_long!!)
            itemView.click {
                onItemClick?.invoke(video)
            }

            if (isEdit) {
                ivDel.show()
                ivDel.click {
                    removeClick?.invoke(video)
                }
            } else {
                ivDel.gone()
            }
        }
    }

    class VH(item: View) : RecyclerView.ViewHolder(item) {
        val videoThumb: ImageView = item.findViewById(R.id.iv_video_thumb)
        val videoTime: TextView = item.findViewById(R.id.tv_video_time)
        val videoDis: TextView = item.findViewById(R.id.tv_video_dis)
        val ivDel: ImageView = item.findViewById(R.id.ivDel)
    }

    fun imageUrl(url: String, thumbImg: String): String {
        val suffix: String = thumbImg.substring(thumbImg.lastIndexOf(".") + 1)
        return "${url}${thumbImg}".replace(".${suffix}", ".html")
    }

    override fun onViewRecycled(holder: VH) {
        super.onViewRecycled(holder)
        val videoThumb: ImageView = holder.itemView.findViewById(R.id.iv_video_thumb)
        if (videoThumb!=null){
            Glide.with(videoThumb.context).clear(videoThumb)
        }
    }

}


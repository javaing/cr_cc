package com.aliee.quei.mo.ui.video.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.bean.VideoBean
import com.aliee.quei.mo.ui.video.VideoDetailActivity
import com.aliee.quei.mo.utils.StringUtils
import com.aliee.quei.mo.utils.extention.loadHtmlImg
import com.aliee.quei.mo.utils.extention.loadNovelCover
import org.jetbrains.anko.textColor

class VideoGuessLikeAdapter : RecyclerView.Adapter<VideoGuessLikeAdapter.VH>() {
    var videos = mutableListOf<VideoBean>()

    var url: String ?=null


    fun setItem(items: MutableList<VideoBean>) {
        if (items.isEmpty() && videos == null) {
            return
        }
        videos.addAll(items)
        notifyDataSetChanged()
    }


    fun setImageDomain(url:String){
        this.url = url
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video_guess, parent, false)
        return VH(view)

    }

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.apply {
            val video = videos[position]
            // var imageUrl = "${domain}${mVideoInfo!!.img_path}".replace(".jpeg",".html")
            if (video!!.thumbImg.contains("http")){
                tv_ad_flag.visibility =View.VISIBLE
                iv_start.visibility =View.GONE
                videoTime.visibility = View.GONE
                videoDis.text = "${video.content}\n${video.name}"
                videoThumb.loadHtmlImg(video!!.thumbImg)
                AdConfig.adPreview(video!!.adCallbackUrl!!)
                videoDis.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                videoDis.textColor = Color.BLACK
            }else{
                val imageUrl  ="${url}${video!!.thumbImg}".replace(".jpeg",".html")
                videoThumb.loadNovelCover(imageUrl)
                tv_ad_flag.visibility =View.GONE
                videoTime.visibility =View.VISIBLE
                videoDis.text = Html.fromHtml(video.name)
                videoTime.text = StringUtils.secToTime(video.video_long)
                videoDis.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
                videoDis.textColor = itemView.context.resources.getColor(R.color.grey)
            }


            itemView.setOnClickListener {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(video)
                }
            }
        }
    }

    class VH(item: View) : RecyclerView.ViewHolder(item) {
        val videoThumb: ImageView = item.findViewById(R.id.iv_video_thumb)
        val videoTime: TextView = item.findViewById(R.id.tv_video_time)
        val videoDis: TextView = item.findViewById(R.id.tv_video_dis)
        val tv_ad_flag: TextView = item.findViewById(R.id.tv_ad_flag)
        val iv_start: ImageView = item.findViewById(R.id.iv_start)
    }


    private lateinit var onItemClickListener: OnItemClickListener
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(video: VideoBean)
    }




}
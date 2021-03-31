package com.aliee.quei.mo.ui.common.adapter

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.haozhang.lib.SlantedTextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.utils.ComicUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.loadNovelCover
import org.jetbrains.anko.find

class ComicLandImgHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivLand = itemView.find<ImageView>(R.id.ivLand)
    private val tagRight = itemView.find<SlantedTextView>(R.id.tagRight)
    private val tvStatus = itemView.find<TextView>(R.id.tvTagStatus)
    private val tvViewNum = itemView.find<TextView>(R.id.tvViewNum)
    private val tvStar = itemView.find<TextView>(R.id.tvStar)
    private val tvDescr = itemView.find<TextView>(R.id.tvDescr)
    private val tvTitle = itemView.find<TextView>(R.id.tvTitle)
    fun bind(bean: RecommendBookBean, itemClick: ((bean: RecommendBookBean) -> Unit)?) {
        Log.e("tag","ComicLandImgHolder---图片:${if (bean.thumb.isNullOrEmpty()) bean.bookcover else bean.thumb}----->：${bean.title}")
        ivLand.loadNovelCover(if (bean.thumb.isNullOrEmpty()) bean.bookcover else bean.thumb)
        tagRight.text = bean.tagText
        tvDescr.text = bean.desc
        tvTitle.text = bean.title
        if (bean.status == BeanConstants.STATUS_FINISH) {
            tvStatus.text = "完结"
            tvStatus.setBackgroundResource(R.mipmap.bg_tag_status_finish)
        } else {
            tvStatus.text = "连载"
            tvStatus.setBackgroundResource(R.mipmap.bg_tag_status_ongoing)
        }
        itemView.click {
            itemClick?.invoke(bean)
        }
        tvViewNum.text = ComicUtils.getReadNum(bean.id).toString()
        tvStar.text = ComicUtils.getCommentStar(bean.id)
    }
}
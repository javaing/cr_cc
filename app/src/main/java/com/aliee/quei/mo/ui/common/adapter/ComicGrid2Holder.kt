package com.aliee.quei.mo.ui.common.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.haozhang.lib.SlantedTextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.utils.ComicUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.loadNovelCover
import com.aliee.quei.mo.utils.extention.show
import org.jetbrains.anko.find

class ComicGrid2Holder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    private val ivCover = itemView.find<ImageView>(R.id.thumb)
    private val tvTitle = itemView.find<TextView>(R.id.title)
    private val category = itemView.find<TextView>(R.id.category)
    private val tagRight = itemView.find<SlantedTextView>(R.id.tagRight)
    private val tagStatus = itemView.find<TextView>(R.id.ivTagStatus)
    private val tvStar = itemView.find<TextView>(R.id.tvStar)
    private val tvReadNum = itemView.find<TextView>(R.id.tvViewNum)
    fun bind(bean: RecommendBookBean?, itemClick : ((bean : RecommendBookBean) -> Unit)?) {
        reset()
        bean?:return
        tagRight.gone()
        if (!bean.tagText.isNullOrEmpty()) {
            tagRight.show()
            tagRight.text = bean.tagText

            if (bean.tagColor != 0) {
                tagRight.setSlantedBackgroundColor(bean.tagColor)
            }
        }
        ivCover.loadNovelCover(bean.bookcover)
        tvTitle.text = bean.title
        category.text = bean.typename
        itemView.click {
            itemClick?.invoke(bean)
        }
        if (bean.status == BeanConstants.STATUS_FINISH) {
            tagStatus.text = "完结"
            tagStatus.setBackgroundResource(R.mipmap.bg_tag_status_finish)
        } else {
            tagStatus.text = "连载"
            tagStatus.setBackgroundResource(R.mipmap.bg_tag_status_ongoing)
        }
        tvStar.text = ComicUtils.getCommentStar(bean.id)
        tvReadNum.text = ComicUtils.getReadNum(bean.id).toString()
    }

    private fun reset () {
        ivCover.setImageResource(R.mipmap.img_default_cover)
        tvTitle.text = ""
        category.text = ""
        itemView.click {  }
    }
}
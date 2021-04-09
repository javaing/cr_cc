package com.aliee.quei.mo.ui.common.adapter

import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.haozhang.lib.SlantedTextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.utils.ComicUtils
import com.aliee.quei.mo.utils.extention.*
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor

class ComicGrid3Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivCover = itemView.find<ImageView>(R.id.thumb)
    private val tvTitle = itemView.find<TextView>(R.id.title)
    private val category = itemView.find<TextView>(R.id.category)
    private val tagRight = itemView.find<SlantedTextView>(R.id.tagRight)
    private val tagStatus = itemView.find<TextView>(R.id.ivTagStatus)
    private val tvStar = itemView.find<TextView>(R.id.tvStar)
    private val tvReadNum = itemView.find<TextView>(R.id.tvViewNum)
    private val tv_ad_desc = itemView.find<TextView>(R.id.tv_ad_desc)
    private val tv_ad_flag = itemView.find<TextView>(R.id.tv_ad_flag)
    private val ll_ad_num = itemView.find<LinearLayout>(R.id.ll_ad_num)
    fun bind(bean: RecommendBookBean?, itemClick: ((bean: RecommendBookBean) -> Unit)?) {
        reset()
        bean ?: return
        tagRight.gone()
        if (!bean.tagText.isNullOrEmpty()) {
            tagRight.show()
            tagRight.text = bean.tagText
            if (bean.tagColor != 0) {
                tagRight.setSlantedBackgroundColor(bean.tagColor)
            }
        }
        //Log.e("tag", "ComicGrid3Holder---图片:${if (bean.thumb.isNullOrEmpty()) bean.bookcover else bean.thumb}----->：${bean.title}")

        if (bean.bookcover!!.contains("http")) {
            ivCover.loadHtmlImg(bean.bookcover)
            tv_ad_desc.show()
            tv_ad_flag.show()
            ll_ad_num.gone()
            tagStatus.gone()
            tv_ad_desc.text = bean.desc
            tvTitle.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            tvTitle.textColor = itemView.context.resources.getColor(R.color.all_black)

            AdConfig.adPreview(bean.adCallbackUrl!!)
        } else {
            ll_ad_num.show()
            tagStatus.show()
            tv_ad_flag.gone()
            tv_ad_desc.gone()
            category.text = bean.typename

            if (bean.status == BeanConstants.STATUS_FINISH) {
                tagStatus.text = "完结"
                tagStatus.setBackgroundResource(R.mipmap.bg_tag_status_finish)
            } else {
                tagStatus.text = "连载"
                tagStatus.setBackgroundResource(R.mipmap.bg_tag_status_ongoing)
            }
            tvStar.text = ComicUtils.getCommentStar(bean.id)
            tvReadNum.text = ComicUtils.getReadNum(bean.id).toString()
            ivCover.loadNovelCover(bean.bookcover)
            tvTitle.textColor = itemView.getColor(R.color.theme_color)
            tvTitle.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)

        }
        itemView.click {
            itemClick?.invoke(bean)
        }
        tvTitle.text = bean.title
    }

    private fun reset() {
        ivCover.setImageResource(R.mipmap.img_default_cover)
        tvTitle.text = ""
        category.text = ""
        itemView.click { }
    }
}
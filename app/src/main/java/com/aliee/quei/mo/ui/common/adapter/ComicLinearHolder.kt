package com.aliee.quei.mo.ui.common.adapter

import android.graphics.Color
import android.graphics.Typeface
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.aliee.quei.mo.R
import com.aliee.quei.mo.config.AdConfig
import com.aliee.quei.mo.data.BeanConstants
import com.aliee.quei.mo.data.bean.ComicBookBean
import com.aliee.quei.mo.data.bean.RecommendBookBean
import com.aliee.quei.mo.router.ARouterManager
import com.aliee.quei.mo.utils.ComicUtils
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.loadHtmlImg
import com.aliee.quei.mo.utils.extention.loadNovelCover
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor

/**
 * Created by Administrator on 2018/4/28 0028.
 */
class ComicLinearHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val image = itemView.find<ImageView>(R.id.cover)
    val title = itemView.find<TextView>(R.id.bookTitle)
    val author = itemView.find<TextView>(R.id.author)
//    val readNow = itemView.find<TextView>(R.id.readNow)
    val category = itemView.find<TextView>(R.id.category)
    val description = itemView.find<TextView>(R.id.descr)
    val tvStar = itemView.find<TextView>(R.id.tvStar)
    val tvViewNum = itemView.find<TextView>(R.id.tvViewNum)
    val tvStatus = itemView.find<TextView>(R.id.tvTagStatus)
    val tv_ad_flag = itemView.find<TextView>(R.id.tv_ad_flag)

    fun bindComic(bean : ComicBookBean){
        image.loadNovelCover(bean.thumb)
        author.text = bean.author.toString()
        category.text = bean.typename
        title.text = bean.title
        description.text = bean.description
        itemView.click {
            ARouterManager.goComicDetailActivity(it.context,bean.id)
//            Log.d("ComicLinearHolder", "BookID:" + bean.id)
        }
        if (bean.status == BeanConstants.STATUS_FINISH) {
            tvStatus.text = "完结"
            tvStatus.setBackgroundResource(R.mipmap.bg_tag_status_finish)
        } else {
            tvStatus.text = "连载"
            tvStatus.setBackgroundResource(R.mipmap.bg_tag_status_ongoing)
        }

        tvStar.text = ComicUtils.getCommentStar(bean.id)
        tvViewNum.text = ComicUtils.getReadNum(bean.id).toString()
    }

    fun bindRecommend(bean: RecommendBookBean,itemClick : ((bean : RecommendBookBean) -> Unit)?) {
        val context =itemView.context
        Log.e("tag","ComicLinearHolder---图片:${if (bean.thumb.isNullOrEmpty()) bean.bookcover else bean.thumb}----->：${bean.title}")
        if (bean.bookcover!!.contains("http")){
            //广告
            image.loadHtmlImg(bean.bookcover)
            AdConfig.adPreview(bean.adCallbackUrl!!)
            author.visibility = View.GONE
            tvStatus.visibility = View.GONE
            category.visibility = View.GONE
            tvStar.visibility = View.GONE
            tvViewNum.visibility = View.GONE
            tv_ad_flag.visibility = View.VISIBLE
            title.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            description.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            title.textColor = Color.BLACK
            description.textColor = Color.BLACK
        }else{
            //漫画
            image.loadNovelCover(bean.bookcover)
            author.text = context.getString(R.string.author_with_label,bean.author.toString())
            category.text = bean.typename

            if (bean.status == BeanConstants.STATUS_FINISH) {
                tvStatus.text = "完结"
                tvStatus.setBackgroundResource(R.mipmap.bg_tag_status_finish)
            } else {
                tvStatus.text = "连载"
                tvStatus.setBackgroundResource(R.mipmap.bg_tag_status_ongoing)
            }
            tvStar.text = ComicUtils.getCommentStar(bean.id)
            tvViewNum.text = ComicUtils.getReadNum(bean.id).toString()

            author.visibility = View.VISIBLE
            tvStatus.visibility = View.VISIBLE
            category.visibility = View.VISIBLE
            tvStar.visibility = View.VISIBLE
            tvViewNum.visibility = View.VISIBLE
            tv_ad_flag.visibility = View.GONE
            title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            description.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            title.textColor = context.resources.getColor(R.color.theme_color)
            description.textColor = Color.parseColor("#ff999999")
        }

        title.text = bean.title
        description.text = bean.desc

        itemView.click {
           itemClick!!.invoke(bean)
        }

    }

}
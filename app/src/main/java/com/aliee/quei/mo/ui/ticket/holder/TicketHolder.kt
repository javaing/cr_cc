package com.aliee.quei.mo.ui.ticket.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.bean.TicketBean
import com.aliee.quei.mo.data.local.ReadStatBean
import com.aliee.quei.mo.data.local.TicketClaimRecord
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.disable
import com.aliee.quei.mo.utils.extention.inflate
import com.aliee.quei.mo.utils.extention.loadNovelCover
import io.realm.Realm
import org.jetbrains.anko.find

class TicketHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent : ViewGroup) : TicketHolder{
            val v = parent.context.inflate(R.layout.item_ticket,parent,false)
            return TicketHolder(v)
        }
    }

    private val ivCover = itemView.find<ImageView>(R.id.ivCover)
    private val tvDescr = itemView.find<TextView>(R.id.tvDescr)
    private val tvAmount = itemView.find<TextView>(R.id.tvAmount)
    private val tvTitle = itemView.find<TextView>(R.id.tvTitle)
    private val btnAction = itemView.find<TextView>(R.id.btnAction)
    private val tvFreeChapter = itemView.find<TextView>(R.id.tvFreeChapter)

    fun bind(bean : TicketBean,realm : Realm,itemClick :((bean : TicketBean,status : Int) -> Unit)? ) {
        ivCover.loadNovelCover(bean.thumb_x)
        tvDescr.text = "阅读推荐的人气漫画至第${bean.chapter_free}章"
        tvFreeChapter.text = " 可免费解锁至${bean.chapter_free}章"
        tvAmount.text = "+${bean.complete_reward} 金币"
        tvTitle.text = "《${bean.title}》"

        var status = 0  //0 领取 //1 去阅读 //2 领金币//3完成
        val claimRecord = realm.where(TicketClaimRecord::class.java)
            .equalTo("tid",bean.id)
            .findFirst()
        if (claimRecord == null) {
            status = 0
        } else if (claimRecord.status == 1) {
            status = 3
        } else {
            var chapterCount = 0
            realm.where(ReadStatBean::class.java)
                .equalTo("bookid",bean.cartoon_id)
                .findAll()?.map {
                    chapterCount += it.chapterCount
                }
            if (chapterCount >= 3) {
                status = 2
            } else {
                status = 1
            }
        }

        if (status ==0) {
            btnAction.text = "领取"
        } else if (status == 1) {
            btnAction.text = "去阅读"
        } else if (status == 2) {
            btnAction.text = "领金币"
        } else if (status == 3) {
            btnAction.text = "已完成"
            btnAction.disable()
        }


        btnAction.click{
//             ARouterManager.goComicDetailActivity(it.context,bean.cartoon_id)
//            it.context.toast("去阅读")
            itemClick?.invoke(bean,status)
        }
        itemView.click {
            itemClick?.invoke(bean,status)
        }
    }
}
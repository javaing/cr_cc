package com.aliee.quei.mo.ui.main.holder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.data.local.LoginRecordBean
import com.aliee.quei.mo.data.local.ReadStatBean
import com.aliee.quei.mo.data.local.TaskBean
import com.aliee.quei.mo.data.local.TaskRewardBean
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import com.aliee.quei.mo.utils.extention.toast
import io.realm.Realm
import org.jetbrains.anko.find
import java.util.*

class TaskHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    private val tvTitle = itemView.find<TextView>(R.id.tvTitle)
    private val tvAmount = itemView.find<TextView>(R.id.tvAmount)
    private val btnClaim = itemView.find<TextView>(R.id.btnClaim)
    private val tvStatus = itemView.find<TextView>(R.id.tvStatus)
    private val calendar = Calendar.getInstance()

    fun bind(
        taskBean: TaskBean,
        realm: Realm,
        claimClick: ((bean: TaskBean) -> Unit)?
    ) {
        tvTitle.text = taskBean.title
        tvAmount.text = "+${taskBean.reward}金币"
        var status = 0
        if (taskBean.type == 0) { //登录任务
            val loginDays = realm.where(LoginRecordBean::class.java).findAll()?.size?:0
            if (loginDays >= taskBean.desc) {
                tvStatus.text = "任务已完成，请点击领取"
                status = 1
            } else {
                tvStatus.text = "已登录${loginDays}天，还需登录${taskBean.desc - loginDays}天"
            }
        }
        if (taskBean.type == 1) { //阅读时长任务
            var duration = 0
            if (taskBean.period == 0) { //日任务
                (realm.where(ReadStatBean::class.java)
                    .equalTo("year",calendar.get(Calendar.YEAR))
                    .equalTo("month",calendar.get(Calendar.MONTH))
                    .equalTo("dayOfMonth",calendar.get(Calendar.DAY_OF_MONTH))
                    .findAll()?: mutableListOf<ReadStatBean>())
                    .forEach {
                        duration += it.duration
                    }

            }
            if (taskBean.period == 1) {//周任务
                (realm.where(ReadStatBean::class.java)
                    .equalTo("year",calendar.get(Calendar.YEAR))
                    .equalTo("weekOfYear",calendar.get(Calendar.WEEK_OF_YEAR))
                    .findAll()?: mutableListOf<ReadStatBean>())
                    .forEach {
                        duration += it.duration
                    }
            }
            if (duration >= taskBean.desc) {
                tvStatus.text = "任务已完成，请点击领取"
                status = 1
            } else {
                tvStatus.text = "已阅读${duration}分钟，还差${taskBean.desc - duration}分钟"
            }
        }
        if (taskBean.type == 2) { //阅读书本书
            var comicCount = 0
            if (taskBean.period == 0) { //日任务
                comicCount = (realm.where(ReadStatBean::class.java)
                    .equalTo("year",calendar.get(Calendar.YEAR))
                    .equalTo("month",calendar.get(Calendar.MONTH))
                    .equalTo("dayOfMonth",calendar.get(Calendar.DAY_OF_MONTH))
                    .distinct("bookid")
                    .findAll()?: mutableListOf<ReadStatBean>()).size
            }
            if (taskBean.period == 1) {
                comicCount = (realm.where(ReadStatBean::class.java)
                    .equalTo("year",calendar.get(Calendar.YEAR))
                    .equalTo("weekOfYear",calendar.get(Calendar.WEEK_OF_YEAR))
                    .distinct("bookid")
                    .findAll()?: mutableListOf<ReadStatBean>()).size
            }
            if (comicCount >= taskBean.desc) {
                tvStatus.text = "任务已完成，请点击领取"
                status = 1
            } else {
                tvStatus.text = "已阅读${comicCount}本，还差${taskBean.desc - comicCount}本"
            }
        }

        val record = realm.where(TaskRewardBean::class.java)
            .equalTo("taskId",taskBean.id)
            .findFirst()
        if (record != null) { //未领取任务
            val curYear = calendar.get(Calendar.YEAR)
            val curMonth = calendar.get(Calendar.MONTH)
            val curDay = calendar.get(Calendar.DAY_OF_MONTH)
            val curWeek = calendar.get(Calendar.WEEK_OF_YEAR)
            val claimDate = Date(record.time)
            val claimCalendar = Calendar.getInstance()
            claimCalendar.time = claimDate
            if (taskBean.period == 0) { //日任务
                if (curYear == claimCalendar.get(Calendar.YEAR) && curMonth == claimCalendar.get(Calendar.MONTH) && curDay == claimCalendar.get(Calendar.DAY_OF_MONTH)) {
                    status = 2
                }
            }
            if (taskBean.period == 1) { //周任务
                if (curYear == claimCalendar.get(Calendar.YEAR) && curWeek == claimCalendar.get(Calendar.WEEK_OF_YEAR)) {
                    status = 2
                }
            }
            if (taskBean.period == 2) { //新手任务
                status = 2
            }
        }

        if (status == 0) {
            btnClaim.setBackgroundResource(R.mipmap.bg_btn_claim_to)
            btnClaim.text = "待完成"
            btnClaim.click {}
        }
        if (status == 1) {
            btnClaim.setBackgroundResource(R.mipmap.bg_btn_claim)
            btnClaim.click {
                claimClick?.invoke(taskBean)
            }
            btnClaim.text = "可领取"
        }
        if (status == 2){
            btnClaim.setBackgroundResource(R.mipmap.bg_btn_claimed)
            btnClaim.text = "已领取"
            btnClaim.click {
                it.context.toast("已经领取过了哦")
            }
        }
    }

    companion object{
        fun create(parent : ViewGroup) : TaskHolder{
            val v = parent.context.inflate(R.layout.item_tasks,parent,false)
            return TaskHolder(v)
        }
    }
}
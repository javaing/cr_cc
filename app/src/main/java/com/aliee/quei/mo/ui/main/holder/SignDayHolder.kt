package com.aliee.quei.mo.ui.main.holder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.aliee.quei.mo.R
import com.aliee.quei.mo.utils.extention.gone
import com.aliee.quei.mo.utils.extention.inflate
import com.aliee.quei.mo.utils.extention.show
import org.jetbrains.anko.find
import java.util.*

class SignDayHolder private constructor(itemView : View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent : ViewGroup) : SignDayHolder {
            val v = parent.context.inflate(R.layout.item_sign_day,parent,false)
            return SignDayHolder(v)
        }

        val WEEK_DAYS = arrayOf(
            "星期一",
            "星期二",
            "星期三",
            "星期四",
            "星期五",
            "星期六",
            "星期日"
        )
    }

    private val btnSign = itemView.find<ViewGroup>(R.id.btnSign)
    private val tvDay = itemView.find<TextView>(R.id.tvWeek)
    private val tvCheckIn = itemView.find<TextView>(R.id.tvCheckin)
    private val ivChecked = itemView.find<ImageView>(R.id.ivCheckStatus)

    private val calendar = Calendar.getInstance()

    fun bind(hasSign : Boolean,position : Int) {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        var myDayOfWeek = dayOfWeek - 1
        if (myDayOfWeek == 0) myDayOfWeek = 7

        if (hasSign) {
            btnSign.setBackgroundResource(R.drawable.bg_btn_sign_day_signed)
            ivChecked.show()
            ivChecked.setImageResource(R.mipmap.ic_sign_checked)
            tvCheckIn.gone()
        } else {
            btnSign.setBackgroundResource(R.drawable.bg_btn_sign_day_sign)
            if (myDayOfWeek > position + 1) { //過去
                btnSign.setBackgroundResource(R.drawable.bg_btn_sign_day_missed)
                ivChecked.show()
                tvCheckIn.gone()
                ivChecked.setImageResource(R.mipmap.ic_sign_miss)
            } else {
                ivChecked.gone()
                tvCheckIn.show()
            }
        }
        if (myDayOfWeek > position + 1) { //過去

        }

        tvDay.text = WEEK_DAYS[position]
    }
}
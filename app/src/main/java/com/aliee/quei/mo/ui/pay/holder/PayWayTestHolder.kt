package com.aliee.quei.mo.ui.pay.holder

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.aliee.quei.mo.R
import com.aliee.quei.mo.component.CommonDataProvider
import com.aliee.quei.mo.data.bean.PayWayBean
import com.aliee.quei.mo.utils.extention.click
import com.aliee.quei.mo.utils.extention.inflate
import org.jetbrains.anko.find

class PayWayTestHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): PayWayTestHolder {
            val v = parent.context.inflate(R.layout.item_pay_way, parent, false)
            return PayWayTestHolder(v)
        }
    }

    private val icon = itemView.find<ImageView>(R.id.ivIcon)
    private val text = itemView.find<TextView>(R.id.text)
    fun bind(bean: PayWayBean, itemClick: ((bean: PayWayBean) -> Unit)? = null) {

        var payName = ""
        if (bean.sdk!!.contains("Weixin")) {
            payName = "微信支付"
            if(bean.icon_url.isNullOrEmpty()) {
                icon.setImageResource(R.mipmap.ic_pay_wechat)
            }else{
                Glide.with(icon)
                        .load(CommonDataProvider.instance.getImgDomain() + "/" + bean.icon_url)
                        .into(icon)
            }
        } else if (bean.sdk.contains("Alipay")) {
            payName = "支付宝"
            if(bean.icon_url.isNullOrEmpty()) {
                icon.setImageResource(R.mipmap.ic_pay_alipay)
            }else{
                Glide.with(icon)
                        .load(CommonDataProvider.instance.getImgDomain() + "/" + bean.icon_url)
                        .into(icon)
            }
        }else if (bean.sdk.contains("Ysfpay")){
            payName = "云闪付"
            if(bean.icon_url.isNullOrEmpty()) {
                icon.setImageResource(R.mipmap.ic_pay_unionpay)
            }else{
                Glide.with(icon)
                        .load(CommonDataProvider.instance.getImgDomain() + "/" + bean.icon_url)
                        .into(icon)
            }
        }

        text.text = payName+ bean.id
        //  text.text ="微信支付"
        itemView.click {
            itemClick?.invoke(bean)
        }
    }
}
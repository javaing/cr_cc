package com.aliee.quei.mo.data.bean

import com.aliee.quei.mo.R


data class ShareConfigBean(
		val icon : String?,
		val title : String?,
		val body : String?
)

data class ShelfHotBookConfig(
		val title : String,
		val bid : Long
)

data class PayWayBean(
	val id : Int,
	val sdk : String? = "Weixin",
	val name : String? = "微信支付",
	val appid : String? = "",
	var callBackDomain : String? = "",
	var icon_url : String? = ""
) {
	fun getPicRes() : Int {
		if (name?.contains("微信") == true)return R.mipmap.ic_pay_wechat
		if (name?.contains("支付宝") == true)return R.mipmap.ic_pay_alipay
		return R.mipmap.ic_pay_wechat
	}
}

data class ImgDomainConfig(
    val createTime: Long?, // 1559633215
    val domain: String?, // img3.sasqcw.com
    val enableTime: Long?, // 1559633282
    val explain: String?, // 帝联云
    val id: Int?, // 1
    val interceptTime: Long?, // null
    val platform: Int?, // 1
    val power: Int?, // 0
    val ssl: Int?, // 0
    val status: Int?, // 2
    val type: Int? // 1
)

data class VideoDomainConfig(
		val id: Int?, // 1
		val domain: String?, // img3.sasqcw.com
		val type: Int?, // 1
		val status: Int?, // 2
		val interceptTime: Long?, // null
		val explain: String?, // 帝联云
		val power: Int?, // 0
		val ssl: Int?, // 0
		val createTime: Long?, // 1559633215
		val enableTime: Long?, // 1559633282
		val platform: Int? // 1




)

data class H5ConfigBean(
		val	common_recharge_url : String?,
		val	vip_recharge_url : String?,
		val	my_message_url : String?,
		val	my_coupon_url : String?,
		val	redeem_url : String?,
		val	customer_url : String?,
		val	feedback_url : String?,
		val	welfare_url : String?,
		val	invite_url : String?,
		val	share_url : String?
)

data class PopupWindowBean(
		val image : String?,
		val url : String?,
		val delay : Int,
		val display : Int
)

data class LaunchAdBean(
		val image : String?,
		val url : String?,
		val duration : Int,
        val display : Int
)

data class MainTabBean(
		val id : Int,
		val title : String,
		val image : String?,
		val imageSel : String?,
		val url : String?,
		var type : Int
)
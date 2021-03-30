package com.aliee.quei.mo.data.bean

/**
 * Created by Administrator on 2018/4/20 0020.
 */

data class UserInfoBean(
        val bookBean: Int?,
        val vip: Int,
        val lastLoginTime: Long?,
        val linkid: Int?,
        val phone: String?,
        val refid: Int?,
        val registerTime: Long,
        var discountEndtime: Long,
        val uid: Int?,
        val username: String?,
        val vipEndtime: Long,
        val id: Int?,
        val tempUid: String,
        val password: String,
        val freetime:Int,
        var isRecharge:Int,
        var isVip:Int

) {
    override fun toString(): String {
        return "UserInfoBean(bookBean=$bookBean, vip=$vip, lastLoginTime=$lastLoginTime, linkid=$linkid, phone=$phone, refid=$refid, registerTime=$registerTime, discountEndtime=$discountEndtime, uid=$uid, username=$username, vipEndtime=$vipEndtime, id=$id, tempUid='$tempUid', password='$password', freetime=$freetime, isRecharge=$isRecharge, isVip=$isVip)"
    }
}

data class UserRecoverBean(
        val temp: Int,
        val uid: String?
)


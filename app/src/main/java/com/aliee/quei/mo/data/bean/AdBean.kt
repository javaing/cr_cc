package com.aliee.quei.mo.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdBean(
        val apiUrl: String,
        val close: Int,
        var count: Int,
        val groupid: Int,
        val id: Int,
        val interval: Int,
        val recharge: Int,
        var sec: Int,
        val status: Int,
        var user: Int,
        val vip: Int,
        val zid: Int
): Parcelable{
    override fun toString(): String {
        return "AdBean(apiUrl='$apiUrl', close=$close, count=$count, groupid=$groupid, id=$id, interval=$interval, recharge=$recharge, sec=$sec, status=$status, user=$user, vip=$vip, zid=$zid)"
    }
}
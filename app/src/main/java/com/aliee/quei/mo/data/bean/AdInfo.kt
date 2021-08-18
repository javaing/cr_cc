package com.aliee.quei.mo.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AdInfo(
        val callbackurl: String,
        val clickurl: String,
        val imgurl: String,
        var optionstr: String? = null,
        var title: String? = null,
        var desc: String? = null,
        var index: Int? = 0,
        var isShow: Int? = 0,
        var isClose: Int? = 0
) : Parcelable {
    var sec: Int? = 0
    override fun toString(): String {
        return "AdInfo(callbackurl='$callbackurl', clickurl='$clickurl', imgurl='$imgurl', optionstr=$optionstr, title=$title, desc=$desc, index=$index, isShow=$isShow, isClose=$isClose, sec=$sec)"
    }

}

fun AdInfo.checkLink():Boolean {
    if (clickurl!=null && callbackurl!=null && imgurl!=null) {
        return true
    }
    return false
}

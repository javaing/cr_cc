package com.aliee.quei.mo.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
        var content: String?=null,
        val favcounts: String?=null,
        val id: Int?=null,
        var isFav: Int?=null,
        var name: String?=null,
        val playcounts: String?=null,
        val price: Int?=null,
        val tags: List<Tags>?=null,
        val thumbHeight: String?=null,
        var thumbImg: String?=null,
        val thumbWidth: String?=null,
        val video_long: Int?=null,
        val video_path: String?=null,
        var adContentType :String?=null
):Parcelable {
    var type: Int = 0 //类型0.入口，1.小标题（视频圈子），2.(视频列表)
    var title: String = "视频圈子" //当type == 1时使用
    var adClickUrl:String?=""
    var adCallbackUrl:String?=""
    override fun toString(): String {
        return "Video(content=$content, favcounts=$favcounts, id=$id, isFav=$isFav, name=$name, playcounts=$playcounts, price=$price, tags=$tags, thumbHeight=$thumbHeight, thumbImg=$thumbImg, thumbWidth=$thumbWidth, video_long=$video_long, video_path=$video_path, type=$type, title='$title', adClickUrl=$adClickUrl, adCallbackUrl=$adCallbackUrl)"
    }


}

@Parcelize
data class Tag(
        val id: Int,
        val tag: String
) : Parcelable


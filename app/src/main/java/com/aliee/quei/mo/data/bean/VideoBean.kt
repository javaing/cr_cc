package com.aliee.quei.mo.data.bean

data class VideoBean(
    val content: String,
    val favcounts: String,
    val id: Int,
    val isFav: Int,
    val name: String,
    val playcounts: String,
    val price: Int,
    val tags: List<Tags>,
    val thumbHeight: String,
    val thumbImg: String,
    val thumbWidth: String,
    val video_long: Int,
    val video_path: String
){
    var adClickUrl:String?=""
    var adCallbackUrl:String?=""
}

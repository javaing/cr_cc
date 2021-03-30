package com.aliee.quei.mo.data.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class VideoInfo(
        val bookbean: Bookbean,
        val content: String,
        val favorite: Int,
        val freetime: Freetime,
        val fromsite: Any,
        val id: Int,
        val img_path: String,
        val isFav: Int,
        val iv: String,
        val key_path: String,
        val m3u8_path: String,
        val m3u8_secs: Int,
        val name: String,
        val price: Int,
        val tags: List<Tags>,
        val title_sec: Any,
        val usetype: String,
        val video_path: String,
        val video_size: String,
        val view: Int,
        val playcounts:String,
        val favcounts:String
)

data class Bookbean(
        val bookbean: Int
)

data class Freetime(
        val count: Int,
        val use: Int
)

@Parcelize
data class Tags(
        val id: Int,
        val name: String
): Parcelable
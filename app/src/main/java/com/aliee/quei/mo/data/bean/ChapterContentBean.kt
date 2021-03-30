package com.aliee.quei.mo.data.bean

import com.google.gson.annotations.SerializedName

data class ChapterContentBean(
    val book: ComicBookBean?,
    @SerializedName("bookBean")
    val book_bean: Int,
    val bookid: Int,
    val id: Int,
    val image: Object?,
    val name: String?,
    val sort: Int?,
    val thumb: String?,
    val nextid: Int?,
    val previd: Int?,
    var code: Int,
    var readmode:String? = null
)


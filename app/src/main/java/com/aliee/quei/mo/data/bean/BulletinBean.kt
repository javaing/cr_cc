package com.aliee.quei.mo.data.bean

data class BulletinBean (
        val tag:Int,
        val title:String,
        val content:String,
        val status:Int,
        val releaseDate:String,
        val imagePath : String? = null,
        val type:Int
)

data class BulletinDetailBean(
        val contentPageId:Int,
        val contentImagePath : String?,
        val contentBookId:Int
)
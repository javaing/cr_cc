package com.aliee.quei.mo.data.bean

data class BonusRecordBean(
    val createDate: Long?,
    val bean : Int?
)

data class BonusIDBean(
        val parentUid: Int,
        val uid : Int,
        val isTemp : Int,
        val shareUrl : String,
        val errmsg : String
)


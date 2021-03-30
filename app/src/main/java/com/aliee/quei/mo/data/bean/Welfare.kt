package com.aliee.quei.mo.data.bean

data class TicketBean(
    val cartoon_id: Int, // 2020
    val chapter_free: Int, // 10
    val expire_time: Long, // 1577758210
    val id: Int, // 2
    val thumb: String?, // http://cimg1.wyimmy.com/20181225/b65d6c5bd50dccec72f00b2c8d92a76f.jpg
    val thumb_x: String?, // http://cimg1.wyimmy.com/20190107/6d348612ffd8a4f7e1e4088f14141bc8.jpg
    val title: String?, // Woodman Dyeon
    val complete_reward : Int
)


data class CheckInStatsBean(
    val time : Long,
    val book_bean : Int,
    val day : Int,
    val status : Int,
    val weekday : Int
)

data class WelfareCoinRecordBean(
    val id : Long,
    val istemp : Int,
    val uid : Int,
    val bookbean : Int,
    val event : String?,
    val created : Long
)

data class UserLevelBean(
    val level : Int,
    val diff : Int,
    val next_total : Int,
    val level_total : Int,
    val total : Int
)

data class TotalIncomeBean(
    val count : Int
)

data class CoinBean(
    val bookbean: Int
)

data class SignAdBean(
    val bookbean : String?,
    val title : String?,
    val content : String?
)

data class WeixinAdBean(
    val bookbean : String?,
    val title : String?,
    val content : String?,
    val wxNumber : String?
)

data class WeixinAttentionBean(
    val isRecivable: Boolean
)

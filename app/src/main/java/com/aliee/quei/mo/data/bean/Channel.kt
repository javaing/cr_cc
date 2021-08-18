package com.aliee.quei.mo.data.bean

data class ChannelInfoBean(
    val apk_url: String?, // null
    val cartoon_id: Int, // 2123
    val chapter_id: Int, // 4645
    val created: Long, // 1552104389
    val description: String?, // CPS测试渠道
    val id: Int?, // 78
    val install_app: Int, // 0
    val lang: Int, // 1
    val refid: Int, // 1
    val version: String? // null
)


data class ChannelHideBean(
    val id: Int, // 17,32
    val hideVideo: Int, // 1
)
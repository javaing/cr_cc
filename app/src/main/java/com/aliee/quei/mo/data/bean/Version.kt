package com.aliee.quei.mo.data.bean

data class VersionInfoBean(
        var version: String?,
        var descr: String?,
        var url: String?,
        var descImgUrl: String? = null,
        var button: String? = null,
        val isForce: Int,
        val userInfo: UserInfoBean
)


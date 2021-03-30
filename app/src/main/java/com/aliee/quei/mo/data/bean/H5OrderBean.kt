package com.aliee.quei.mo.data.bean

data class H5OrderBean(
        val orderId : String?,
        val payurl : String?,
        val paymode : Int,
        var referer : String? = ""
)

data class H5PayUrlBean(
        val payurl : String?
)

data class H5PayResultBean(
        val status : Int
)
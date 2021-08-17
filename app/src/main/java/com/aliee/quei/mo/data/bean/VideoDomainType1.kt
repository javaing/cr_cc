package com.aliee.quei.mo.data.bean

import com.aliee.quei.mo.net.ApiConstants
import com.google.gson.annotations.SerializedName

data class VideoDomainType1(

    @SerializedName(ApiConstants.VIDEO_TYPESTRING)
    val `20`: List<Domain>
)

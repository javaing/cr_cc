package com.aliee.quei.mo.data.bean

import com.google.gson.annotations.SerializedName

data class VideoDomainType(
    @SerializedName("20")
    val `20`: List<Domain>
)

data class Domain(
    val base64: Any,
    val create_time: Any,
    val domain: String,
    val enable_time: Any,
    val expiration_time: Any,
    val explain: String,
    val id: Int,
    val intercept_time: Any,
    val platform: Int,
    val power: Int,
    val ssl: Int,
    val status: Int,
    val type: Int
)
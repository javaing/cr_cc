package com.aliee.quei.mo.data.bean

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Option(
        var title: String,
        val desc: String
) : Parcelable
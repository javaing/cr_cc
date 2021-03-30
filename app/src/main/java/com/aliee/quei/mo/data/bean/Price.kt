package com.aliee.quei.mo.data.bean

import com.aliee.quei.mo.component.GsonProvider

data class PriceBean(
    val id: Int,
    val bean: Int,
    val default: Int,
    val description: String?,
    val isVip: Int,
    val lang: Int,
    val price: Float,
    val sort: Int,
    val vipDay: Int,
    val vipName: String?,
    private val tag: String,
    val realBean: Int,
    val givePrice: Int,
    val giveBean: Int,
    val isHot:Int,
    val isDefault:Int,
    val isRepeat:Int,
    val isPromote:Int
) {


    //		"realBean": 20000 ,  中间黑字
    //		"givePrice": 200,    多送的
    //		"giveBean": 20000    赠送的金币，最底部


    fun getTag(): PriceTagBean? {
        return try {
            GsonProvider.gson.fromJson<PriceTagBean>(tag, PriceTagBean::class.java)
        } catch (e: Exception) {
            null
        }
    }
}

data class PriceTagBean(val label: String, val icon: String)


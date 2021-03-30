package com.aliee.quei.mo.analyze.bean

/**
 * Created by liyang on 2018/6/13 0013.
 */
data class RechargeAnalyzeBean(
        var uid : Int? = null,
        var novelId : Long? = null,
        var chapterId : Long? = null,
        var price : Float? = null,
        var itemId : Long? = null,
        var entrance : String? = null,
        var giftId : Long? = null,
        var result : String? = null,
        var payMethod : String? = null
) {
    fun toMap(): Map<String,String> {
        return mapOf(
                "id" to uid.toString(),
                "novelId" to novelId.toString(),
                "chapterId" to chapterId.toString(),
                "price" to price.toString(),
                "entrance" to entrance.toString(),
                "itemId" to itemId.toString(),
                "giftId" to giftId.toString(),
                "result" to result.toString(),
                "payMethod" to payMethod.toString()
        )
    }
}